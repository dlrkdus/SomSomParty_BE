package com.acc.somsomparty.domain.chatting.dto;

import lombok.Builder;

@Builder
public record UserChatRoomListDto(
        Long id,
        String title,
        Long userCount,
        Integer unReadCount
) {
}
