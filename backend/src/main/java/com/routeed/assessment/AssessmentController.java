package com.routeed.assessment;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AssessmentController {
    private final AssessmentService assessments;
    public AssessmentController(AssessmentService assessments) { this.assessments = assessments; }
    @PostMapping("/courses/{courseId}/assessments") @ResponseStatus(HttpStatus.CREATED)
    public AssessmentDtos.AssessmentResponse create(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID courseId, @Valid @RequestBody AssessmentDtos.CreateAssessmentRequest request) { return assessments.create(principal.getUsername(), courseId, request); }
    @GetMapping("/courses/{courseId}/assessments")
    public AssessmentDtos.AssessmentResponse get(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID courseId) { return assessments.getForAdmin(principal.getUsername(), courseId); }
    @PostMapping("/assessments/{assessmentId}/questions") @ResponseStatus(HttpStatus.CREATED)
    public AssessmentDtos.QuestionResponse addQuestion(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID assessmentId, @Valid @RequestBody AssessmentDtos.CreateQuestionRequest request) { return assessments.addQuestion(principal.getUsername(), assessmentId, request); }
}
