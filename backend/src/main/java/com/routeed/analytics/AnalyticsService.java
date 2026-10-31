package com.routeed.analytics;

import com.routeed.assessment.LearnerAssessmentRepository;
import com.routeed.course.*;
import com.routeed.learning.*;
import com.routeed.user.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly=true)
public class AnalyticsService {
 private final CourseRepository courses; private final CourseModuleRepository modules; private final LearnerPathRepository paths; private final ProgressEventRepository progress; private final UserRepository users; private final LearnerAssessmentRepository assessments;
 public AnalyticsService(CourseRepository courses,CourseModuleRepository modules,LearnerPathRepository paths,ProgressEventRepository progress,UserRepository users,LearnerAssessmentRepository assessments){this.courses=courses;this.modules=modules;this.paths=paths;this.progress=progress;this.users=users;this.assessments=assessments;}
 public CourseAnalytics analytics(String email,UUID courseId){Course course=owned(email,courseId);List<LearnerPath> learnerPaths=paths.findByCourse_Id(courseId);int total=learnerPaths.size();long complete=learnerPaths.stream().filter(path->{var active=path.getModuleOrderJson().stream().filter(id->!path.getSkippedModuleIds().contains(id)).toList();return active.isEmpty()||progress.findByLearner_IdAndModule_Course_Id(path.getLearner().getId(),courseId).stream().map(e->e.getModule().getId()).filter(active::contains).count()>=active.size();}).count();Map<String,Long> dropoff=new LinkedHashMap<>();for(CourseModule module:modules.findByCourse_IdOrderByDefaultOrder(courseId)){long inactive=learnerPaths.stream().filter(path->!path.getSkippedModuleIds().contains(module.getId())).filter(path->progress.findByLearner_IdAndModule_Course_Id(path.getLearner().getId(),courseId).stream().noneMatch(e->e.getModule().getId().equals(module.getId()))).count();dropoff.put(module.getTitle(),inactive);}return new CourseAnalytics(courseId,total,total==0?0:round(complete*100.0/total),dropoff);}
 public List<HealthScore> healthScores(String email,UUID courseId){owned(email,courseId);return paths.findByCourse_Id(courseId).stream().map(path->{User learner=path.getLearner();var active=path.getModuleOrderJson().stream().filter(id->!path.getSkippedModuleIds().contains(id)).toList();double completion=active.isEmpty()?100:progress.findByLearner_IdAndModule_Course_Id(learner.getId(),courseId).stream().map(e->e.getModule().getId()).filter(active::contains).count()*100.0/active.size();double recency=recencyScore(learner);double assessment=assessments.findFirstByLearner_IdOrderByCompletedAtDesc(learner.getId()).map(item->item.getOverallScore().doubleValue()).orElse(0.0);double score=round(completion*.5+recency*.3+assessment*.2);return new HealthScore(learner.getId(),learner.getName(),learner.getEmail(),score,score>=75?"Good":score>=50?"At-Risk":"Critical");}).toList();}
 private double recencyScore(User learner){if(learner.getLastLoginAt()==null)return 0;long days=Duration.between(learner.getLastLoginAt(),Instant.now()).toDays();return days<=1?100:days<=3?75:days<=7?50:25;}
 private Course owned(String email,UUID courseId){User admin=users.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User not found"));Course course=courses.findById(courseId).orElseThrow(()->new IllegalArgumentException("Course not found"));if(!admin.getId().equals(course.getAdminId()))throw new IllegalArgumentException("Course is not managed by this administrator");return course;}
 private double round(double value){return Math.round(value*100.0)/100.0;}
 public record CourseAnalytics(UUID courseId,int totalLearners,double completionRate,Map<String,Long> dropOffModules){}
 public record HealthScore(UUID learnerId,String name,String email,double score,String status){}
}
