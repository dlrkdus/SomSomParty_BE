package com.acc.somsomparty.domain.Notification.service;

import com.acc.somsomparty.domain.Festival.entity.Festival;
import com.acc.somsomparty.domain.Notification.entity.FcmToken;
import com.acc.somsomparty.domain.Notification.enums.TokenState;
import com.acc.somsomparty.domain.Notification.projection.FestivalTokenProjection;
import com.acc.somsomparty.domain.Notification.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @Value("${aws.noti.sns.application-arn}")
    private String arn;

    private final FcmTokenRepository fcmTokenRepository;
    private final SNSService snsService;

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private List<FestivalTokenProjection> getTokensForTomorrowFestival() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<FestivalTokenProjection> tokens = fcmTokenRepository.findTokensForTomorrowFestival(tomorrow);

        if (tokens.isEmpty()) {
            logger.info("No festivals scheduled for tomorrow or no FCM tokens found.");
        } else {
            logger.info("Found {} festivals with FCM tokens for tomorrow.", tokens.size());
        }
        return tokens;
    }

    // festival 알림을 위한 aws sns 설정
    public void setAWSSNSForTomorrowFestival() {
        logger.info("Starting AWS SNS notification setup for tomorrow's festivals.");

        try {
            List<FestivalTokenProjection> results = getTokensForTomorrowFestival();

            if (results.isEmpty()) {
                logger.info("No FCM tokens available for festival notifications. Skipping notification setup.");
                return;
            }

            for (FestivalTokenProjection projection : results) {
                String token = projection.getFcmToken();
                TokenState tokenState = projection.getTokenState();
                Festival festival = projection.getFestival();

                if (token == null || token.isEmpty()) {
                    logger.warn("FCM token is missing for festival: {}", festival.getName());
                    continue;
                }

                if (tokenState == TokenState.Disabled) {
                    logger.warn("FCM token is disabled for festival: {}", festival.getName());
                    continue;
                }

                String notiTitle = festival.getName();
                String notiMsg = festival.getStartDate() + " 에 " + festival.getName() + "이 시작됩니다.";

                logger.info("Sending notification for festival: {} with FCM token: {}", festival.getName(), token);
                snsService.snsFCMWorkFlow(arn, token, notiTitle, notiMsg);
            }

            logger.info("AWS SNS notification setup completed successfully.");
        } catch (Exception e) {
            logger.error("An error occurred while setting up AWS SNS notifications: ", e);
        }
    }
}