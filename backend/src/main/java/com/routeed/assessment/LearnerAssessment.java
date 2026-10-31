package com.routeed.assessment;

import com.routeed.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "learner_assessments")
public class LearnerAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "overall_score", precision = 5, scale = 2)
    private BigDecimal overallScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skill_scores_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, BigDecimal> skillScoresJson = new LinkedHashMap<>();

    @Column(name = "completed_at")
    private Instant completedAt;

    protected LearnerAssessment() {
    }

    public LearnerAssessment(User learner, Assessment assessment, BigDecimal overallScore,
                             Map<String, BigDecimal> skillScoresJson) {
        this.learner = learner;
        this.assessment = assessment;
        this.overallScore = overallScore;
        this.skillScoresJson = new LinkedHashMap<>(skillScoresJson);
        this.completedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public BigDecimal getOverallScore() { return overallScore; }
    public Map<String, BigDecimal> getSkillScoresJson() { return Map.copyOf(skillScoresJson); }
    public Instant getCompletedAt() { return completedAt; }
    public User getLearner() { return learner; }
    public Assessment getAssessment() { return assessment; }
}
