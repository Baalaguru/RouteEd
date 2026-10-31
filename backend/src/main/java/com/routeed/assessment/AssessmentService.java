package com.routeed.assessment;

import com.routeed.course.Course;
import com.routeed.course.CourseRepository;
import com.routeed.user.User;
import com.routeed.user.UserRepository;
import com.routeed.learning.PathPersonalizationService;
import java.util.List;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AssessmentService {
    private final AssessmentRepository assessments;
    private final QuestionRepository questions;
    private final CourseRepository courses;
    private final UserRepository users;
    private final LearnerAssessmentRepository learnerAssessments;
    private final PathPersonalizationService personalization;

    public AssessmentService(AssessmentRepository assessments, QuestionRepository questions, CourseRepository courses, UserRepository users, LearnerAssessmentRepository learnerAssessments, PathPersonalizationService personalization) {
        this.assessments = assessments; this.questions = questions; this.courses = courses; this.users = users; this.learnerAssessments = learnerAssessments; this.personalization = personalization;
    }

    @Transactional
    public AssessmentDtos.AssessmentResponse create(String email, UUID courseId, AssessmentDtos.CreateAssessmentRequest request) {
        Course course = ownedCourse(email, courseId);
        if (assessments.findByCourse_Id(courseId).isPresent()) throw new IllegalArgumentException("This course already has a diagnostic assessment");
        return response(assessments.save(new Assessment(course, request.title())));
    }

    @Transactional
    public AssessmentDtos.QuestionResponse addQuestion(String email, UUID assessmentId, AssessmentDtos.CreateQuestionRequest request) {
        Assessment assessment = assessments.findById(assessmentId).orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
        ownedCourse(email, assessment.getCourseId());
        if (!request.options().contains(request.correctOption())) throw new IllegalArgumentException("Correct option must be one of the supplied options");
        int position = questions.findByAssessment_IdOrderByPosition(assessmentId).size() + 1;
        Question question = questions.save(new Question(assessment, request.questionText(), request.options(), request.correctOption(), request.skillTag(), position));
        return questionResponse(question);
    }

    public AssessmentDtos.AssessmentResponse getForAdmin(String email, UUID courseId) {
        ownedCourse(email, courseId);
        Assessment assessment = assessments.findByCourse_Id(courseId).orElseThrow(() -> new IllegalArgumentException("No diagnostic assessment exists for this course"));
        return response(assessment);
    }

    private Course ownedCourse(String email, UUID courseId) {
        User admin = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Course course = courses.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!admin.getId().equals(course.getAdminId())) throw new IllegalArgumentException("Course is not managed by this administrator");
        return course;
    }
    private AssessmentDtos.AssessmentResponse response(Assessment assessment) {
        List<AssessmentDtos.QuestionResponse> items = questions.findByAssessment_IdOrderByPosition(assessment.getId()).stream().map(this::questionResponse).toList();
        return new AssessmentDtos.AssessmentResponse(assessment.getId(), assessment.getCourseId(), assessment.getTitle(), items);
    }
    private AssessmentDtos.QuestionResponse questionResponse(Question question) { return new AssessmentDtos.QuestionResponse(question.getId(), question.getQuestionText(), question.getOptionsJson(), question.getCorrectOption(), question.getSkillTag()); }

    public AssessmentDtos.LearnerAssessmentResponse learnerAssessment(UUID courseId) {
        Assessment assessment = assessments.findByCourse_Id(courseId).orElseThrow(() -> new IllegalArgumentException("No onboarding assessment exists for this course"));
        List<AssessmentDtos.LearnerQuestionResponse> items = questions.findByAssessment_IdOrderByPosition(assessment.getId()).stream()
                .map(question -> new AssessmentDtos.LearnerQuestionResponse(question.getId(), question.getQuestionText(), question.getOptionsJson(), question.getSkillTag())).toList();
        return new AssessmentDtos.LearnerAssessmentResponse(assessment.getId(), courseId, assessment.getTitle(), items);
    }

    @Transactional
    public AssessmentDtos.AssessmentResultResponse submit(String email, UUID assessmentId, AssessmentDtos.SubmitAssessmentRequest request) {
        User learner = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Assessment assessment = assessments.findById(assessmentId).orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
        List<Question> items = questions.findByAssessment_IdOrderByPosition(assessmentId);
        if (items.isEmpty()) throw new IllegalArgumentException("This assessment has no questions");
        var totals = new LinkedHashMap<String, Integer>(); var correct = new LinkedHashMap<String, Integer>(); int totalCorrect = 0;
        for (Question question : items) { String skill = question.getSkillTag() == null ? "GENERAL" : question.getSkillTag(); totals.merge(skill, 1, Integer::sum); if (question.getCorrectOption().equals(request.answers().get(question.getId()))) { correct.merge(skill, 1, Integer::sum); totalCorrect++; } }
        var skillScores = new LinkedHashMap<String, BigDecimal>();
        totals.forEach((skill, total) -> skillScores.put(skill, BigDecimal.valueOf(correct.getOrDefault(skill, 0) * 100.0 / total).setScale(2, RoundingMode.HALF_UP)));
        BigDecimal overall = BigDecimal.valueOf(totalCorrect * 100.0 / items.size()).setScale(2, RoundingMode.HALF_UP);
        LearnerAssessment saved = learnerAssessments.save(new LearnerAssessment(learner, assessment, overall, skillScores));
        personalization.generate(saved);
        return new AssessmentDtos.AssessmentResultResponse(saved.getId(), overall, skillScores);
    }
}
