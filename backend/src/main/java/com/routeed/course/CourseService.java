package com.routeed.course;

import com.routeed.organization.Organization;
import com.routeed.organization.OrganizationRepository;
import com.routeed.user.User;
import com.routeed.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, CourseModuleRepository moduleRepository,
                         OrganizationRepository organizationRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public List<CourseDtos.CourseResponse> listForAdmin(String email) {
        User admin = findUser(email);
        return courseRepository.findByAdmin_IdOrderByCreatedAtDesc(admin.getId()).stream().map(this::toResponse).toList();
    }

    public List<CourseDtos.CourseResponse> list(UUID organizationId) {
        return courseRepository.findByOrganization_IdOrderByCreatedAtDesc(organizationId).stream()
                .map(this::toResponse).toList();
    }

    public CourseDtos.CourseResponse get(UUID organizationId, UUID courseId) {
        return toResponse(findScopedCourse(organizationId, courseId));
    }

    @Transactional
    public CourseDtos.CourseResponse create(UUID organizationId, CourseDtos.CreateCourseRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        Course course = courseRepository.save(new Course(organization, request.title(), request.description(),
                request.estimatedDurationDays() == null ? 28 : request.estimatedDurationDays()));
        return toResponse(course);
    }

    @Transactional
    public CourseDtos.CourseResponse createForAdmin(String email, CourseDtos.CreateCourseRequest request) {
        User admin = findUser(email);
        Course course = new Course(organizationRepository.getReferenceById(admin.getOrganizationId()), request.title(), request.description(),
                request.estimatedDurationDays() == null ? 28 : request.estimatedDurationDays());
        course.assignAdmin(admin);
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseDtos.ModuleResponse addModule(UUID organizationId, UUID courseId, CourseDtos.AddModuleRequest request) {
        Course course = findScopedCourse(organizationId, courseId);
        int nextOrder = request.defaultOrderIndex() == null ? moduleRepository.findByCourse_IdOrderByDefaultOrder(courseId).size() + 1 : request.defaultOrderIndex();
        shiftOrdersForInsert(courseId, nextOrder);
        CourseModule module = moduleRepository.save(new CourseModule(course, request.title(), request.description(),
                request.contentUrl(), nextOrder, request.estimatedMinutes() == null ? 30 : request.estimatedMinutes()));
        module.setSkillTag(request.skillTag());
        return toModuleResponse(module);
    }

    @Transactional
    public CourseDtos.ModuleResponse addModuleForAdmin(String email, UUID courseId, CourseDtos.AddModuleRequest request) {
        Course course = findOwnedCourse(email, courseId);
        int order = request.defaultOrderIndex() == null ? moduleRepository.findByCourse_IdOrderByDefaultOrder(courseId).size() + 1 : request.defaultOrderIndex();
        shiftOrdersForInsert(courseId, order);
        CourseModule module = new CourseModule(course, request.title(), request.description(), request.contentUrl(), order,
                request.estimatedMinutes() == null ? 30 : request.estimatedMinutes());
        module.setSkillTag(request.skillTag());
        return toModuleResponse(moduleRepository.save(module));
    }

    @Transactional
    public CourseDtos.ModuleResponse updateModuleForAdmin(String email, UUID moduleId, CourseDtos.UpdateModuleRequest request) {
        CourseModule module = moduleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module not found"));
        findOwnedCourse(email, module.getCourse().getId());
        module.update(request.title(), request.description(), request.contentUrl(), request.estimatedMinutes(), request.skillTag());
        if (request.defaultOrderIndex() != null && request.defaultOrderIndex() != module.getDefaultOrder()) reorder(module.getCourse().getId(), moduleId, request.defaultOrderIndex());
        return toModuleResponse(module);
    }

    @Transactional
    public void deleteModuleForAdmin(String email, UUID moduleId) {
        CourseModule module = moduleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module not found"));
        findOwnedCourse(email, module.getCourse().getId());
        UUID courseId = module.getCourse().getId();
        moduleRepository.delete(module);
        renumber(courseId);
    }

    private void shiftOrdersForInsert(UUID courseId, int atOrder) {
        List<CourseModule> modules = moduleRepository.findByCourse_IdOrderByDefaultOrder(courseId);
        modules.forEach(module -> module.setDefaultOrder(module.getDefaultOrder() + 1000));
        modules.stream().sorted((left, right) -> Integer.compare(left.getDefaultOrder(), right.getDefaultOrder())).forEach(module -> {
            int originalOrder = module.getDefaultOrder() - 1000;
            module.setDefaultOrder(originalOrder >= atOrder ? originalOrder + 1 : originalOrder);
        });
    }

    private void reorder(UUID courseId, UUID movedModuleId, int requestedOrder) {
        List<CourseModule> modules = moduleRepository.findByCourse_IdOrderByDefaultOrder(courseId);
        CourseModule moved = modules.stream().filter(module -> module.getId().equals(movedModuleId)).findFirst().orElseThrow();
        modules.remove(moved);
        modules.add(Math.min(Math.max(requestedOrder - 1, 0), modules.size()), moved);
        modules.forEach(module -> module.setDefaultOrder(module.getDefaultOrder() + 1000));
        for (int index = 0; index < modules.size(); index++) modules.get(index).setDefaultOrder(index + 1);
    }

    private void renumber(UUID courseId) {
        List<CourseModule> modules = moduleRepository.findByCourse_IdOrderByDefaultOrder(courseId);
        modules.forEach(module -> module.setDefaultOrder(module.getDefaultOrder() + 1000));
        for (int index = 0; index < modules.size(); index++) modules.get(index).setDefaultOrder(index + 1);
    }

    private Course findOwnedCourse(String email, UUID courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getAdminId().equals(findUser(email).getId())) throw new IllegalArgumentException("Course is not managed by this administrator");
        return course;
    }

    private User findUser(String email) { return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found")); }

    private Course findScopedCourse(UUID organizationId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getOrganizationId().equals(organizationId)) {
            throw new IllegalArgumentException("Course is not available to this organization");
        }
        return course;
    }

    private CourseDtos.CourseResponse toResponse(Course course) {
        List<CourseDtos.ModuleResponse> modules = moduleRepository.findByCourse_IdOrderByDefaultOrder(course.getId())
                .stream().map(this::toModuleResponse).toList();
        return new CourseDtos.CourseResponse(course.getId(), course.getTitle(), course.getDescription(),
                course.getStatus().name(), course.getEstimatedDurationDays(), modules);
    }

    private CourseDtos.ModuleResponse toModuleResponse(CourseModule module) {
        return new CourseDtos.ModuleResponse(module.getId(), module.getTitle(), module.getDescription(),
                module.getContentUrl(), module.getDefaultOrder(), module.getEstimatedMinutes(), module.getSkillTag());
    }
}

