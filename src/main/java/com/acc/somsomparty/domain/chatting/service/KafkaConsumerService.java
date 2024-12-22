package com.acc.somsomparty.domain.chatting.service;

import com.acc.somsomparty.domain.chatting.dto.MessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate; // STOMP 메시지 전송 객체

    /**
     * @param record: @KafkaListener 의 기본 동작 방식에서는 메서드 파라미터로 메시지와 Key 를 직접 분리해서 읽을 수 없다.
     *                대신, Kafka 의 ConsumerRecord 를 활용하면 메시지와 Key 를 동시에 처리할 수 있다.
     */
    @KafkaListener(topics = "chat-topic", groupId = "consumer-group-1")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        String message = record.value();
        log.info("Kafka 메시지 수신 - Key: {}, 메시지: {}", key, message);

        MessageDto chatMessage = parseMessage(message);

        if (chatMessage == null) {
            log.warn("잘못된 메시지를 무시합니다.");
            return;
        }

        // WebSocket 구독자들에게 메시지 전달
        messagingTemplate.convertAndSend("/topic/chat/" + key, chatMessage);
        log.info("WebSocket 구독자들에게 메세지 전달 완료");
    }

    private MessageDto parseMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(message, MessageDto.class);
        } catch (Exception e) {
            log.error("Kafka 메시지 역직렬화 실패 - 메시지: {}", message, e);
            return null;
        }
    }
}
