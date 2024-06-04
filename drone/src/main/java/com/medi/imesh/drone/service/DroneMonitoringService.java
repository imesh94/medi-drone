package com.medi.imesh.drone.service;

import com.medi.imesh.drone.repository.AuditLogRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import com.medi.imesh.drone.common.ApplicationConstants;
import com.medi.imesh.drone.model.AuditLog;
import com.medi.imesh.drone.model.Drone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for monitoring drone battery levels and logging any issues found.
 * This service periodically checks the battery levels of all drones in the system and logs
 * any that have a battery capacity below a specified threshold.
 */
@Service
public class DroneMonitoringService {

    private final DroneRepository droneRepository;
    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(DroneMonitoringService.class);


    public DroneMonitoringService(DroneRepository droneRepository, AuditLogRepository auditLogRepository) {
        this.droneRepository = droneRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Periodically checks the battery levels of all drones in the system.
     * Drones with battery capacity below threshold are logged and recorded in the audit log.
     */
    @Scheduled(fixedRate = ApplicationConstants.MONITORING_INTERVAL)
    public void checkDronesBatteryLevel() {

        List<Drone> drones = droneRepository.findAll();
        for (Drone drone : drones) {
            if (drone.getBatteryCapacity() < ApplicationConstants.LOADING_BATTERY_THRESHOLD) {
                String message = String.format("Drone %s has low battery level: %d", drone.getSerialNumber(),
                        drone.getBatteryCapacity());
                logger.info(message);
                auditLogRepository.save(new AuditLog(message));
            }
        }
    }
}
