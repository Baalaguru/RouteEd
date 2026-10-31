package com.routeed.course;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public final class CourseDtos {
    private CourseDtos() { }

    public record CreateCourseRequest(
            @NotBlank String title,
            String description,
            @Min(1) @Max(365) Integer estimatedDurationDays
    ) { }

    public record AddModuleRequest(
            @NotBlank String title,
            String description,
            String contentUrl,
            @Min(1) Integer estimatedMinutes,
            @Min(1) Integer defaultOrderIndex,
            String skillTag
    ) { }

    public record UpdateModuleRequest(
            @NotBlank String title,
            String description,
            String contentUrl,
            @Min(1) Integer estimatedMinutes,
            @Min(1) Integer defaultOrderIndex,
            String skillTag
    ) { }

    public record ModuleResponse(UUID id, String title, String description, String contentUrl,
                                 int order, int estimatedMinutes, String skillTag) { }

    public record CourseResponse(UUID id, String title, String description, String status,
                                 int estimatedDurationDays, List<ModuleResponse> modules) { }
}

