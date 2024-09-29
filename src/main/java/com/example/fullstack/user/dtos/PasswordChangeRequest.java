package com.example.fullstack.user.dtos;

public record PasswordChangeRequest(String currentPassword, String newPassword) {
}
