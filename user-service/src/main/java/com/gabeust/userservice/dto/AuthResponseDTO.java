package com.gabeust.userservice.dto;


public record AuthResponseDTO (String email, String message, String jwt, boolean status){
}
