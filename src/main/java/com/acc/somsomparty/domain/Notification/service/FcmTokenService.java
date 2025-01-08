package com.acc.somsomparty.domain.Notification.service;

import com.acc.somsomparty.domain.Notification.enums.DeviceType;

public interface FcmTokenService {
    void activateFcmToken(String token, DeviceType deviceType);
    void deactivateFcmToken(String token);
}
