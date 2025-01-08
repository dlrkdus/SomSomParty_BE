package com.acc.somsomparty.domain.Notification.controller;

import com.acc.somsomparty.domain.Notification.dto.FcmTokenActivateRequest;
import com.acc.somsomparty.domain.Notification.dto.FcmTokenDeactivateRequest;
import com.acc.somsomparty.domain.Notification.service.FcmTokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final FcmTokenService fcmTokenService;

    @Operation(
            summary = "FCM 토큰 활성화",
            description = "FCM 토큰과 디바이스 정보를 받아 해당 토큰을 활성화합니다."
    )
    @PostMapping("/notification/activate")
    public ResponseEntity<String> activateFcmToken(@RequestBody FcmTokenActivateRequest req) {
        fcmTokenService.activateFcmToken(req.getToken(), req.getDeviceType());
        return ResponseEntity.ok("FCM 토큰이 활성화되었습니다.");
    }

    @Operation(
            summary = "FCM 토큰 비활성화",
            description = "FCM 토큰 정보를 받아 해당 토큰을 비활성화합니다."
    )
    @PostMapping("/notification/deactivate")
    public ResponseEntity<String> deactivateFcmToken(@RequestBody FcmTokenDeactivateRequest req) {
        fcmTokenService.deactivateFcmToken(req.getToken());
        return ResponseEntity.ok("FCM 토큰이 활성화되었습니다.");
    }
}
