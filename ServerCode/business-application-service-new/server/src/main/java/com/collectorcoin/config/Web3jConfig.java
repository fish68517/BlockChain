package com.collectorcoin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

/**
 * Configuration class for Web3j blockchain connection.
 */
@Configuration
public class Web3jConfig {

    private static final Logger logger = LoggerFactory.getLogger(Web3jConfig.class);

    @Value("${blockchain.node.url}")
    private String nodeUrl;

    @Value("${blockchain.admin.private-key}")
    private String adminPrivateKey;

    @Value("${blockchain.chain.id}")
    private long chainId;

    @Bean
    public Web3j web3j() {
        logger.info("Connecting to Ethereum node: {}", nodeUrl);
        return Web3j.build(new HttpService(nodeUrl));
    }

    @Bean
    public Credentials adminCredentials() {
        Credentials credentials = Credentials.create(adminPrivateKey);
        logger.info("Admin wallet address: {}", credentials.getAddress());
        return credentials;
    }

    @Bean
    public ContractGasProvider gasProvider() {
        BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L);
        BigInteger gasLimit = BigInteger.valueOf(500_000L);
        return new StaticGasProvider(gasPrice, gasLimit);
    }

    public long getChainId() {
        return chainId;
    }
}
