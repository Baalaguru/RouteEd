package com.routeed.learning;

import com.routeed.course.CourseModule;
import com.routeed.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "progress_events", uniqueConstraints = @UniqueConstraint(columnNames = {"learner_id", "module_id"}))
public class ProgressEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "learner_id", nullable = false) private User learner;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "module_id", nullable = false) private CourseModule module;
    @Column(name = "completed_at", nullable = false) private Instant completedAt = Instant.now();
    protected ProgressEvent() { }
    public ProgressEvent(User learner, CourseModule module) { this.learner = learner; this.module = module; }
    public UUID getId() { return id; }
    public Instant getCompletedAt() { return completedAt; }
    public CourseModule getModule() { return module; }
}
