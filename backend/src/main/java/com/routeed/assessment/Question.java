package com.routeed.assessment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options_json", nullable = false, columnDefinition = "jsonb")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> optionsJson = new ArrayList<>();

    @Column(name = "correct_option", length = 100)
    private String correctOption;

    @Column(name = "skill_tag", length = 100)
    private String skillTag;

    @Column(nullable = false)
    private int points = 1;

    @Column(nullable = false)
    private int position;

    protected Question() {
    }

    public Question(Assessment assessment, String questionText, List<String> optionsJson,
                    String correctOption, String skillTag, int position) {
        this.assessment = assessment;
        this.questionText = questionText;
        this.optionsJson = new ArrayList<>(optionsJson);
        this.correctOption = correctOption;
        this.skillTag = skillTag;
        this.position = position;
    }

    public UUID getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptionsJson() { return List.copyOf(optionsJson); }
    public String getCorrectOption() { return correctOption; }
    public String getSkillTag() { return skillTag; }
}
