package com.company.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3Config {

    @Value("${blockchain.network.url}")
    private String networkUrl;

    @Value("${blockchain.admin.private.key}")
    private String adminPrivateKey;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(networkUrl));
    }

    @Bean
    public Credentials adminCredentials() {
        return Credentials.create(adminPrivateKey);
    }

    @Bean
    public ContractGasProvider gasProvider() {
        return new DefaultGasProvider();
    }
}



