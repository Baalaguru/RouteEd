package com.routeed.course;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) { this.courseService = courseService; }

    @GetMapping
    public List<CourseDtos.CourseResponse> list(@RequestHeader("X-Organization-Id") UUID organizationId) {
        return courseService.list(organizationId);
    }

    @GetMapping("/{courseId}")
    public CourseDtos.CourseResponse get(@RequestHeader("X-Organization-Id") UUID organizationId,
                                         @PathVariable UUID courseId) {
        return courseService.get(organizationId, courseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDtos.CourseResponse create(@RequestHeader("X-Organization-Id") UUID organizationId,
                                            @Valid @RequestBody CourseDtos.CreateCourseRequest request) {
        return courseService.create(organizationId, request);
    }

    @PostMapping("/{courseId}/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDtos.ModuleResponse addModule(@RequestHeader("X-Organization-Id") UUID organizationId,
                                               @PathVariable UUID courseId,
                                               @Valid @RequestBody CourseDtos.AddModuleRequest request) {
        return courseService.addModule(organizationId, courseId, request);
    }
}

