package com.medi.imesh.drone.controller;

import com.medi.imesh.drone.model.AuditLog;
import com.medi.imesh.drone.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling audit log requests.
 */
@RestController
@RequestMapping("/logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Get all audit logs of the system.
     *
     * @param pageable - Pagination data
     * @return - Audit log response
     */
    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(auditLogs);
    }

}
