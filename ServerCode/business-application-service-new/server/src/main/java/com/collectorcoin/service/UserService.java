package com.collectorcoin.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import com.collectorcoin.dto.RegisterRequest;
import com.collectorcoin.model.User;
import com.collectorcoin.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final Web3j web3j;
    private final Credentials adminCredentials;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.initial.eth:1.0}")
    private BigDecimal initialEthAmount;

    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;

    public UserService(UserRepository userRepository, Web3j web3j, Credentials adminCredentials, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.web3j = web3j;
        this.adminCredentials = adminCredentials;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email exists
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setEthBalance(BigDecimal.ZERO);
        
        // Set role (default to USER if not specified)
        String role = request.getRole();
        if (role != null && !role.isEmpty()) {
            // Validate role
            if (role.equals("OWNER") || role.equals("RESTORER") || 
                role.equals("INVESTOR") || role.equals("BUYER")) {
                user.setRole(role);
            }
            // Note: ADMIN role cannot be self-assigned for security
        }

        // Auto-generate wallet for new user
        try {
            org.web3j.crypto.ECKeyPair keyPair = org.web3j.crypto.Keys.createEcKeyPair();
            String privateKey = keyPair.getPrivateKey().toString(16);
            String address = "0x" + org.web3j.crypto.Keys.getAddress(keyPair);
            
            user.setWalletAddress(address);
            user.setWalletPrivateKey(privateKey);
            logger.info("Generated wallet for user {}: {}", request.getUsername(), address);
        } catch (Exception e) {
            logger.error("Failed to generate wallet", e);
            throw new RuntimeException("Failed to generate wallet", e);
        }

        User savedUser = userRepository.save(user);
        logger.info("User registered: {}", savedUser.getUsername());

        // Allocate initial ETH from main wallet
        allocateEth(savedUser);

        return savedUser;
    }

    @Transactional
    public void allocateEth(User user) {
        if (user.getWalletAddress() == null || user.getWalletAddress().isEmpty()) {
            logger.warn("User {} has no wallet address, skipping ETH allocation", user.getUsername());
            return;
        }

        try {
            if (blockchainEnabled) {
                // Real blockchain transfer using signed transaction
                BigInteger amountWei = Convert.toWei(initialEthAmount, Convert.Unit.ETHER).toBigInteger();
                
                // Get nonce
                BigInteger nonce = web3j.ethGetTransactionCount(
                    adminCredentials.getAddress(), 
                    DefaultBlockParameterName.LATEST
                ).send().getTransactionCount();
                
                // Get gas price
                BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                BigInteger gasLimit = BigInteger.valueOf(21000);
                
                // Get chain ID for EIP-155
                long chainId = 31337; // Hardhat local chain ID
                
                // Create and sign raw transaction with chain ID
                org.web3j.crypto.RawTransaction rawTransaction = org.web3j.crypto.RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    user.getWalletAddress(),
                    amountWei
                );
                
                // Sign transaction with chain ID
                byte[] signedMessage = org.web3j.crypto.TransactionEncoder.signMessage(rawTransaction, chainId, adminCredentials);
                String hexValue = org.web3j.utils.Numeric.toHexString(signedMessage);
                
                // Send transaction
                org.web3j.protocol.core.methods.response.EthSendTransaction ethSendTransaction = 
                    web3j.ethSendRawTransaction(hexValue).send();
                
                if (ethSendTransaction.hasError()) {
                    logger.error("ETH transfer failed: {}", ethSendTransaction.getError().getMessage());
                    // Don't throw, just log and continue - user registration should still succeed
                } else {
                    String txHash = ethSendTransaction.getTransactionHash();
                    logger.info("Transferred {} ETH to user {}, tx: {}", initialEthAmount, user.getUsername(), txHash);
                }
            } else {
                logger.info("[DEMO MODE] Would transfer {} ETH to {}", initialEthAmount, user.getWalletAddress());
            }

            // Update user balance in database
            user.setEthBalance(user.getEthBalance().add(initialEthAmount));
            userRepository.save(user);
            
        } catch (Exception e) {
            logger.error("Failed to allocate ETH to user {}: {}", user.getUsername(), e.getMessage(), e);
            // Don't throw - user registration should still succeed even if ETH transfer fails
        }
    }

    public Optional<User> login(String username, String password) {
        logger.info("Login attempt: username={}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", username);
            return Optional.empty();
        }
        User user = userOpt.get();
        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("Password match for user: {}", username);
            return Optional.of(user);
        }
        logger.warn("Password mismatch for user: {}", username);
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    @Transactional
    public User bindWallet(Long userId, String walletAddress) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByWalletAddress(walletAddress)) {
            throw new IllegalArgumentException("Wallet already bound to another user");
        }

        user.setWalletAddress(walletAddress);
        User savedUser = userRepository.save(user);

        // Allocate ETH for new wallet
        allocateEth(savedUser);

        return savedUser;
    }
}
