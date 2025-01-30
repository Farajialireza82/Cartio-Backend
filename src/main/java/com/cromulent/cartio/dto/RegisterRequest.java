package com.cromulent.cartio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        String fullName,
        @Email(message = "Must be a valid email address")
        String username,
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
                message = "Password must be at least 8 characters long and contain both letters and numbers"
        )
        String password) {
}
