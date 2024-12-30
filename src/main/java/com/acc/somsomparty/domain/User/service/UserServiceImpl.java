package com.acc.somsomparty.domain.User.service;

import com.acc.somsomparty.domain.User.entity.User;
import com.acc.somsomparty.domain.User.enums.Role;
import com.acc.somsomparty.domain.User.repository.UserRepository;
import com.acc.somsomparty.global.exception.CustomException;
import com.acc.somsomparty.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CognitoIdentityProviderClient cognitoClient;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${aws.auth.cognito.user-pool-id}")
    private String userPoolId;

    @Value("${aws.auth.cognito.app-client-id}")
    private String appClientId;

    @Value("${aws.auth.cognito.client-secret}")
    private String clientSecret;

    // Cognito의 Secret Hash 계산
    private String calculateSecretHash(String clientId, String clientSecret, String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            logger.error("Error while calculating secret hash", e);
            throw new RuntimeException("Error while calculating secret hash", e);
        }
    }

    // 이메일을 통해 Cognito에서 사용자 이름(username)을 가져옴
    private String getCognitoUsername(String email) {
        try {
            AdminGetUserRequest request = AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .build();

            AdminGetUserResponse response = cognitoClient.adminGetUser(request);
            return response.username();
        } catch (UserNotFoundException e) {
            logger.error("User not found in Cognito with email {}", email, e);
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error while fetching Cognito username for email {}", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 회원가입 처리
    public void signUp(String email, String password) {
        try {
            String secretHash = calculateSecretHash(appClientId, clientSecret, email);
            SignUpRequest request = SignUpRequest.builder()
                    .clientId(appClientId)
                    .username(email)
                    .password(password)
                    .secretHash(secretHash)
                    .build();

            cognitoClient.signUp(request);
            logger.info("User {} signed up successfully", email);
        } catch (UsernameExistsException e) {
            logger.warn("User {} already exists", email, e);
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during signUp for user {}", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 회원가입 확인 (이메일 인증 코드 확인)
    public void confirmSignUp(String email, String name, String confirmationCode) {
        try {
            String secretHash = calculateSecretHash(appClientId, clientSecret, email);

            ConfirmSignUpRequest request = ConfirmSignUpRequest.builder()
                    .clientId(appClientId)
                    .username(email)
                    .confirmationCode(confirmationCode)
                    .secretHash(secretHash)
                    .build();

            String username = getCognitoUsername(email);

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .build();

            userRepository.save(user);
            logger.info("User {} saved to repository successfully with username {}", email, username);

            cognitoClient.confirmSignUp(request);
            logger.info("User {} confirmed sign up successfully", email);
        } catch (CodeMismatchException e) {
            logger.warn("Code mismatch for user {}", email, e);
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (ExpiredCodeException e) {
            logger.warn("Confirmation code expired for user {}", email, e);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during confirmSignUp for user {}", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 로그인 처리 및 Access Token과 Refresh Token 반환
    public Map<String, Object> login(String email, String password) {
        try {
            String secretHash = calculateSecretHash(appClientId, clientSecret, email);

            Map<String, String> authParameters = new HashMap<>();
            authParameters.put("USERNAME", email);
            authParameters.put("PASSWORD", password);
            authParameters.put("SECRET_HASH", secretHash);

            AdminInitiateAuthRequest request = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .userPoolId(userPoolId)
                    .clientId(appClientId)
                    .authParameters(authParameters)
                    .build();

            AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(request);

            logger.info("User {} logged in successfully", email);

            String username = getCognitoUsername(email);

            Map<String, Object> result = new HashMap<>();
            result.put("authResponse", response);
            result.put("username", username);

            return result;
        } catch (NotAuthorizedException e) {
            logger.warn("Unauthorized access attempt for user {}", email, e);
            if (e.getMessage() != null && e.getMessage().contains("User is not confirmed")) {
                throw new CustomException(ErrorCode.USER_NOT_CONFIRMED);
            }
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } catch (UserNotFoundException e) {
            logger.warn("User {} not found", email, e);
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during login for user {}", email, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 로그아웃 처리
    public void logout(String accessToken) {
        try {
            GlobalSignOutRequest request = GlobalSignOutRequest.builder()
                    .accessToken(accessToken)
                    .build();

            cognitoClient.globalSignOut(request);
            logger.info("User with access token logged out successfully");
        } catch (InvalidParameterException e) {
            logger.warn("Invalid parameter during logout", e);
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during logout", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 토큰 유효성 검증
    public boolean verifyToken(String token) {
        try {
            GetUserRequest request = GetUserRequest.builder()
                    .accessToken(token)
                    .build();

            cognitoClient.getUser(request);
            logger.info("Token verification succeeded for access token");
            return true;
        } catch (NotAuthorizedException e) {
            logger.warn("Unauthorized token access", e);
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during token verification", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // Refresh Token을 사용하여 Access Token 갱신
    public AdminInitiateAuthResponse refreshAccessToken(String refreshToken, String userName) {
        try {
            String secretHash = calculateSecretHash(appClientId, clientSecret, userName);

            Map<String, String> authParameters = new HashMap<>();
            authParameters.put("REFRESH_TOKEN", refreshToken);
            authParameters.put("SECRET_HASH", secretHash);

            AdminInitiateAuthRequest request = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .clientId(appClientId)
                    .userPoolId(userPoolId)
                    .authParameters(authParameters)
                    .build();

            logger.info("Access token refreshed successfully");
            return cognitoClient.adminInitiateAuth(request);
        } catch (NotAuthorizedException e) {
            logger.warn("Unauthorized refresh token access", e);
            if (e.getMessage() != null && e.getMessage().contains("Refresh Token has expired")) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (CognitoIdentityProviderException e) {
            logger.error("Error during access token refresh", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // id 가져오기
    public Long getIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = null;
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }

        if (username == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else {
            User user = userRepository.getUserByUsername(username);
            logger.info("user info : {}", user.toString());
            return user.getId();
        }
    }
}