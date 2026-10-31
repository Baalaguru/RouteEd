package com.routeed.auth;

import com.routeed.auth.AuthDtos.AuthResponse;
import com.routeed.auth.AuthDtos.LoginRequest;
import com.routeed.auth.AuthDtos.RegisterRequest;
import com.routeed.auth.AuthDtos.UserResponse;
import com.routeed.organization.Organization;
import com.routeed.organization.OrganizationRepository;
import com.routeed.security.JwtProvider;
import com.routeed.user.User;
import com.routeed.user.UserRepository;
import com.routeed.user.UserRole;
import java.util.UUID;
import java.time.Instant;
import java.time.Duration;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final UserRepository users;
    private final OrganizationRepository organizations;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UUID defaultOrganizationId;
    private final RefreshTokenRepository refreshTokens;
    private final Duration refreshDuration;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository users, OrganizationRepository organizations, PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider, RefreshTokenRepository refreshTokens, @Value("${routeed.default-organization-id}") UUID defaultOrganizationId,
                       @Value("${routeed.jwt.refresh-token-days}") long refreshTokenDays) {
        this.users = users;
        this.organizations = organizations;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.defaultOrganizationId = defaultOrganizationId;
        this.refreshTokens = refreshTokens;
        this.refreshDuration = Duration.ofDays(refreshTokenDays);
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new IllegalArgumentException("An account already exists for this email");
        Organization organization = organizations.findById(request.organizationId() == null ? defaultOrganizationId : request.organizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        if (request.role() == UserRole.ADMIN) throw new IllegalArgumentException("Administrator accounts are provisioned by an existing administrator");
        User user = users.save(new User(organization, request.name().trim(), email, passwordEncoder.encode(request.password()), UserRole.LEARNER));
        return responseFor(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        user.recordLogin();
        return responseFor(user);
    }

    public AuthResponse refresh(String rawToken) {
        RefreshToken current = refreshTokens.findByTokenHash(hash(rawToken)).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (!current.isUsable()) throw new IllegalArgumentException("Refresh token has expired or was revoked");
        String nextRaw = newToken();
        RefreshToken replacement = refreshTokens.save(new RefreshToken(current.getUser(), hash(nextRaw), Instant.now().plus(refreshDuration)));
        current.revoke(replacement.getId());
        refreshTokens.save(current);
        return new AuthResponse(jwtProvider.generateAccessToken(current.getUser()), nextRaw, "Bearer", UserResponse.from(current.getUser()));
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String email) {
        return UserResponse.from(users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    private AuthResponse responseFor(User user) {
        String raw = newToken();
        refreshTokens.save(new RefreshToken(user, hash(raw), Instant.now().plus(refreshDuration)));
        return new AuthResponse(jwtProvider.generateAccessToken(user), raw, "Bearer", UserResponse.from(user));
    }

    private String newToken() { byte[] bytes = new byte[48]; secureRandom.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private String hash(String value) { try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8))); } catch (Exception exception) { throw new IllegalStateException("Token hashing is unavailable", exception); } }
}
