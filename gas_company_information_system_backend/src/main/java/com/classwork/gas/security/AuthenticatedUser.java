package com.classwork.gas.security;

import lombok.Value;

@Value
public class AuthenticatedUser {
    Long id;
    String email;
    String role;
}
