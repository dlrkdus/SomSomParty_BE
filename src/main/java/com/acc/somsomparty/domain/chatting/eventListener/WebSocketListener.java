package com.acc.somsomparty.domain.chatting.eventListener;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketListener {

    private final RedisTemplate<String, Object> redisTemplate;

    // WebSocket 연결 시
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getNativeHeader("userId").get(0); // 첫 번째 값 가져오기
        String chatRoomId = headerAccessor.getNativeHeader("chatRoomId").get(0);

        // Redis 에 접속 상태 저장
        String redisKey = "chatRoom:activeUsers:" + chatRoomId;
        redisTemplate.opsForSet().add(redisKey, userId);
        redisTemplate.expire(redisKey, Duration.ofHours(1));
        resetUnreadCount(chatRoomId, userId);

        // WebSocket 세션 속성에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        log.info("User {} 가 채팅방 {} 에 접속했습니다.", userId, chatRoomId);
    }

    // WebSocket 연결 해제 시
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String chatRoomId = (String) headerAccessor.getSessionAttributes().get("chatRoomId");

        // Redis 에서 접속 상태 제거
        String redisKey = "chatRoom:activeUsers:" + chatRoomId;
        redisTemplate.opsForSet().remove(redisKey, userId);
        log.info("User {} 가 채팅방 {} 에서 나갔습니다.", userId, chatRoomId);
    }

    private void resetUnreadCount(String chatRoomId, String userId) {
        String unreadCountKey = "chatRoom:unreadCount";
        String redisField = chatRoomId + ":" + userId;

        // Redis 에서 읽지 않은 메시지 개수 초기화
        redisTemplate.opsForHash().put(unreadCountKey, redisField, 0);
        log.info("User {} 의 안 읽은 메세지 개수 초기화.", userId);
    }
}