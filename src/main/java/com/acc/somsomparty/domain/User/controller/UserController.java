package com.acc.somsomparty.domain.User.controller;

import com.acc.somsomparty.domain.User.dto.*;
import com.acc.somsomparty.domain.User.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "사용자의 이메일과 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("이메일 인증을 완료해주세요.");
    }

    @Operation(summary = "회원가입 확인", description = "이메일 인증 코드를 통해 회원가입을 완료합니다.")
    @PostMapping("/confirm-signup")
    public ResponseEntity<String> confirmSignUp(@RequestBody ConfirmSignUpReqeust req) {
        System.out.println("confirmSignUp controller email : " + req.getEmail());
        userService.confirmSignUp(req.getEmail(), req.getName(), req.getCode());
        return ResponseEntity.ok("회원가입되었습니다.");
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 액세스 토큰과 리프레시 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Map<String, Object> result = userService.login(req.getEmail(), req.getPassword());

        AdminInitiateAuthResponse response = (AdminInitiateAuthResponse) result.get("authResponse");
        String username = (String) result.get("username");

        return ResponseEntity.ok(LoginResponse.fromAdminInitiateAuthResponse(response, username));
    }

    @Operation(summary = "로그아웃", description = "Authorization 헤더로 전달된 토큰을 사용하여 로그아웃합니다.")
    @PostMapping("/signout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "토큰 유효성 검사", description = "Authorization 헤더로 전달된 액세스 토큰의 유효성을 검사합니다.")
    @GetMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String token) {
        boolean isValid = userService.verifyToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(isValid ? "유효한 토큰" : "유효하지 않은 토큰");
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰과 사용자 이름을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken, @RequestParam(name = "username") String userName) {
        AdminInitiateAuthResponse response = userService.refreshAccessToken(refreshToken, userName);
        return ResponseEntity.ok(RefreshTokenResponse.fromAdminInitiateAuthResponse(response));
    }
}