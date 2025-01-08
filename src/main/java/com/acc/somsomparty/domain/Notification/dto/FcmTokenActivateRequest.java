package com.acc.somsomparty.domain.Notification.dto;

import com.acc.somsomparty.domain.Notification.enums.DeviceType;
import lombok.Getter;

@Getter
public class FcmTokenActivateRequest {
    private String token;
    private DeviceType deviceType;
}
