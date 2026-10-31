package com.routeed.communication;

import com.routeed.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "email_logs")
public class EmailLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "learner_id", nullable = false) private User learner;
    @Enumerated(EnumType.STRING) @Column(name = "email_type", nullable = false) private EmailType emailType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EmailStatus status = EmailStatus.QUEUED;
    @Column(nullable = false) private String recipient;
    @Column(name = "sent_at") private Instant sentAt;
    protected EmailLog() { }
    public EmailLog(User learner, EmailType emailType) { this.learner = learner; this.emailType = emailType; this.recipient = learner.getEmail(); }
    public void markSent() { status = EmailStatus.SENT; sentAt = Instant.now(); }
    public void markFailed() { status = EmailStatus.FAILED; }
}
