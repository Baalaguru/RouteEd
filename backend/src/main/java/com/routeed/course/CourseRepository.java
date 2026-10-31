package com.routeed.course;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByOrganization_IdOrderByCreatedAtDesc(UUID organizationId);
    List<Course> findByAdmin_IdOrderByCreatedAtDesc(UUID adminId);
}

