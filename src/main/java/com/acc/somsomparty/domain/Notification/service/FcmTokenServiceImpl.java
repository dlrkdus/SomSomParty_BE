package com.acc.somsomparty.domain.Notification.service;

import com.acc.somsomparty.domain.Notification.entity.FcmToken;
import com.acc.somsomparty.domain.Notification.enums.DeviceType;
import com.acc.somsomparty.domain.Notification.enums.TokenState;
import com.acc.somsomparty.domain.Notification.repository.FcmTokenRepository;
import com.acc.somsomparty.domain.User.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenServiceImpl implements FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserService userService;

    public void activateFcmToken(String token, DeviceType deviceType) {
        Long userId = userService.getIdByAuthentication();
        log.info("Activating FCM token. User ID: {}, Token: {}, Device Type: {}", userId, token, deviceType);

        FcmToken fcmToken = fcmTokenRepository.findByFcmTokenAndUserId(token, userId);

        if (fcmToken == null) {
            log.info("No existing FCM token found. Creating new token for User ID: {}", userId);
            fcmToken = FcmToken.builder()
                    .userId(userId)
                    .fcmToken(token)
                    .deviceType(deviceType)
                    .tokenState(TokenState.Enabled)
                    .build();
        } else {
            log.info("Existing FCM token found for User ID: {}. Updating token state to Enabled.", userId);
            fcmToken.setTokenState(TokenState.Enabled);
        }
        fcmTokenRepository.save(fcmToken);
        log.info("FCM token successfully activated for User ID: {}", userId);
    }

    public void deactivateFcmToken(String token) {
        Long userId = userService.getIdByAuthentication();
        log.info("Deactivating FCM token. User ID: {}, Token: {}", userId, token);

        FcmToken fcmToken = fcmTokenRepository.findByFcmTokenAndUserId(token, userId);

        if (fcmToken != null) {
            log.info("Existing FCM token found for User ID: {}. Updating token state to Disabled.", userId);
            fcmToken.setTokenState(TokenState.Disabled);
            fcmTokenRepository.save(fcmToken);
            log.info("FCM token successfully deactivated for User ID: {}", userId);
        } else {
            log.warn("No FCM token found for User ID: {} and Token: {}. Nothing to deactivate.", userId, token);
        }
    }
}
