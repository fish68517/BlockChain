package com.collectorcoin.service;

import com.collectorcoin.model.Investment;
import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.model.User;
import com.collectorcoin.repository.InvestmentRepository;
import com.collectorcoin.repository.ProjectListingRepository;
import com.collectorcoin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvestmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);
    
    private final InvestmentRepository investmentRepository;
    private final ProjectListingRepository listingRepository;
    private final UserRepository userRepository;
    
    public InvestmentService(InvestmentRepository investmentRepository,
                             ProjectListingRepository listingRepository,
                             UserRepository userRepository) {
        this.investmentRepository = investmentRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }
    
    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }
    
    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }
    
    public List<Investment> getInvestmentsByListingId(Long listingId) {
        return investmentRepository.findByListingId(listingId);
    }
    
    public Optional<Investment> getInvestmentById(Long id) {
        return investmentRepository.findById(id);
    }
    
    @Transactional
    public Investment createInvestment(Long userId, Long listingId, BigDecimal amount, String transactionHash) {
        // Validate listing exists
        ProjectListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Investment amount must be positive");
        }
        
        // Check if user already has investment in this listing
        Optional<Investment> existingInvestment = investmentRepository
            .findFirstByUserIdAndListingId(userId, listingId);
        
        if (existingInvestment.isPresent()) {
            // Add to existing investment
            Investment investment = existingInvestment.get();
            investment.increaseAmount(amount);
            if (transactionHash != null) {
                investment.setTransactionHash(transactionHash);
            }
            logger.info("Increased existing investment: userId={}, listingId={}, newTotal={}", 
                userId, listingId, investment.getAmount());
            return investmentRepository.save(investment);
        }
        
        // Create new investment
        Investment investment = new Investment();
        investment.setUserId(userId);
        investment.setListingId(listingId);
        investment.setAmount(amount);
        investment.setInvestorAddress(user.getWalletAddress());
        investment.setTransactionHash(transactionHash);
        
        logger.info("Created new investment: userId={}, listingId={}, amount={}", userId, listingId, amount);
        return investmentRepository.save(investment);
    }
    
    @Transactional
    public Investment updateInvestmentStatus(Long investmentId, String status) {
        Investment investment = investmentRepository.findById(investmentId)
            .orElseThrow(() -> new IllegalArgumentException("Investment not found: " + investmentId));
        
        investment.setStatus(status);
        logger.info("Updated investment status: investmentId={}, status={}", investmentId, status);
        return investmentRepository.save(investment);
    }
    
    public BigDecimal getTotalInvestmentForListing(Long listingId) {
        List<Investment> investments = investmentRepository.findByListingId(listingId);
        return investments.stream()
            .map(Investment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalInvestmentByUser(Long userId) {
        List<Investment> investments = investmentRepository.findByUserId(userId);
        return investments.stream()
            .map(Investment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
