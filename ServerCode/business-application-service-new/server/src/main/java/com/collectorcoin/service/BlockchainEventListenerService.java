package com.collectorcoin.service;

import com.collectorcoin.repository.ProjectListingRepository;
import com.collectorcoin.contracts.CCMarketPlace;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Service for listening to blockchain events.
 */
@Service
public class BlockchainEventListenerService {

    private static final Logger logger = LoggerFactory.getLogger(
        BlockchainEventListenerService.class);

    private final Web3j web3j;
    private final ProjectListingRepository repository;
    private final WorkflowTaskService workflowTaskService;

    @Value("${blockchain.contract.marketplace.address}")
    private String marketplaceAddress;

    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;

    private Disposable subscription;
    private CCMarketPlace contract;

    public BlockchainEventListenerService(Web3j web3j,
                                          ProjectListingRepository repository,
                                          WorkflowTaskService workflowTaskService) {
        this.web3j = web3j;
        this.repository = repository;
        this.workflowTaskService = workflowTaskService;
    }

    @PostConstruct
    public void startListening() {
        if (!blockchainEnabled) {
            logger.info("Blockchain disabled - event listener not started");
            return;
        }
        if (marketplaceAddress == null || marketplaceAddress.startsWith("$")) {
            logger.warn("Marketplace address not configured");
            return;
        }

        logger.info("Starting event listener for: {}", marketplaceAddress);

        try {
            ReadonlyTransactionManager txManager = new ReadonlyTransactionManager(
                web3j, marketplaceAddress);
            contract = CCMarketPlace.load(
                marketplaceAddress, web3j, txManager, new DefaultGasProvider());
            subscribeToEvents();
            logger.info("Event listener started with workflow integration");
        } catch (Exception e) {
            logger.error("Failed to start event listener: {}", e.getMessage());
        }
    }

    private void subscribeToEvents() {
        // DNFTCreated event
        contract.dNFTCreatedEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("DNFTCreated: tokenId={}", event.tokenId);
                workflowTaskService.onDNFTCreated(
                    event.tokenId.longValue(), event.projectAddress);
            }, this::handleError);

        // InvestorAdded event
        contract.investorAddedEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("InvestorAdded: tokenId={}", event.tokenId);
                workflowTaskService.onInvestorAdded(
                    event.tokenId.longValue(), event.investor, event.amount.longValue());
            }, this::handleError);

        // RestorerUpdated event
        contract.restorerUpdatedEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("RestorerUpdated: tokenId={}", event.tokenId);
                workflowTaskService.onRestorerUpdated(
                    event.tokenId.longValue(), event.restorer);
            }, this::handleError);

        subscribeToMoreEvents();
    }

    private void subscribeToMoreEvents() {
        // RestorationCompleted event
        contract.restorationCompletedEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("RestorationCompleted: tokenId={}", event.tokenId);
                workflowTaskService.onRestorationCompleted(event.tokenId.longValue());
            }, this::handleError);

        // ItemPosted event
        contract.itemPostedEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("ItemPosted: tokenId={}", event.tokenId);
                workflowTaskService.onItemPosted(
                    event.tokenId.longValue(), event.price.longValue());
            }, this::handleError);

        // NFTTransferred event
        contract.nFTTransferredEventFlowable(
            DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                logger.info("NFTTransferred: tokenId={}", event.tokenId);
                workflowTaskService.onNFTTransferred(
                    event.tokenId.longValue(), event.winner);
            }, this::handleError);
    }

    private void handleLog(Log log) {
        logger.debug("Received log: {}", log.getTransactionHash());
    }

    private void handleError(Throwable error) {
        logger.error("Event listener error: {}", error.getMessage());
        reconnect();
    }

    private void reconnect() {
        logger.info("Reconnecting...");
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        try {
            Thread.sleep(5000);
            startListening();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void stopListening() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            logger.info("Event listener stopped");
        }
    }
}
