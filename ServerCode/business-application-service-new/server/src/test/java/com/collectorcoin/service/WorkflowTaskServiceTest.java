package com.collectorcoin.service;

import org.jbpm.services.api.UserTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowTaskServiceTest {

    @Mock
    private UserTaskService userTaskService;

    @InjectMocks
    private WorkflowTaskService workflowTaskService;

    @Test
    void onDNFTCreated_shouldLogEvent() {
        workflowTaskService.onDNFTCreated(1L, "0x123");
        // Verify no exception thrown
    }

    @Test
    void onInvestorAdded_shouldLogEvent() {
        workflowTaskService.onInvestorAdded(1L, "0x456", 1000L);
    }

    @Test
    void onRestorerUpdated_shouldLogEvent() {
        workflowTaskService.onRestorerUpdated(1L, "0x789");
    }

    @Test
    void onRestorationCompleted_shouldLogEvent() {
        workflowTaskService.onRestorationCompleted(1L);
    }

    @Test
    void onItemPosted_shouldLogEvent() {
        workflowTaskService.onItemPosted(1L, 50000L);
    }

    @Test
    void onNFTTransferred_shouldLogEvent() {
        workflowTaskService.onNFTTransferred(1L, "0xabc");
    }

    @Test
    void completeWaitTask_withNullService_shouldNotThrow() {
        WorkflowTaskService service = new WorkflowTaskService();
        service.onDNFTCreated(1L, "0x123");
    }
}
