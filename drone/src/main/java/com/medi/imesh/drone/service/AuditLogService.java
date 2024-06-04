package com.medi.imesh.drone.service;

import com.medi.imesh.drone.repository.AuditLogRepository;
import com.medi.imesh.drone.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing audit logs.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Get all audit logs in the system within the page limit.
     *
     * @param pageable Pagination data
     * @return A page of audit logs.
     */
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
