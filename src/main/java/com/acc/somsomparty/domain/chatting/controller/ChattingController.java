package com.acc.somsomparty.domain.chatting.controller;

import com.acc.somsomparty.domain.chatting.dto.MessageDto;
import com.acc.somsomparty.domain.chatting.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChattingController {
    private final KafkaProducerService kafkaProducerService;

    @MessageMapping("/chat.send")
    public void sendMessage(MessageDto message) {
        kafkaProducerService.sendMessage("chat-topic", "chatRoomId"+message.chatRoomId().toString(), message);
    }
}
