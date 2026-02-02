package com.collectorcoin.contracts;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Web3j wrapper for CCMarketPlace smart contract.
 */
public class CCMarketPlace extends Contract {

    public static final String BINARY = "";

    public CCMarketPlace(String contractAddress, Web3j web3j,
                         Credentials credentials, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, credentials, gasProvider);
    }

    public CCMarketPlace(String contractAddress, Web3j web3j,
                         TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> mintDNFT(
            String projectAddress, BigInteger valueEst,
            BigInteger repairEst, String metadataURI) {
        final Function function = new Function(
            "mintDNFT",
            Arrays.asList(
                new Address(projectAddress),
                new Uint256(valueEst),
                new Uint256(repairEst),
                new Utf8String(metadataURI)
            ),
            Arrays.asList(new TypeReference<Uint256>() {})
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> launchDNFT(BigInteger tokenId) {
        final Function function = new Function(
            "launchDNFT",
            Arrays.asList(new Uint256(tokenId)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addInvestor(
            BigInteger tokenId, String investor, BigInteger amount) {
        final Function function = new Function(
            "addInvestor",
            Arrays.asList(
                new Uint256(tokenId),
                new Address(investor),
                new Uint256(amount)
            ),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateRestorer(
            BigInteger tokenId, String restorer) {
        final Function function = new Function(
            "updateRestorer",
            Arrays.asList(new Uint256(tokenId), new Address(restorer)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> markRestorationComplete(
            BigInteger tokenId) {
        final Function function = new Function(
            "markRestorationComplete",
            Arrays.asList(new Uint256(tokenId)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setItemForSale(
            BigInteger tokenId, BigInteger price) {
        final Function function = new Function(
            "setItemForSale",
            Arrays.asList(new Uint256(tokenId), new Uint256(price)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferToWinner(
            BigInteger tokenId, String winner) {
        final Function function = new Function(
            "transferToWinner",
            Arrays.asList(new Uint256(tokenId), new Address(winner)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> redistributeFunds(
            BigInteger tokenId, BigInteger totalAmount) {
        final Function function = new Function(
            "redistributeFunds",
            Arrays.asList(new Uint256(tokenId), new Uint256(totalAmount)),
            Arrays.asList()
        );
        return executeRemoteCallTransaction(function);
    }

    public static CCMarketPlace load(String contractAddress, Web3j web3j,
                                     Credentials credentials,
                                     ContractGasProvider gasProvider) {
        return new CCMarketPlace(contractAddress, web3j, credentials, gasProvider);
    }

    public static CCMarketPlace load(String contractAddress, Web3j web3j,
                                     TransactionManager transactionManager,
                                     ContractGasProvider gasProvider) {
        return new CCMarketPlace(contractAddress, web3j, transactionManager, gasProvider);
    }

    // Event response classes
    public static class DNFTCreatedEventResponse {
        public BigInteger tokenId;
        public String projectAddress;
    }

    public static class InvestorAddedEventResponse {
        public BigInteger tokenId;
        public String investor;
        public BigInteger amount;
    }

    public static class RestorerUpdatedEventResponse {
        public BigInteger tokenId;
        public String restorer;
    }

    public static class RestorationCompletedEventResponse {
        public BigInteger tokenId;
    }

    public static class ItemPostedEventResponse {
        public BigInteger tokenId;
        public BigInteger price;
    }

    public static class NFTTransferredEventResponse {
        public BigInteger tokenId;
        public String winner;
    }

    // Event definitions
    public static final Event DNFT_CREATED_EVENT = new Event("DNFTCreated",
        Arrays.asList(
            new TypeReference<Uint256>(true) {},
            new TypeReference<Address>() {}
        ));

    public static final Event INVESTOR_ADDED_EVENT = new Event("InvestorAdded",
        Arrays.asList(
            new TypeReference<Uint256>(true) {},
            new TypeReference<Address>() {},
            new TypeReference<Uint256>() {}
        ));

    public static final Event RESTORER_UPDATED_EVENT = new Event("RestorerUpdated",
        Arrays.asList(
            new TypeReference<Uint256>(true) {},
            new TypeReference<Address>() {}
        ));

    public static final Event RESTORATION_COMPLETED_EVENT = new Event("RestorationCompleted",
        Arrays.asList(new TypeReference<Uint256>(true) {}));

    public static final Event ITEM_POSTED_EVENT = new Event("ItemPosted",
        Arrays.asList(
            new TypeReference<Uint256>(true) {},
            new TypeReference<Uint256>() {}
        ));

    public static final Event NFT_TRANSFERRED_EVENT = new Event("NFTTransferred",
        Arrays.asList(
            new TypeReference<Uint256>(true) {},
            new TypeReference<Address>() {}
        ));

    // Event flowable methods
    public Flowable<DNFTCreatedEventResponse> dNFTCreatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DNFT_CREATED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            DNFTCreatedEventResponse response = new DNFTCreatedEventResponse();
            List<Type> results = extractEventParametersWithLog(DNFT_CREATED_EVENT, log).getNonIndexedValues();
            List<Type> indexed = extractEventParametersWithLog(DNFT_CREATED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            response.projectAddress = (String) results.get(0).getValue();
            return response;
        });
    }

    public Flowable<InvestorAddedEventResponse> investorAddedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INVESTOR_ADDED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            InvestorAddedEventResponse response = new InvestorAddedEventResponse();
            List<Type> results = extractEventParametersWithLog(INVESTOR_ADDED_EVENT, log).getNonIndexedValues();
            List<Type> indexed = extractEventParametersWithLog(INVESTOR_ADDED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            response.investor = (String) results.get(0).getValue();
            response.amount = (BigInteger) results.get(1).getValue();
            return response;
        });
    }

    public Flowable<RestorerUpdatedEventResponse> restorerUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESTORER_UPDATED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            RestorerUpdatedEventResponse response = new RestorerUpdatedEventResponse();
            List<Type> results = extractEventParametersWithLog(RESTORER_UPDATED_EVENT, log).getNonIndexedValues();
            List<Type> indexed = extractEventParametersWithLog(RESTORER_UPDATED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            response.restorer = (String) results.get(0).getValue();
            return response;
        });
    }

    public Flowable<RestorationCompletedEventResponse> restorationCompletedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESTORATION_COMPLETED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            RestorationCompletedEventResponse response = new RestorationCompletedEventResponse();
            List<Type> indexed = extractEventParametersWithLog(RESTORATION_COMPLETED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            return response;
        });
    }

    public Flowable<ItemPostedEventResponse> itemPostedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ITEM_POSTED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            ItemPostedEventResponse response = new ItemPostedEventResponse();
            List<Type> results = extractEventParametersWithLog(ITEM_POSTED_EVENT, log).getNonIndexedValues();
            List<Type> indexed = extractEventParametersWithLog(ITEM_POSTED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            response.price = (BigInteger) results.get(0).getValue();
            return response;
        });
    }

    public Flowable<NFTTransferredEventResponse> nFTTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NFT_TRANSFERRED_EVENT));
        return web3j.ethLogFlowable(filter).map(log -> {
            NFTTransferredEventResponse response = new NFTTransferredEventResponse();
            List<Type> results = extractEventParametersWithLog(NFT_TRANSFERRED_EVENT, log).getNonIndexedValues();
            List<Type> indexed = extractEventParametersWithLog(NFT_TRANSFERRED_EVENT, log).getIndexedValues();
            response.tokenId = (BigInteger) indexed.get(0).getValue();
            response.winner = (String) results.get(0).getValue();
            return response;
        });
    }
}