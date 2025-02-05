package com.acc.somsomparty.domain.chatting.entity;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Message {

    private String content;  // 메시지 내용
    private Long senderId;  // 보낸 사람 ID
    private String senderName; // 보낸 사람 이름
    private Long chatRoomId; // 채팅방 ID
    private Long sendTime;

    @DynamoDbPartitionKey
    public Long getChatRoomId() {
        return chatRoomId;
    }

    @DynamoDbSortKey
    public Long getSendTime() {
        return sendTime;
    }

}
