package com.signaldesk.user.dto;

import com.signaldesk.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String createdAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }
}
