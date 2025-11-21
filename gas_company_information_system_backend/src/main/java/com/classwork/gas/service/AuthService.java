package com.classwork.gas.service;

import com.classwork.gas.entity.dto.auth.AuthDtos;

public interface AuthService {
    void sendCode(String email, String purpose);

    AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request);

    AuthDtos.AuthResponse login(AuthDtos.LoginRequest request);

    void resetPassword(AuthDtos.ResetPasswordRequest request);
}
