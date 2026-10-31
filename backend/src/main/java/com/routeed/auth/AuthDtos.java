package com.routeed.auth;

import com.routeed.user.User;
import com.routeed.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() { }

    public record RegisterRequest(
            @NotBlank @Size(max = 160) String name,
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            UserRole role,
            UUID organizationId) { }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }

    public record UserResponse(UUID id, UUID organizationId, String name, String email, UserRole role, Instant createdAt) {
        static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getOrganizationId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
        }
    }

    public record AuthResponse(String accessToken, String refreshToken, String tokenType, UserResponse user) { }
    public record RefreshRequest(@NotBlank String refreshToken) { }
}
