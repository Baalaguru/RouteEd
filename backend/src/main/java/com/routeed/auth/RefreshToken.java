package com.routeed.auth;

import com.routeed.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "token_hash", nullable = false, unique = true) private String tokenHash;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "revoked_at") private Instant revokedAt;
    @Column(name = "replacement_token_id") private UUID replacementTokenId;
    protected RefreshToken() { }
    public RefreshToken(User user, String tokenHash, Instant expiresAt) { this.user = user; this.tokenHash = tokenHash; this.expiresAt = expiresAt; }
    public User getUser() { return user; }
    public UUID getId() { return id; }
    public boolean isUsable() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
    public void revoke(UUID replacementId) { revokedAt = Instant.now(); replacementTokenId = replacementId; }
}
