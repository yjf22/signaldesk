package com.signaldesk.auth.controller;

import com.signaldesk.auth.dto.LoginRequest;
import com.signaldesk.auth.dto.RegisterRequest;
import com.signaldesk.auth.dto.TokenResponse;
import com.signaldesk.auth.service.AuthService;
import com.signaldesk.infrastructure.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
