package com.acc.somsomparty.domain.User.dto;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

@Getter
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private Integer expiresIn;
    private String tokenType;

    public static RefreshTokenResponse fromAdminInitiateAuthResponse(AdminInitiateAuthResponse response) {
        if (response == null || response.authenticationResult() == null) {
            throw new IllegalArgumentException("Invalid AdminInitiateAuthResponse");
        }

        return RefreshTokenResponse.builder()
                .accessToken(response.authenticationResult().accessToken())
                .expiresIn(response.authenticationResult().expiresIn())
                .tokenType(response.authenticationResult().tokenType())
                .build();
    }
}