package com.acc.somsomparty.domain.chatting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MessageDto(
        @NotNull
        Long chatRoomId,
        Long senderId,
        String senderName,
        String content,
        Long sendTime
) {
}
