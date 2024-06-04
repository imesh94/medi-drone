package com.medi.imesh.drone.service;

import com.medi.imesh.drone.model.AuditLog;
import com.medi.imesh.drone.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditLogServiceTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void getAllAuditLogs_ReturnsAllLogsPaged() {

        AuditLog log1 = new AuditLog("Drone 1 has low battery level: 20%");
        AuditLog log2 = new AuditLog("Drone 2 has low battery level: 15%");
        List<AuditLog> mockLogs = Arrays.asList(log1, log2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<AuditLog> mockPage = new PageImpl<>(mockLogs, pageable, mockLogs.size());

        when(auditLogRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<AuditLog> retrievedPage = auditLogService.getAllAuditLogs(pageable);

        assertNotNull(retrievedPage, "The retrieved page of audit logs should not be null.");
        assertEquals(2, retrievedPage.getTotalElements(), "The total elements in the retrieved " +
                "page should match the mock data.");
        assertEquals(mockLogs, retrievedPage.getContent(), "The content of the retrieved page should match " +
                "the mock data.");

        verify(auditLogRepository).findAll(pageable);
    }

}
