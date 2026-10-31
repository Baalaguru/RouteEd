package com.routeed.assessment;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.routeed.user.UserRepository;
import com.routeed.learning.PathPersonalizationService;

@RestController
@RequestMapping("/api/learner")
public class LearnerAssessmentController {
    private final AssessmentService assessments;
    private final UserRepository users;
    private final PathPersonalizationService paths;
    public LearnerAssessmentController(AssessmentService assessments, UserRepository users, PathPersonalizationService paths) { this.assessments = assessments; this.users = users; this.paths = paths; }
    @GetMapping("/courses/{courseId}/assessment")
    public AssessmentDtos.LearnerAssessmentResponse get(@PathVariable UUID courseId) { return assessments.learnerAssessment(courseId); }
    @PostMapping("/assessments/{assessmentId}/submit")
    public AssessmentDtos.AssessmentResultResponse submit(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID assessmentId, @Valid @RequestBody AssessmentDtos.SubmitAssessmentRequest request) { return assessments.submit(principal.getUsername(), assessmentId, request); }
    @GetMapping("/courses/{courseId}/path")
    public AssessmentDtos.LearnerPathResponse path(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID courseId) {
        var learner = users.findByEmail(principal.getUsername()).orElseThrow(() -> new IllegalArgumentException("User not found")); var path = paths.latest(learner, courseId);
        return new AssessmentDtos.LearnerPathResponse(path.getModuleOrderJson(), path.getSkippedModuleIds(), path.getAiRationaleJson());
    }
}
