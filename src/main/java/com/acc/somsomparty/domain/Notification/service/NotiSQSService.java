package com.acc.somsomparty.domain.Notification.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotiSQSService {
    private Logger logger = LoggerFactory.getLogger(NotiSQSService.class);

    private final NotificationService notificationService;
    private final SqsAsyncClient sqsAsyncClient;

    @Value("${aws.noti.sqs.queue-url}")
    private String queueUrl;

    Set<String> processedMessageIds = new HashSet<>();

    // 알림 SQS 메시지 처리
    @SqsListener("${aws.noti.sqs.queue-name}")
    public void receiveMessage(Message message) {
        logger.info("Received message: " + message.body());
        logger.info("Receipt Handle: " + message.receiptHandle());

        // 메시지 중복 처리 방지
        if (processedMessageIds.contains(message.messageId())) {
            logger.info("Duplicate message detected: " + message.messageId());
            return;
        }
        processedMessageIds.add(message.messageId());

        try {
            // 알림 전송
            notificationService.setAWSSNSForTomorrowFestival();
            // 메시지 삭제
            deleteMessage(message.receiptHandle());
        } catch (Exception e) {
            logger.error("Error processing message: " + e.getMessage(), e);
        }
    }

    // 알림 SQS에서 메시지 삭제
    private void deleteMessage(String receiptHandle) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsAsyncClient.deleteMessage(deleteMessageRequest)
                .thenRun(() -> logger.info("Message successfully deleted from queue. ReceiptHandle: {}, QueueUrl: {}", receiptHandle, queueUrl))
                .exceptionally(throwable -> {
                    logger.error("Failed to delete message: " + throwable.getMessage());
                    return null;
                });
    }
}
