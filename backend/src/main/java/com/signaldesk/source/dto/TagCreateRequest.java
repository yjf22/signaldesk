package com.signaldesk.source.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TagCreateRequest {
    @NotBlank @Size(max = 64)
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color;
}
