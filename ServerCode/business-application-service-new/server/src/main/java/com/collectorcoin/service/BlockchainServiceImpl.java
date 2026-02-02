package com.collectorcoin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of BlockchainService.
 */
@Service
public class BlockchainServiceImpl implements BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);

    private final BlockchainProxyService proxyService;
    
    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;
    
    // Mock token ID counter for demo mode
    private final AtomicLong mockTokenIdCounter = new AtomicLong(1000);

    public BlockchainServiceImpl(BlockchainProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public Long mintDNFT(String projectAddress, BigInteger valueEst,
                         BigInteger repairEst, String metadataURI) {
        if (!blockchainEnabled) {
            // Demo mode - return mock token ID
            Long mockTokenId = mockTokenIdCounter.incrementAndGet();
            logger.info("[DEMO MODE] Mock dNFT minted with tokenId: {}", mockTokenId);
            return mockTokenId;
        }
        
        try {
            TransactionReceipt receipt = proxyService.mintDNFT(
                projectAddress, valueEst, repairEst, metadataURI);
            logger.info("dNFT minted: {}", receipt.getTransactionHash());
            return 0L; // Token ID from event
        } catch (Exception e) {
            logger.error("Failed to mint dNFT", e);
            throw new RuntimeException("Mint failed", e);
        }
    }

    @Override
    public void launchDNFT(Long tokenId) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock dNFT launched: {}", tokenId);
            return;
        }
        try {
            proxyService.launchDNFT(BigInteger.valueOf(tokenId));
            logger.info("dNFT launched: {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to launch dNFT", e);
            throw new RuntimeException("Launch failed", e);
        }
    }

    @Override
    public void addNFTInvestor(Long tokenId, String investorAddress, BigInteger amount) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock investor {} added to token {}", investorAddress, tokenId);
            return;
        }
        try {
            proxyService.addInvestor(BigInteger.valueOf(tokenId), investorAddress, amount);
            logger.info("Investor added to token {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to add investor", e);
            throw new RuntimeException("Add investor failed", e);
        }
    }

    @Override
    public void updateNFTRestorer(Long tokenId, String restorerAddress) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock restorer {} set for token {}", restorerAddress, tokenId);
            return;
        }
        try {
            proxyService.updateRestorer(BigInteger.valueOf(tokenId), restorerAddress);
            logger.info("Restorer updated for token {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to update restorer", e);
            throw new RuntimeException("Update restorer failed", e);
        }
    }

    @Override
    public void markRestorationComplete(Long tokenId) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock restoration complete for token {}", tokenId);
            return;
        }
        try {
            proxyService.markRestorationComplete(BigInteger.valueOf(tokenId));
            logger.info("Restoration marked complete for token {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to mark restoration complete", e);
            throw new RuntimeException("Mark restoration complete failed", e);
        }
    }

    @Override
    public void setNFTAuctionLive(Long tokenId, BigInteger auctionPrice) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock auction live for token {} at price {}", tokenId, auctionPrice);
            return;
        }
        try {
            proxyService.setItemForSale(BigInteger.valueOf(tokenId), auctionPrice);
            logger.info("Auction set live for token {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to set auction live", e);
            throw new RuntimeException("Set auction live failed", e);
        }
    }

    @Override
    public void transferNFTToWinner(Long tokenId, String winnerAddress) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock token {} transferred to {}", tokenId, winnerAddress);
            return;
        }
        try {
            proxyService.transferToWinner(BigInteger.valueOf(tokenId), winnerAddress);
            logger.info("Token {} transferred to winner {}", tokenId, winnerAddress);
        } catch (Exception e) {
            logger.error("Failed to transfer to winner", e);
            throw new RuntimeException("Transfer to winner failed", e);
        }
    }

    @Override
    public void redistributeFunds(Long tokenId, BigInteger totalAmount) {
        if (!blockchainEnabled) {
            logger.info("[DEMO MODE] Mock redistribute {} wei for token {}", totalAmount, tokenId);
            return;
        }
        try {
            proxyService.redistributeFunds(BigInteger.valueOf(tokenId), totalAmount);
            logger.info("Funds redistributed for token {}", tokenId);
        } catch (Exception e) {
            logger.error("Failed to redistribute funds", e);
            throw new RuntimeException("Redistribute funds failed", e);
        }
    }
}
