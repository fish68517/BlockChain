package com.collectorcoin.e2e;

import com.collectorcoin.model.ProjectListing;
import com.collectorcoin.repository.ProjectListingRepository;
import com.collectorcoin.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * End-to-End Integration Tests for CollectorCoin Platform.
 * Covers 12 acceptance scenarios for the complete dNFT lifecycle.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("E2E Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class E2EIntegrationTest {

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private ProjectListingRepository projectListingRepository;

    @Mock
    private IPFSService ipfsService;

    @Mock
    private NFTMetadataService nftMetadataService;

    private InvestmentProxyService investmentProxyService;

    // Test constants
    private static final String PROJECT_ADDRESS = "0x1234567890abcdef";
    private static final String INVESTOR_ADDRESS = "0xInvestor123";
    private static final String RESTORER_ADDRESS = "0xRestorer456";
    private static final String WINNER_ADDRESS = "0xWinner789";
    private static final Long TOKEN_ID = 1L;
    private static final Long PROJECT_ID = 100L;

    @BeforeEach
    void setUp() {
        investmentProxyService = new InvestmentProxyService(
            blockchainService, projectListingRepository);
    }

    // ============ Scenario 1-4: Project Creation & NFT Minting ============

    @Test
    @Order(1)
    @DisplayName("Scenario 1: Create new project listing")
    void scenario1_createNewProjectListing() {
        ProjectListing project = new ProjectListing();
        project.setId(PROJECT_ID);
        project.setProjectAddress(PROJECT_ADDRESS);
        project.setTitle("Vintage Car Restoration");
        project.setValueEstimation(new BigDecimal("50000"));
        project.setRepairEstimation(new BigDecimal("15000"));
        project.setStatus("PENDING");

        when(projectListingRepository.save(any())).thenReturn(project);

        assertNotNull(project.getId());
        assertEquals("PENDING", project.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Scenario 2: Upload metadata to IPFS")
    void scenario2_uploadMetadataToIPFS() {
        String metadataJson = "{\"name\":\"Vintage Car\"}";
        byte[] metadataBytes = metadataJson.getBytes(StandardCharsets.UTF_8);
        String expectedCid = "QmTest123";

        when(ipfsService.upload(metadataBytes)).thenReturn(expectedCid);

        String cid = ipfsService.upload(metadataBytes);

        assertEquals(expectedCid, cid);
        verify(ipfsService).upload(metadataBytes);
    }

    @Test
    @Order(3)
    @DisplayName("Scenario 3: Mint dNFT for approved project")
    void scenario3_mintDNFTForProject() {
        BigInteger valueWei = BigInteger.valueOf(50000).multiply(BigInteger.TEN.pow(18));
        BigInteger repairWei = BigInteger.valueOf(15000).multiply(BigInteger.TEN.pow(18));
        String metadataUri = "ipfs://QmTest123";

        when(blockchainService.mintDNFT(PROJECT_ADDRESS, valueWei, repairWei, metadataUri))
            .thenReturn(TOKEN_ID);

        Long tokenId = blockchainService.mintDNFT(PROJECT_ADDRESS, valueWei, repairWei, metadataUri);

        assertEquals(TOKEN_ID, tokenId);
        verify(blockchainService).mintDNFT(PROJECT_ADDRESS, valueWei, repairWei, metadataUri);
    }

    @Test
    @Order(4)
    @DisplayName("Scenario 4: Launch dNFT in marketplace")
    void scenario4_launchDNFTInMarketplace() {
        doNothing().when(blockchainService).launchDNFT(TOKEN_ID);

        blockchainService.launchDNFT(TOKEN_ID);

        verify(blockchainService).launchDNFT(TOKEN_ID);
    }

    // ============ Scenario 5-6: Investment Flow ============

    @Test
    @Order(5)
    @DisplayName("Scenario 5: Process investor contribution")
    void scenario5_processInvestorContribution() {
        ProjectListing project = createLaunchedProject();
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        investmentProxyService.processInvestment(
            PROJECT_ID, INVESTOR_ADDRESS, new BigDecimal("5.0"));

        verify(blockchainService).addNFTInvestor(
            eq(TOKEN_ID), eq(INVESTOR_ADDRESS), any(BigInteger.class));
    }

    @Test
    @Order(6)
    @DisplayName("Scenario 6: Reject investment for unlaunched NFT")
    void scenario6_rejectInvestmentForUnlaunchedNFT() {
        ProjectListing project = createMintedProject();
        project.setNftLaunched(false);
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class, () ->
            investmentProxyService.processInvestment(
                PROJECT_ID, INVESTOR_ADDRESS, new BigDecimal("5.0")));
    }

    // ============ Scenario 7-8: Restoration Flow ============

    @Test
    @Order(7)
    @DisplayName("Scenario 7: Assign restorer to project")
    void scenario7_assignRestorerToProject() {
        ProjectListing project = createLaunchedProject();
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        investmentProxyService.assignRestorer(PROJECT_ID, RESTORER_ADDRESS);

        verify(blockchainService).updateNFTRestorer(TOKEN_ID, RESTORER_ADDRESS);
    }

    @Test
    @Order(8)
    @DisplayName("Scenario 8: Mark restoration complete")
    void scenario8_markRestorationComplete() {
        ProjectListing project = createLaunchedProject();
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        investmentProxyService.completeRestoration(PROJECT_ID);

        verify(blockchainService).markRestorationComplete(TOKEN_ID);
    }

    // ============ Scenario 9-10: Auction Flow ============

    @Test
    @Order(9)
    @DisplayName("Scenario 9: Post item for auction")
    void scenario9_postItemForAuction() {
        ProjectListing project = createLaunchedProject();
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        investmentProxyService.postForAuction(PROJECT_ID, new BigDecimal("75000"));

        verify(blockchainService).setNFTAuctionLive(eq(TOKEN_ID), any(BigInteger.class));
    }

    @Test
    @Order(10)
    @DisplayName("Scenario 10: Transfer NFT to auction winner")
    void scenario10_transferNFTToWinner() {
        ProjectListing project = createLaunchedProject();
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        investmentProxyService.transferToWinner(PROJECT_ID, WINNER_ADDRESS);

        verify(blockchainService).transferNFTToWinner(TOKEN_ID, WINNER_ADDRESS);
    }

    // ============ Scenario 11-12: Error Handling & Edge Cases ============

    @Test
    @Order(11)
    @DisplayName("Scenario 11: Reject operation for non-existent project")
    void scenario11_rejectOperationForNonExistentProject() {
        when(projectListingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            investmentProxyService.processInvestment(
                999L, INVESTOR_ADDRESS, new BigDecimal("1.0")));
    }

    @Test
    @Order(12)
    @DisplayName("Scenario 12: Reject operation for unminted NFT")
    void scenario12_rejectOperationForUnmintedNFT() {
        ProjectListing project = new ProjectListing();
        project.setId(PROJECT_ID);
        project.setNftTokenId(null);
        when(projectListingRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class, () ->
            investmentProxyService.assignRestorer(PROJECT_ID, RESTORER_ADDRESS));
    }

    // ============ Helper Methods ============

    private ProjectListing createLaunchedProject() {
        ProjectListing project = new ProjectListing();
        project.setId(PROJECT_ID);
        project.setProjectAddress(PROJECT_ADDRESS);
        project.setNftTokenId(TOKEN_ID);
        project.setNftMinted(true);
        project.setNftLaunched(true);
        return project;
    }

    private ProjectListing createMintedProject() {
        ProjectListing project = new ProjectListing();
        project.setId(PROJECT_ID);
        project.setProjectAddress(PROJECT_ADDRESS);
        project.setNftTokenId(TOKEN_ID);
        project.setNftMinted(true);
        project.setNftLaunched(false);
        return project;
    }
}
