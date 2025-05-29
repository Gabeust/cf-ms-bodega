package com.gabeust.userservice.dto;
/**
 * DTO para solicitudes de cambio de contraseña.
 *
 * Contiene la contraseña actual y la nueva contraseña que el usuario desea establecer.
 *
 * @param currentPassword la contraseña actual del usuario
 * @param newPassword     la nueva contraseña que se desea establecer
 */
public record ChangePasswordRequest(String currentPassword,  String newPassword) {
}
