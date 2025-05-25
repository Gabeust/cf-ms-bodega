package com.gabeust.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(@NotBlank String email, @NotBlank String password) {
}
