package com.collectorcoin.service;

import com.collectorcoin.model.NFTMetadata;
import com.collectorcoin.service.impl.IPFSServiceImpl;
import com.collectorcoin.service.impl.NFTMetadataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NFTMetadataService 单元测试")
class NFTMetadataServiceTest {

    private NFTMetadataServiceImpl metadataService;
    private IPFSServiceImpl ipfsService;

    @BeforeEach
    void setUp() {
        ipfsService = new IPFSServiceImpl();
        ReflectionTestUtils.setField(ipfsService, "mockEnabled", true);
        ReflectionTestUtils.setField(ipfsService, "ipfsGatewayUrl", "https://ipfs.io/ipfs/");

        metadataService = new NFTMetadataServiceImpl(ipfsService);
        ReflectionTestUtils.setField(metadataService, "externalBaseUrl", "https://collectorcoin.com/listing/");
    }

    @Test
    @DisplayName("创建元数据应包含所有必要字段")
    void createMetadata_shouldContainAllFields() {
        NFTMetadata metadata = metadataService.createMetadata(
                "Antique Vase",
                "A beautiful Ming dynasty vase",
                "ipfs://QmImageHash",
                BigInteger.valueOf(10000),
                BigInteger.valueOf(2000),
                "LISTED",
                1L
        );

        assertEquals("Antique Vase", metadata.getName());
        assertEquals("A beautiful Ming dynasty vase", metadata.getDescription());
        assertEquals("ipfs://QmImageHash", metadata.getImage());
        assertEquals("https://collectorcoin.com/listing/1", metadata.getExternalUrl());
        assertNotNull(metadata.getAttributes());
        assertEquals(4, metadata.getAttributes().size());
    }

    @Test
    @DisplayName("上传元数据应返回 IPFS URI")
    void uploadToIPFS_shouldReturnUri() {
        NFTMetadata metadata = new NFTMetadata("Test", "Test desc", "ipfs://test");

        String uri = metadataService.uploadToIPFS(metadata);

        assertNotNull(uri);
        assertTrue(uri.startsWith("ipfs://Qm"));
    }

    @Test
    @DisplayName("创建并上传应返回有效 URI")
    void createAndUpload_shouldReturnValidUri() {
        String uri = metadataService.createAndUpload(
                "Test Item",
                "Test description",
                "ipfs://QmImage",
                BigInteger.valueOf(5000),
                BigInteger.valueOf(1000),
                "FUNDED",
                2L
        );

        assertNotNull(uri);
        assertTrue(uri.startsWith("ipfs://"));
    }
}
