package com.routeed.course;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminCourseController {
    private final CourseService courses;
    public AdminCourseController(CourseService courses) { this.courses = courses; }
    @GetMapping("/courses") public List<CourseDtos.CourseResponse> list(@AuthenticationPrincipal UserDetails principal) { return courses.listForAdmin(principal.getUsername()); }
    @PostMapping("/courses") @ResponseStatus(HttpStatus.CREATED) public CourseDtos.CourseResponse create(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody CourseDtos.CreateCourseRequest request) { return courses.createForAdmin(principal.getUsername(), request); }
    @PostMapping("/courses/{courseId}/modules") @ResponseStatus(HttpStatus.CREATED) public CourseDtos.ModuleResponse addModule(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID courseId, @Valid @RequestBody CourseDtos.AddModuleRequest request) { return courses.addModuleForAdmin(principal.getUsername(), courseId, request); }
    @PutMapping("/modules/{moduleId}") public CourseDtos.ModuleResponse updateModule(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID moduleId, @Valid @RequestBody CourseDtos.UpdateModuleRequest request) { return courses.updateModuleForAdmin(principal.getUsername(), moduleId, request); }
    @DeleteMapping("/modules/{moduleId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteModule(@AuthenticationPrincipal UserDetails principal, @PathVariable UUID moduleId) { courses.deleteModuleForAdmin(principal.getUsername(), moduleId); }
}
