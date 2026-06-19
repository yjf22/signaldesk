package com.signaldesk.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signaldesk.audit.domain.AuditAction;
import com.signaldesk.audit.domain.AuditLog;
import com.signaldesk.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Record an audit log entry. Runs in its own transaction so it doesn't
     * interfere with the main business transaction.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long userId, AuditAction action, String targetType,
                       Long targetId, Map<String, Object> details, String ipAddress) {
        try {
            String detailJson = details != null ? objectMapper.writeValueAsString(details) : null;

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .detailJson(detailJson)
                    .ipAddress(ipAddress)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit details", e);
        }
    }
}
