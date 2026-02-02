package com.collectorcoin.service;

import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.ProjectListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentProxyServiceTest {

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private ProjectListingRepository projectListingRepository;

    private InvestmentProxyService investmentProxyService;

    @BeforeEach
    void setUp() {
        investmentProxyService = new InvestmentProxyService(
            blockchainService, projectListingRepository);
    }

    @Test
    void processInvestment_success() {
        ProjectListing project = createProject(1L, 100L, true);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        investmentProxyService.processInvestment(
            1L, "0xInvestor", new BigDecimal("1.5"));

        verify(blockchainService).addNFTInvestor(
            eq(100L), eq("0xInvestor"), any(BigInteger.class));
    }

    @Test
    void processInvestment_projectNotFound() {
        when(projectListingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            investmentProxyService.processInvestment(
                1L, "0xInvestor", new BigDecimal("1.0")));
    }

    @Test
    void processInvestment_nftNotMinted() {
        ProjectListing project = createProject(1L, null, false);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class, () ->
            investmentProxyService.processInvestment(
                1L, "0xInvestor", new BigDecimal("1.0")));
    }

    @Test
    void assignRestorer_success() {
        ProjectListing project = createProject(1L, 100L, true);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        investmentProxyService.assignRestorer(1L, "0xRestorer");

        verify(blockchainService).updateNFTRestorer(100L, "0xRestorer");
    }

    @Test
    void completeRestoration_success() {
        ProjectListing project = createProject(1L, 100L, true);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        investmentProxyService.completeRestoration(1L);

        verify(blockchainService).markRestorationComplete(100L);
    }

    @Test
    void postForAuction_success() {
        ProjectListing project = createProject(1L, 100L, true);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        investmentProxyService.postForAuction(1L, new BigDecimal("10.0"));

        verify(blockchainService).setNFTAuctionLive(eq(100L), any(BigInteger.class));
    }

    @Test
    void transferToWinner_success() {
        ProjectListing project = createProject(1L, 100L, true);
        when(projectListingRepository.findById(1L)).thenReturn(Optional.of(project));

        investmentProxyService.transferToWinner(1L, "0xWinner");

        verify(blockchainService).transferNFTToWinner(100L, "0xWinner");
    }

    private ProjectListing createProject(Long id, Long tokenId, boolean launched) {
        ProjectListing project = new ProjectListing();
        project.setId(id);
        project.setNftTokenId(tokenId);
        project.setNftLaunched(launched);
        return project;
    }
}
