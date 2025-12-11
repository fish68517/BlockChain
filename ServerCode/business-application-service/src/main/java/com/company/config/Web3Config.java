package com.company.config;

import com.company.utils.Web3Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Web3 and blockchain integration
 */
@Configuration
public class Web3Config {
    
    @Value("${blockchain.rpc-url:http://localhost:8545}")
    private String rpcUrl;
    
    @Value("${blockchain.private-key:}")
    private String privateKey;

    /**
     * Create a Web3Utils bean for blockchain operations
     */
    @Bean
    public Web3Utils web3Utils() {
        if (privateKey != null && !privateKey.isEmpty()) {
            return new Web3Utils(rpcUrl, privateKey);
        }
        // Default constructor uses RPC URL only
        return new Web3Utils();
    }
}
