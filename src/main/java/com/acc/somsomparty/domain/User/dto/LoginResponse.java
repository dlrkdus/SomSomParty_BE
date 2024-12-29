package com.acc.somsomparty.domain.User.dto;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    private String userName;

    public static LoginResponse fromAdminInitiateAuthResponse(AdminInitiateAuthResponse response, String userName) {
        if (response == null || response.authenticationResult() == null) {
            throw new IllegalArgumentException("Invalid AdminInitiateAuthResponse");
        }

        return LoginResponse.builder()
                .accessToken(response.authenticationResult().accessToken())
                .idToken(response.authenticationResult().idToken())
                .refreshToken(response.authenticationResult().refreshToken())
                .expiresIn(response.authenticationResult().expiresIn())
                .tokenType(response.authenticationResult().tokenType())
                .userName(userName)
                .build();
    }
}
