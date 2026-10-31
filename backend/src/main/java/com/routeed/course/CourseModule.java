package com.routeed.course;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "modules")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_url")
    private String contentUrl;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;

    @Column(name = "skill_tag", length = 100)
    private String skillTag;

    @Column(name = "default_order", nullable = false)
    private int defaultOrder;

    @Column(nullable = false)
    private String difficulty = "BEGINNER";

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes = 30;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected CourseModule() { }

    public CourseModule(Course course, String title, String description, String contentUrl, int defaultOrder, int estimatedMinutes) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.defaultOrder = defaultOrder;
        this.estimatedMinutes = estimatedMinutes;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContentUrl() { return contentUrl; }
    public int getDefaultOrder() { return defaultOrder; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public String getContentText() { return contentText; }
    public String getSkillTag() { return skillTag; }

    public Course getCourse() { return course; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public void setSkillTag(String skillTag) { this.skillTag = skillTag; }
    public void update(String title, String description, String contentUrl, Integer estimatedMinutes, String skillTag) {
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        if (estimatedMinutes != null) this.estimatedMinutes = estimatedMinutes;
        this.skillTag = skillTag;
    }
    public void setDefaultOrder(int defaultOrder) { this.defaultOrder = defaultOrder; }
}

