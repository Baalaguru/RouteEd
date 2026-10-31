package com.routeed.learning;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/learner")
public class ProgressController {
 private final ProgressService progress; public ProgressController(ProgressService progress){this.progress=progress;}
 @PostMapping("/modules/{moduleId}/complete") @ResponseStatus(HttpStatus.NO_CONTENT) public void complete(@AuthenticationPrincipal UserDetails principal,@PathVariable UUID moduleId){progress.complete(principal.getUsername(),moduleId);}
 @GetMapping("/courses/{courseId}/progress") public ProgressService.ProgressResponse get(@AuthenticationPrincipal UserDetails principal,@PathVariable UUID courseId){return progress.progress(principal.getUsername(),courseId);}
}
