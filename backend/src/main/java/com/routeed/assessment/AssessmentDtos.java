package com.routeed.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public final class AssessmentDtos {
    private AssessmentDtos() { }
    public record CreateAssessmentRequest(@NotBlank @Size(max = 200) String title) { }
    public record CreateQuestionRequest(@NotBlank String questionText, @NotEmpty List<@NotBlank String> options,
                                        @NotBlank String correctOption, @NotBlank @Size(max = 100) String skillTag) { }
    public record QuestionResponse(UUID id, String questionText, List<String> options, String correctOption, String skillTag) { }
    public record AssessmentResponse(UUID id, UUID courseId, String title, List<QuestionResponse> questions) { }
    public record LearnerQuestionResponse(UUID id, String questionText, List<String> options, String skillTag) { }
    public record LearnerAssessmentResponse(UUID id, UUID courseId, String title, List<LearnerQuestionResponse> questions) { }
    public record SubmitAssessmentRequest(java.util.Map<UUID, String> answers) { }
    public record AssessmentResultResponse(UUID learnerAssessmentId, java.math.BigDecimal overallScore,
                                           java.util.Map<String, java.math.BigDecimal> skillScores) { }
    public record LearnerPathResponse(java.util.List<UUID> moduleOrder, java.util.List<UUID> skippedModuleIds,
                                      java.util.Map<String, String> aiRationale) { }
}
