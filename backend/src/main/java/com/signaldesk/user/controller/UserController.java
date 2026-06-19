package com.signaldesk.user.controller;

import com.signaldesk.infrastructure.dto.ApiResponse;
import com.signaldesk.user.dto.UpdateProfileRequest;
import com.signaldesk.user.dto.UserProfileResponse;
import com.signaldesk.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getProfile(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(userService.getProfile(userId));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateProfile(
            Authentication auth,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(userService.updateProfile(userId, request));
    }
}
