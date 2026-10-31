package com.routeed.communication;
import com.routeed.learning.LearnerPath;
import com.routeed.user.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service public class EmailService {
 private final JavaMailSender sender; private final EmailLogRepository logs;
 public EmailService(JavaMailSender sender,EmailLogRepository logs){this.sender=sender;this.logs=logs;}
 @Async public void sendWelcomeEmail(User learner,LearnerPath path){send(learner,EmailType.WELCOME,"Your RouteEd path is ready","<h1>Welcome, "+learner.getName()+".</h1><p>Your learning path has been personalized. "+path.getAiRationaleJson().values().stream().findFirst().orElse("")+"</p>");}
 @Async public void sendWeeklySummary(User learner,String summary){send(learner,EmailType.WEEKLY_SUMMARY,"Your RouteEd weekly update","<h1>Your weekly update</h1><p>Hi "+learner.getName()+",</p><p>"+summary+"</p>");}
 private void send(User learner,EmailType type,String subject,String html){EmailLog log=logs.save(new EmailLog(learner,type));try{MimeMessage message=sender.createMimeMessage();MimeMessageHelper helper=new MimeMessageHelper(message,true,"UTF-8");helper.setTo(learner.getEmail());helper.setSubject(subject);helper.setText(html,true);sender.send(message);log.markSent();}catch(Exception error){log.markFailed();}finally{logs.save(log);}}
}
