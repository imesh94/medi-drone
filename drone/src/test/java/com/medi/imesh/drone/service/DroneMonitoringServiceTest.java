package com.medi.imesh.drone.service;

import com.medi.imesh.drone.model.AuditLog;
import com.medi.imesh.drone.model.Drone;
import com.medi.imesh.drone.repository.AuditLogRepository;
import com.medi.imesh.drone.repository.DroneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.Arrays;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneMonitoringServiceTest {

    @Mock
    private DroneRepository droneRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private Logger logger;

    @InjectMocks
    private DroneMonitoringService droneMonitoringService;

    @Test
    void whenNoDronesHaveLowBattery_thenNoAuditLogsAreSaved() {
        Drone drone1 = new Drone();
        Drone drone2 = new Drone();
        drone1.setSerialNumber("DR001");
        drone1.setBatteryCapacity(30);
        drone2.setSerialNumber("DR002");
        drone2.setBatteryCapacity(50);

        when(droneRepository.findAll()).thenReturn(Arrays.asList(drone1, drone2));
        droneMonitoringService.checkDronesBatteryLevel();
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void whenDronesHaveLowBattery_thenAuditLogsAreSaved() {
        Drone droneWithLowBattery = new Drone();
        Drone droneWithOkBattery = new Drone();
        droneWithLowBattery.setSerialNumber("DR003");
        droneWithLowBattery.setBatteryCapacity(20);
        droneWithOkBattery.setSerialNumber("DR004");
        droneWithOkBattery.setBatteryCapacity(50);

        when(droneRepository.findAll()).thenReturn(Arrays.asList(droneWithLowBattery, droneWithOkBattery));
        droneMonitoringService.checkDronesBatteryLevel();
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

}
