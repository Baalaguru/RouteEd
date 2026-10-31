package com.routeed.course;

import com.routeed.user.UserRepository;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learner/courses")
public class LearnerCourseController {
    private final CourseService courses; private final UserRepository users;
    public LearnerCourseController(CourseService courses, UserRepository users) { this.courses = courses; this.users = users; }
    @GetMapping public List<CourseDtos.CourseResponse> list(@AuthenticationPrincipal UserDetails principal) {
        var user = users.findByEmail(principal.getUsername()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return courses.list(user.getOrganizationId());
    }
}
