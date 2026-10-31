package com.routeed.course;

import com.routeed.organization.Organization;
import com.routeed.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "estimated_duration_days", nullable = false)
    private int estimatedDurationDays = 28;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Course() { }

    public Course(Organization organization, String title, String description, int estimatedDurationDays) {
        this.organization = organization;
        this.title = title;
        this.description = description;
        this.estimatedDurationDays = estimatedDurationDays;
    }

    @PreUpdate
    void updateTimestamp() { updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public CourseStatus getStatus() { return status; }
    public int getEstimatedDurationDays() { return estimatedDurationDays; }
    public UUID getOrganizationId() { return organization.getId(); }
    public UUID getAdminId() { return admin == null ? null : admin.getId(); }
    public void assignAdmin(User admin) { this.admin = admin; }
}

