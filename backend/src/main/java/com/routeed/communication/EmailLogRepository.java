package com.routeed.communication;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> { }
