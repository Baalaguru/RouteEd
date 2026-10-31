package com.routeed.analytics;
import java.util.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin/courses") public class AnalyticsController {
 private final AnalyticsService analytics; public AnalyticsController(AnalyticsService analytics){this.analytics=analytics;}
 @GetMapping("/{courseId}/analytics") public AnalyticsService.CourseAnalytics analytics(@AuthenticationPrincipal UserDetails principal,@PathVariable UUID courseId){return analytics.analytics(principal.getUsername(),courseId);}
 @GetMapping("/{courseId}/health-scores") public List<AnalyticsService.HealthScore> health(@AuthenticationPrincipal UserDetails principal,@PathVariable UUID courseId){return analytics.healthScores(principal.getUsername(),courseId);}
}
