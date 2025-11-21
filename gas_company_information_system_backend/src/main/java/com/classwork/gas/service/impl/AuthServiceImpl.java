package com.classwork.gas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classwork.gas.entity.dto.auth.AuthDtos;
import com.classwork.gas.mapper.UserMapper;
import com.classwork.gas.entity.User;
import com.classwork.gas.security.JwtUtil;
import com.classwork.gas.service.AuthService;
import com.classwork.gas.service.EmailCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final EmailCodeService emailCodeService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper,
                           EmailCodeService emailCodeService,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.emailCodeService = emailCodeService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void sendCode(String email, String purpose) {
        emailCodeService.sendCode(email, purpose);
    }

    @Override
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (exists != null && exists > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "邮箱已被注册");
        }
        emailCodeService.verifyCode(request.getEmail(), "REGISTER", request.getCode());
        User saved = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role("USER")
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();
        userMapper.insert(saved);
        return toAuthResponse(saved);
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        return toAuthResponse(user);
    }

    @Override
    public void resetPassword(AuthDtos.ResetPasswordRequest request) {
        emailCodeService.verifyCode(request.getEmail(), "RESET", request.getCode());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    private AuthDtos.AuthResponse toAuthResponse(User user) {
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setToken(jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole()));
        return response;
    }
}
