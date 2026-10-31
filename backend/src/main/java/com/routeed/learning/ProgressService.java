package com.routeed.learning;
import com.routeed.course.*;
import com.routeed.user.User;
import com.routeed.user.UserRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProgressService {
 private final ProgressEventRepository events; private final CourseModuleRepository modules; private final UserRepository users; private final PathPersonalizationService paths;
 public ProgressService(ProgressEventRepository events, CourseModuleRepository modules, UserRepository users, PathPersonalizationService paths) { this.events=events;this.modules=modules;this.users=users;this.paths=paths; }
 @Transactional public void complete(String email, UUID moduleId) { User learner=user(email); CourseModule module=modules.findById(moduleId).orElseThrow(()->new IllegalArgumentException("Module not found")); events.findByLearner_IdAndModule_Id(learner.getId(), moduleId).orElseGet(()->events.save(new ProgressEvent(learner,module))); }
 public ProgressResponse progress(String email, UUID courseId) { User learner=user(email); LearnerPath path=paths.latest(learner,courseId); Set<UUID> completed=events.findByLearner_IdAndModule_Course_Id(learner.getId(),courseId).stream().map(event->event.getModule().getId()).collect(java.util.stream.Collectors.toSet()); List<UUID> active=path.getModuleOrderJson().stream().filter(id->!path.getSkippedModuleIds().contains(id)).toList(); UUID current=active.stream().filter(id->!completed.contains(id)).findFirst().orElse(null); int percent=active.isEmpty()?100:(int)Math.round(completed.stream().filter(active::contains).count()*100.0/active.size()); return new ProgressResponse(percent,List.copyOf(completed),current,path.getSkippedModuleIds()); }
 private User user(String email) { return users.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User not found")); }
 public record ProgressResponse(int percentage, List<UUID> completedModuleIds, UUID currentModuleId, List<UUID> skippedModuleIds) { }
}
