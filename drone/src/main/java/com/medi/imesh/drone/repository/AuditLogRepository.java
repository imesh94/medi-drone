package com.medi.imesh.drone.repository;

import com.medi.imesh.drone.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Audit Log instances.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
