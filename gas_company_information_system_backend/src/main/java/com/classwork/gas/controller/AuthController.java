package com.classwork.gas.controller;

import com.classwork.gas.entity.ApiResponse;
import com.classwork.gas.entity.dto.auth.AuthDtos;
import com.classwork.gas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 发送邮箱验证码，用于注册或重置密码。
     */
    @PostMapping("/send-code")
    public ApiResponse<Void> sendCode(@Valid @RequestBody AuthDtos.SendCodeRequest request) {
        authService.sendCode(request.getEmail(), request.getPurpose());
        return ApiResponse.ok("验证码已发送", null);
    }

    /**
     * 使用验证码完成注册并返回 JWT。
     */
    @PostMapping("/register")
    public ApiResponse<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ApiResponse.ok("注册成功", authService.register(request));
    }

    /**
     * 登录校验账号密码并返回 JWT。
     */
    @PostMapping("/login")
    public ApiResponse<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }

    /**
     * 通过邮箱验证码重置密码。
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("密码重置成功", null);
    }

    /**
     * 退出登录：前端丢弃 JWT 即可，服务端返回确认。
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.ok("已退出登录", null);
    }
}
