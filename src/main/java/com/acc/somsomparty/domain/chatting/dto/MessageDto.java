package com.acc.somsomparty.domain.chatting.dto;


import lombok.Builder;

@Builder
public record MessageDto(
        Long chatRoomId,
        String userName,
        String content
) {
}
