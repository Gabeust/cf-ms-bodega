package com.gabeust.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO(@NotBlank String email, @NotBlank String password) {
}
