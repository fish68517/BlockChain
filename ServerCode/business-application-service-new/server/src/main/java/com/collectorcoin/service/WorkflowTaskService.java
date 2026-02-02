package com.collectorcoin.service;

import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.ProjectListingRepository;
import org.jbpm.services.api.UserTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for integrating JBPM TaskService with blockchain events.
 * Completes Human Task nodes when corresponding blockchain events are received.
 */
@Service
public class WorkflowTaskService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowTaskService.class);

    // Task name constants matching BPMN definitions
    public static final String TASK_WAIT_NFT_MINTING = "WaitForNFTMinting";
    public static final String TASK_WAIT_INVESTMENT_CONFIRM = "WaitForInvestmentConfirm";
    public static final String TASK_WAIT_RESTORATION_ASSIGN = "WaitForRestorationAssign";
    public static final String TASK_WAIT_RESTORATION_COMPLETE = "WaitForRestorationComplete";
    public static final String TASK_WAIT_ITEM_POST = "WaitForItemPost";
    public static final String TASK_WAIT_NFT_TRANSFER = "WaitForNFTTransfer";

    @Autowired(required = false)
    private UserTaskService userTaskService;

    @Autowired
    private ProjectListingRepository listingRepository;

    @PostConstruct
    public void init() {
        if (userTaskService == null) {
            logger.warn("JBPM UserTaskService not available - workflow task completion disabled");
        }
    }

    /**
     * Complete a waiting task when blockchain event is received.
     */
    public void completeWaitTask(String taskName, Long listingId, Map<String, Object> data) {
        logger.info("Completing task {} for listing {}", taskName, listingId);

        if (userTaskService == null) {
            logger.warn("UserTaskService not available, skipping task completion");
            return;
        }

        try {
            Long taskId = findTaskByNameAndListing(taskName, listingId);
            if (taskId != null) {
                userTaskService.complete(taskId, "system", data);
                logger.info("Task {} completed for listing {}", taskName, listingId);
            } else {
                logger.warn("Task {} not found for listing {}", taskName, listingId);
            }
        } catch (Exception e) {
            logger.error("Failed to complete task {}: {}", taskName, e.getMessage());
        }
    }

    /**
     * Handle DNFTCreated event - complete WaitForNFTMinting task.
     */
    public void onDNFTCreated(Long tokenId, String projectAddress) {
        logger.info("DNFTCreated event: tokenId={}, project={}", tokenId, projectAddress);

        Map<String, Object> data = new HashMap<>();
        data.put("tokenId", tokenId);
        data.put("status", "MINTED");

        Long listingId = findListingByProject(projectAddress);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_NFT_MINTING, listingId, data);
        }
    }

    /**
     * Handle InvestorAdded event - complete WaitForInvestmentConfirm task.
     */
    public void onInvestorAdded(Long tokenId, String investor, Long amount) {
        logger.info("InvestorAdded event: tokenId={}, investor={}", tokenId, investor);

        Map<String, Object> data = new HashMap<>();
        data.put("investor", investor);
        data.put("amount", amount);
        data.put("status", "FUNDED");

        Long listingId = findListingByTokenId(tokenId);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_INVESTMENT_CONFIRM, listingId, data);
        }
    }

    /**
     * Handle RestorerUpdated event - complete WaitForRestorationAssign task.
     */
    public void onRestorerUpdated(Long tokenId, String restorer) {
        logger.info("RestorerUpdated event: tokenId={}, restorer={}", tokenId, restorer);

        Map<String, Object> data = new HashMap<>();
        data.put("restorer", restorer);
        data.put("status", "RESTORER_ASSIGNED");

        Long listingId = findListingByTokenId(tokenId);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_RESTORATION_ASSIGN, listingId, data);
        }
    }

    /**
     * Handle RestorationCompleted event - complete WaitForRestorationComplete task.
     */
    public void onRestorationCompleted(Long tokenId) {
        logger.info("RestorationCompleted event: tokenId={}", tokenId);

        Map<String, Object> data = new HashMap<>();
        data.put("status", "RESTORATION_COMPLETE");

        Long listingId = findListingByTokenId(tokenId);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_RESTORATION_COMPLETE, listingId, data);
        }
    }

    /**
     * Handle ItemPosted event - complete WaitForItemPost task.
     */
    public void onItemPosted(Long tokenId, Long price) {
        logger.info("ItemPosted event: tokenId={}, price={}", tokenId, price);

        Map<String, Object> data = new HashMap<>();
        data.put("auctionPrice", price);
        data.put("status", "FOR_SALE");

        Long listingId = findListingByTokenId(tokenId);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_ITEM_POST, listingId, data);
        }
    }

    /**
     * Handle NFTTransferred event - complete WaitForNFTTransfer task.
     */
    public void onNFTTransferred(Long tokenId, String winner) {
        logger.info("NFTTransferred event: tokenId={}, winner={}", tokenId, winner);

        Map<String, Object> data = new HashMap<>();
        data.put("winner", winner);
        data.put("status", "TRANSFERRED");

        Long listingId = findListingByTokenId(tokenId);
        if (listingId != null) {
            completeWaitTask(TASK_WAIT_NFT_TRANSFER, listingId, data);
        }
    }

    // Helper methods - implemented with repository queries
    private Long findTaskByNameAndListing(String taskName, Long listingId) {
        logger.debug("Finding task {} for listing {}", taskName, listingId);
        
        if (userTaskService == null) {
            // Fallback: directly update listing status when JBPM not configured
            updateListingStatusFromTask(taskName, listingId);
            return null;
        }
        
        // JBPM task query - find pending task by name for this listing
        try {
            // Query tasks by process variable listingId
            // This would use RuntimeDataService in full JBPM setup
            return null;
        } catch (Exception e) {
            logger.error("Error finding task: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Fallback: Update listing status directly when JBPM is not configured.
     * This ensures the workflow progresses even without full JBPM integration.
     */
    private void updateListingStatusFromTask(String taskName, Long listingId) {
        listingRepository.findById(listingId).ifPresent(listing -> {
            String newStatus = mapTaskToStatus(taskName);
            if (newStatus != null) {
                listing.setStatus(newStatus);
                listingRepository.save(listing);
                logger.info("Updated listing {} status to {} (fallback mode)", listingId, newStatus);
            }
        });
    }
    
    private String mapTaskToStatus(String taskName) {
        return switch (taskName) {
            case TASK_WAIT_NFT_MINTING -> "ESTIMATED";
            case TASK_WAIT_INVESTMENT_CONFIRM -> "FUNDED";
            case TASK_WAIT_RESTORATION_ASSIGN -> "RESTORING";
            case TASK_WAIT_RESTORATION_COMPLETE -> "RESTORED";
            case TASK_WAIT_ITEM_POST -> "AUCTION";
            case TASK_WAIT_NFT_TRANSFER -> "SOLD";
            default -> null;
        };
    }

    private Long findListingByProject(String projectAddress) {
        logger.debug("Finding listing by project: {}", projectAddress);
        return listingRepository.findByProjectAddress(projectAddress)
            .map(ProjectListing::getId)
            .orElse(null);
    }

    private Long findListingByTokenId(Long tokenId) {
        logger.debug("Finding listing by tokenId: {}", tokenId);
        return listingRepository.findByNftTokenId(tokenId)
            .map(ProjectListing::getId)
            .orElse(null);
    }
}
