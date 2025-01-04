package com.acc.somsomparty.domain.chatting.config;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

@Configuration
@EnableKafka
public class KafkaConfig {
    // 운영 환경에서 브로커 추가시 주소 추가해주면 됨
    private final String KAFKA_BROKER =
            "b-1.sommsk.0hsy6v.c4.kafka.ap-northeast-2.amazonaws.com:9098,"+
            "b-2.sommsk.0hsy6v.c4.kafka.ap-northeast-2.amazonaws.com:9098";

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        config.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
        config.put(SaslConfigs.SASL_JAAS_CONFIG,"software.amazon.msk.auth.iam.IAMLoginModule required awsProfileName=\"somparty_EC2_IAM\";");
        config.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-1"); // Consumer 그룹 ID
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 가장 처음부터 메시지 읽기
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        config.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
        config.put(SaslConfigs.SASL_JAAS_CONFIG,"software.amazon.msk.auth.iam.IAMLoginModule required awsProfileName=\"somparty_EC2_IAM\";");
        config.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 병렬로 처리할 쓰레드 수 설정
        return factory;
    }

    @PostConstruct
    public void createTopic() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        config.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
        config.put(SaslConfigs.SASL_JAAS_CONFIG,"software.amazon.msk.auth.iam.IAMLoginModule required awsProfileName=\"somparty_EC2_IAM\";");
        config.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

        try (AdminClient adminClient = AdminClient.create(config)) {
            String topicName = "chat-topic";
            int partitions = 3; // 파티션 개수
            short replicationFactor = 2; // 복제본 개수

            // 토픽 존재 여부 확인
            boolean topicExists = adminClient.listTopics().names().get().contains(topicName);
            if (!topicExists) {
                // 토픽 생성
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                System.out.println("토픽 생성 성공: " + topicName);
            } else {
                System.out.println("토픽이 이미 존재합니다: " + topicName);
            }
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("토픽 생성 실패: " + e.getMessage());
        }
    }
}