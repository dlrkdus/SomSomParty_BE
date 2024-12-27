package com.acc.somsomparty.domain.User.service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

import java.util.Map;

public interface UserService {
    void signUp(String email, String password);
    void confirmSignUp(String email, String name, String confirmationCode);
    Map<String, Object> login(String email, String password);
    void logout(String accessToken);
    boolean verifyToken(String token);
    AdminInitiateAuthResponse refreshAccessToken(String refreshToken, String userName);
}
