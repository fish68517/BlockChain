package com.collectorcoin.service;

import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.ProjectListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Service for handling investment proxy operations.
 * Admin wallet pays gas fees while recording investor contributions.
 */
@Service
public class InvestmentProxyService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentProxyService.class);

    private final BlockchainService blockchainService;
    private final ProjectListingRepository projectListingRepository;

    public InvestmentProxyService(BlockchainService blockchainService,
                                  ProjectListingRepository projectListingRepository) {
        this.blockchainService = blockchainService;
        this.projectListingRepository = projectListingRepository;
    }

    /**
     * Process investment for a project.
     * @param projectId Project ID
     * @param investorAddress Investor wallet address
     * @param amount Investment amount in ETH
     */
    @Transactional
    public void processInvestment(Long projectId, String investorAddress, BigDecimal amount) {
        logger.info("Processing investment: project={}, investor={}, amount={}",
            projectId, investorAddress, amount);

        ProjectListing project = projectListingRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (project.getNftTokenId() == null) {
            throw new IllegalStateException("Project NFT not minted yet");
        }

        if (!project.getNftLaunched()) {
            throw new IllegalStateException("Project NFT not launched yet");
        }

        BigInteger amountWei = toWei(amount);

        blockchainService.addNFTInvestor(
            project.getNftTokenId(),
            investorAddress,
            amountWei
        );

        // Update current funding
        BigDecimal currentFunding = project.getCurrentFunding();
        if (currentFunding == null) {
            currentFunding = BigDecimal.ZERO;
        }
        currentFunding = currentFunding.add(amount);
        project.setCurrentFunding(currentFunding);

        // Check if funding target reached
        BigDecimal fundingTarget = project.getFundingTarget();
        if (fundingTarget != null && currentFunding.compareTo(fundingTarget) >= 0) {
            logger.info("Funding target reached for project {}, auto-assigning restorer", projectId);
            project.setStatus("FUNDED");
            projectListingRepository.save(project);

            // Auto assign restorer if selected
            if (project.getSelectedRestorerId() != null) {
                // Get restorer address from RestorerBid
                autoAssignRestorer(project);
            }
        } else {
            project.setStatus("FUNDING");
            projectListingRepository.save(project);
        }

        logger.info("Investment processed successfully for project {}", projectId);
    }

    /**
     * Auto assign restorer when funding target is reached
     */
    private void autoAssignRestorer(ProjectListing project) {
        logger.info("Auto-assigning restorer for project {}", project.getId());

        String restorerAddress = project.getSelectedRestorerAddress();
        if (restorerAddress == null || restorerAddress.isEmpty()) {
            logger.warn("No restorer address found for project {}", project.getId());
            return;
        }

        // Call blockchain to update restorer
        blockchainService.updateNFTRestorer(project.getNftTokenId(), restorerAddress);

        // Update status to RESTORING
        project.setStatus("RESTORING");
        projectListingRepository.save(project);

        logger.info("Restorer {} assigned to project {}", restorerAddress, project.getId());
    }

    /**
     * Assign restorer to a project.
     * @param projectId Project ID
     * @param restorerAddress Restorer wallet address
     */
    @Transactional
    public void assignRestorer(Long projectId, String restorerAddress) {
        logger.info("Assigning restorer: project={}, restorer={}", projectId, restorerAddress);

        ProjectListing project = projectListingRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (project.getNftTokenId() == null) {
            throw new IllegalStateException("Project NFT not minted yet");
        }

        blockchainService.updateNFTRestorer(project.getNftTokenId(), restorerAddress);

        // Update status to RESTORING after restorer assigned
        project.setStatus("RESTORING");
        projectListingRepository.save(project);

        logger.info("Restorer assigned successfully for project {}", projectId);
    }

    /**
     * Complete restoration for a project.
     * @param projectId Project ID
     */
    @Transactional
    public void completeRestoration(Long projectId) {
        logger.info("Completing restoration for project {}", projectId);

        ProjectListing project = projectListingRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (project.getNftTokenId() == null) {
            throw new IllegalStateException("Project NFT not minted yet");
        }

        blockchainService.markRestorationComplete(project.getNftTokenId());

        // Update status to RESTORED after restoration complete
        project.setStatus("RESTORED");
        projectListingRepository.save(project);

        logger.info("Restoration completed for project {}", projectId);
    }

    /**
     * Post item for auction.
     * @param projectId Project ID
     * @param auctionPrice Auction price in ETH
     */
    @Transactional
    public void postForAuction(Long projectId, BigDecimal auctionPrice) {
        logger.info("Posting for auction: project={}, price={}", projectId, auctionPrice);

        ProjectListing project = projectListingRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (project.getNftTokenId() == null) {
            throw new IllegalStateException("Project NFT not minted yet");
        }

        BigInteger priceWei = toWei(auctionPrice);

        blockchainService.setNFTAuctionLive(project.getNftTokenId(), priceWei);

        // Update status to AUCTION and save auction price
        project.setStatus("AUCTION");
        project.setValueEstimation(auctionPrice); // Update to auction price for display
        projectListingRepository.save(project);

        logger.info("Item posted for auction: project {}", projectId);
    }

    /**
     * Transfer NFT to auction winner.
     * @param projectId Project ID
     * @param winnerAddress Winner wallet address
     */
    @Transactional
    public void transferToWinner(Long projectId, String winnerAddress) {
        logger.info("Transferring to winner: project={}, winner={}", projectId, winnerAddress);

        ProjectListing project = projectListingRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (project.getNftTokenId() == null) {
            throw new IllegalStateException("Project NFT not minted yet");
        }

        blockchainService.transferNFTToWinner(project.getNftTokenId(), winnerAddress);

        // Redistribute funds to investors based on auction price
        if (project.getValueEstimation() != null) {
            BigInteger auctionPriceWei = toWei(project.getValueEstimation());
            blockchainService.redistributeFunds(project.getNftTokenId(), auctionPriceWei);
            logger.info("Funds redistributed for project {}", projectId);
        }

        // Update status to SOLD after NFT transfer
        project.setStatus("SOLD");
        projectListingRepository.save(project);

        logger.info("NFT transferred to winner for project {}", projectId);
    }

    private BigInteger toWei(BigDecimal ethAmount) {
        return ethAmount.multiply(BigDecimal.TEN.pow(18)).toBigInteger();
    }
}
