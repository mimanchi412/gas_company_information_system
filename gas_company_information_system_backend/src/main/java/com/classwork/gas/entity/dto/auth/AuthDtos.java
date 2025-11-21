package com.classwork.gas.entity.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDtos {

    @Data
    public static class SendCodeRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String purpose; // REGISTER or RESET
    }

    @Data
    public static class RegisterRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String name;

        @NotBlank
        @Size(min = 6)
        private String password;

        @NotBlank
        private String code;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class ResetPasswordRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        @Size(min = 6)
        private String newPassword;

        @NotBlank
        private String code;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private Long userId;
        private String name;
        private String role;
        private String email;
    }
}
