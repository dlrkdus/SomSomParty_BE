package com.acc.somsomparty.domain.chatting.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class DynamoDBConfig {


    @Value("${spring.aws.dynamodb.endpoint:http://localhost:8000}")
    private String dynamoDbEndpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}