package com.acc.somsomparty.domain.Notification.service;

import com.acc.somsomparty.domain.Notification.config.NotiAWSConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Service
@AllArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    public SnsClient getSnsClient() {
        return SnsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create()) // IAM 역할 기반 인증
                .region(Region.AP_NORTHEAST_2) // AWS Region 설정
                .build();
    }
}
