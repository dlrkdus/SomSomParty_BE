package com.acc.somsomparty.domain.User.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoConfig {

    @Bean
    public CognitoIdentityProviderClient cognitoClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.AP_NORTHEAST_2) // AWS Region 설정
                .credentialsProvider(DefaultCredentialsProvider.create()) // IAM 역할 기반 인증
                .build();
    }
}