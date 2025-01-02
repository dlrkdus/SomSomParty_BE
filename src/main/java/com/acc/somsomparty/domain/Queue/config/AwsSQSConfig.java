package com.acc.somsomparty.domain.Queue.config;

import static software.amazon.awssdk.regions.Region.AP_NORTHEAST_2;

import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Import(SqsBootstrapConfiguration.class)
@Slf4j
@Configuration
public class AwsSQSConfig {

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create()) // IAM 역할 기반 인증
                .region(Region.of(String.valueOf(AP_NORTHEAST_2))) // 환경 변수에서 AWS Region 읽기
                .build();
    }

    @Bean
    SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .configure(options -> options
                        .acknowledgementMode(AcknowledgementMode.MANUAL) // 명시적으로 acknowledgement를 해야 메시지가 삭제됨
                        .acknowledgementOrdering(AcknowledgementOrdering.ORDERED) // 순서대로 처리
                )
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }
}