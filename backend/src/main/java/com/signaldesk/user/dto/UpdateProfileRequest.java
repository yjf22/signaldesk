package com.signaldesk.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {
    @Size(max = 64)
    private String displayName;

    @Size(max = 512)
    private String avatarUrl;
}
