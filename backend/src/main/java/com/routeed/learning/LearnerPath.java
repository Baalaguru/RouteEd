package com.routeed.learning;

import com.routeed.course.Course;
import com.routeed.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "learner_paths")
public class LearnerPath {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "learner_id", nullable = false)
    private User learner;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "module_order_json", nullable = false, columnDefinition = "jsonb")
    private List<UUID> moduleOrderJson = new ArrayList<>();
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "skipped_module_ids", nullable = false, columnDefinition = "jsonb")
    private List<UUID> skippedModuleIds = new ArrayList<>();
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "ai_rationale_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> aiRationaleJson = new LinkedHashMap<>();
    @Column(name = "generated_at", nullable = false) private Instant generatedAt = Instant.now();

    protected LearnerPath() { }
    public LearnerPath(User learner, Course course, List<UUID> moduleOrder, List<UUID> skippedModuleIds, Map<String, String> rationale) {
        this.learner = learner; this.course = course; this.moduleOrderJson = new ArrayList<>(moduleOrder);
        this.skippedModuleIds = new ArrayList<>(skippedModuleIds); this.aiRationaleJson = new LinkedHashMap<>(rationale);
    }
    public UUID getId() { return id; }
    public List<UUID> getModuleOrderJson() { return List.copyOf(moduleOrderJson); }
    public List<UUID> getSkippedModuleIds() { return List.copyOf(skippedModuleIds); }
    public Map<String, String> getAiRationaleJson() { return Map.copyOf(aiRationaleJson); }
    public User getLearner() { return learner; }
    public Course getCourse() { return course; }
}
