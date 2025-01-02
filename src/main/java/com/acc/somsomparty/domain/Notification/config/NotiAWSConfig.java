package com.acc.somsomparty.domain.Notification.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Getter
@Configuration
public class NotiAWSConfig {
    @Value("${aws.noti.region}")
    private String notiRegion;

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(notiRegion))
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(notiRegion))
                .build();
    }
}
