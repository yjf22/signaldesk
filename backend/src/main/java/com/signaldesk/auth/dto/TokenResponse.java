package com.signaldesk.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String displayName;
    }
}
