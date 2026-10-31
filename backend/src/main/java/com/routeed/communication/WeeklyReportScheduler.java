package com.routeed.communication;
import com.routeed.learning.LearnerPathRepository;
import com.routeed.learning.OllamaLlmService;
import java.util.HashSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component public class WeeklyReportScheduler {
 private final LearnerPathRepository paths; private final EmailService emails; private final OllamaLlmService ollama;
 public WeeklyReportScheduler(LearnerPathRepository paths,EmailService emails,OllamaLlmService ollama){this.paths=paths;this.emails=emails;this.ollama=ollama;}
 @Scheduled(cron="0 0 9 * * MON") public void sendWeeklyReports(){var sent=new HashSet<java.util.UUID>();for(var path:paths.findAll())if(sent.add(path.getLearner().getId()))emails.sendWeeklySummary(path.getLearner(),ollama.weeklyEncouragement(path.getLearner().getName()));}
}
