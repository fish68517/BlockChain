package com.collectorcoin.service;

import com.collectorcoin.contracts.CCMarketPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;

/**
 * Service for executing blockchain transactions as admin.
 */
@Service
public class BlockchainProxyService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainProxyService.class);

    private final Web3j web3j;
    private final Credentials adminCredentials;
    private final ContractGasProvider gasProvider;

    @Value("${blockchain.contract.marketplace.address}")
    private String marketplaceAddress;

    private CCMarketPlace marketplace;

    public BlockchainProxyService(Web3j web3j, Credentials adminCredentials,
                                  ContractGasProvider gasProvider) {
        this.web3j = web3j;
        this.adminCredentials = adminCredentials;
        this.gasProvider = gasProvider;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing BlockchainProxyService");
        logger.info("Admin wallet: {}", adminCredentials.getAddress());

        if (marketplaceAddress != null && !marketplaceAddress.startsWith("$")) {
            marketplace = CCMarketPlace.load(
                marketplaceAddress, web3j, adminCredentials, gasProvider);
            logger.info("Marketplace loaded at: {}", marketplaceAddress);
        }
    }

    public TransactionReceipt mintDNFT(String projectAddress, BigInteger valueEst,
                                       BigInteger repairEst, String metadataURI) throws Exception {
        logger.info("Minting dNFT for project: {}", projectAddress);
        TransactionReceipt receipt = marketplace.mintDNFT(
            projectAddress, valueEst, repairEst, metadataURI).send();
        logger.info("dNFT minted, tx: ", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt launchDNFT(BigInteger tokenId) throws Exception {
        logger.info("Launching dNFT: {}", tokenId);
        TransactionReceipt receipt = marketplace.launchDNFT(tokenId).send();
        logger.info("dNFT launched, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt addInvestor(BigInteger tokenId, String investor,
                                          BigInteger amount) throws Exception {
        logger.info("Adding investor {} to token {}", investor, tokenId);
        TransactionReceipt receipt = marketplace.addInvestor(tokenId, investor, amount).send();
        logger.info("Investor added, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public String getAdminAddress() {
        return adminCredentials.getAddress();
    }

    public TransactionReceipt updateRestorer(BigInteger tokenId, String restorer) throws Exception {
        logger.info("Updating restorer for token {}: {}", tokenId, restorer);
        TransactionReceipt receipt = marketplace.updateRestorer(tokenId, restorer).send();
        logger.info("Restorer updated, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt markRestorationComplete(BigInteger tokenId) throws Exception {
        logger.info("Marking restoration complete for token {}", tokenId);
        TransactionReceipt receipt = marketplace.markRestorationComplete(tokenId).send();
        logger.info("Restoration marked complete, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt setItemForSale(BigInteger tokenId, BigInteger price) throws Exception {
        logger.info("Setting item for sale: token={}, price={}", tokenId, price);
        TransactionReceipt receipt = marketplace.setItemForSale(tokenId, price).send();
        logger.info("Item set for sale, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt transferToWinner(BigInteger tokenId, String winner) throws Exception {
        logger.info("Transferring token {} to winner {}", tokenId, winner);
        TransactionReceipt receipt = marketplace.transferToWinner(tokenId, winner).send();
        logger.info("Token transferred, tx: {}", receipt.getTransactionHash());
        return receipt;
    }

    public TransactionReceipt redistributeFunds(BigInteger tokenId, BigInteger totalAmount) throws Exception {
        logger.info("Redistributing funds for token {}: {} wei", tokenId, totalAmount);
        TransactionReceipt receipt = marketplace.redistributeFunds(tokenId, totalAmount).send();
        logger.info("Funds redistributed, tx: {}", receipt.getTransactionHash());
        return receipt;
    }
}
