package com.gabeust.userservice.dto;

public record ChangePasswordRequest(String currentPassword,  String newPassword) {
}
