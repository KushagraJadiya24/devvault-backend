package dev.kushagra.devvault.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kushagra.devvault.model.AuditEvent;
import dev.kushagra.devvault.model.AuditLog;
import dev.kushagra.devvault.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    private static final String AUDIT_QUEUE = "audit:events";

    public void publishEvent(Long userId, String action, String secretName, String ipAddress) {
        try {
            AuditEvent event = new AuditEvent(userId, action, secretName, ipAddress);
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.opsForList().leftPush(AUDIT_QUEUE, json);
        } catch (Exception e) {
            log.error("Failed to publish audit event: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void processEvents() {
        try {
            String json = redisTemplate.opsForList().rightPop(AUDIT_QUEUE);
            while (json != null) {
                AuditEvent event = objectMapper.readValue(json, AuditEvent.class);
                AuditLog log = new AuditLog();
                log.setUserId(event.getUserId());
                log.setAction(event.getAction());
                log.setSecretName(event.getSecretName());
                log.setIpAddress(event.getIpAddress());
                auditLogRepository.save(log);
                json = redisTemplate.opsForList().rightPop(AUDIT_QUEUE);
            }
        } catch (Exception e) {
            log.error("Failed to process audit event: {}", e.getMessage());
        }
    }
}