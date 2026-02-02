package com.collectorcoin.service;

import java.math.BigInteger;

/**
 * Service interface for blockchain interactions.
 */
public interface BlockchainService {

    /**
     * Mint a new dNFT for a project
     * @param projectAddress Project contract address
     * @param valueEst Value estimation in wei
     * @param repairEst Repair cost estimation in wei
     * @param metadataURI IPFS metadata URI
     * @return tokenId The minted token ID
     */
    Long mintDNFT(String projectAddress, BigInteger valueEst,
                  BigInteger repairEst, String metadataURI);

    /**
     * Launch dNFT in marketplace
     * @param tokenId The token ID to launch
     */
    void launchDNFT(Long tokenId);

    /**
     * Add investor information to NFT
     * @param tokenId The token ID
     * @param investorAddress Investor wallet address
     * @param amount Investment amount in wei
     */
    void addNFTInvestor(Long tokenId, String investorAddress, BigInteger amount);

    /**
     * Update restorer address
     * @param tokenId The token ID
     * @param restorerAddress Restorer wallet address
     */
    void updateNFTRestorer(Long tokenId, String restorerAddress);

    /**
     * Mark restoration as complete
     * @param tokenId The token ID
     */
    void markRestorationComplete(Long tokenId);

    /**
     * Set NFT for auction with price
     * @param tokenId The token ID
     * @param auctionPrice Auction price in wei
     */
    void setNFTAuctionLive(Long tokenId, BigInteger auctionPrice);

    /**
     * Transfer NFT to auction winner
     * @param tokenId The token ID
     * @param winnerAddress Winner wallet address
     */
    void transferNFTToWinner(Long tokenId, String winnerAddress);

    /**
     * Redistribute funds to investors based on investment ratio
     * @param tokenId The token ID
     * @param totalAmount Total amount to redistribute in wei
     */
    void redistributeFunds(Long tokenId, BigInteger totalAmount);
}
