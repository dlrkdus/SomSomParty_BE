package com.acc.somsomparty.domain.Notification.service;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

public interface CredentialService {
    SnsClient getSnsClient();
}
