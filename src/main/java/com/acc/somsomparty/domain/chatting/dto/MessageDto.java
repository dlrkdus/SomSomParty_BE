package com.acc.somsomparty.domain.chatting.dto;


import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MessageDto(
        Long chatRoomId,
        String userName,
        LocalDateTime createdAt,
        String content
) {
}
