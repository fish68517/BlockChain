package com.collectorcoin.service;

import com.collectorcoin.service.impl.IPFSServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IPFSService 单元测试")
class IPFSServiceTest {

    private IPFSServiceImpl ipfsService;

    @BeforeEach
    void setUp() {
        ipfsService = new IPFSServiceImpl();
        // 启用 Mock 模式
        ReflectionTestUtils.setField(ipfsService, "mockEnabled", true);
        ReflectionTestUtils.setField(ipfsService, "ipfsGatewayUrl", "https://ipfs.io/ipfs/");
    }

    @Test
    @DisplayName("上传字节数组应返回有效 CID")
    void upload_shouldReturnValidCid() {
        byte[] content = "Hello IPFS".getBytes(StandardCharsets.UTF_8);

        String cid = ipfsService.upload(content);

        assertNotNull(cid);
        assertTrue(cid.startsWith("Qm"));
        assertEquals(46, cid.length());
    }

    @Test
    @DisplayName("上传 JSON 应返回有效 CID")
    void uploadJson_shouldReturnValidCid() {
        String json = "{\"name\":\"Test NFT\",\"description\":\"Test\"}";

        String cid = ipfsService.uploadJson(json);

        assertNotNull(cid);
        assertTrue(cid.startsWith("Qm"));
    }

    @Test
    @DisplayName("获取已上传内容应返回原始数据")
    void fetch_shouldReturnOriginalContent() {
        byte[] original = "Test content for IPFS".getBytes(StandardCharsets.UTF_8);
        String cid = ipfsService.upload(original);

        byte[] fetched = ipfsService.fetch(cid);

        assertArrayEquals(original, fetched);
    }

    @Test
    @DisplayName("获取 JSON 应返回原始 JSON 字符串")
    void fetchJson_shouldReturnOriginalJson() {
        String originalJson = "{\"name\":\"Test\",\"value\":123}";
        String cid = ipfsService.uploadJson(originalJson);

        String fetchedJson = ipfsService.fetchJson(cid);

        assertEquals(originalJson, fetchedJson);
    }

    @Test
    @DisplayName("获取不存在的 CID 应抛出异常")
    void fetch_nonExistentCid_shouldThrowException() {
        String fakeCid = "QmFakeCidThatDoesNotExist12345678901234567890";

        assertThrows(RuntimeException.class, () -> ipfsService.fetch(fakeCid));
    }

    @Test
    @DisplayName("构建网关 URL 应返回正确格式")
    void buildGatewayUrl_shouldReturnCorrectFormat() {
        String cid = "QmTestCid123456789012345678901234567890123456";

        String url = ipfsService.buildGatewayUrl(cid);

        assertEquals("https://ipfs.io/ipfs/" + cid, url);
    }

    @Test
    @DisplayName("多次上传相同内容应返回不同 CID（Mock 模式）")
    void upload_sameContent_shouldReturnDifferentCids() {
        byte[] content = "Same content".getBytes(StandardCharsets.UTF_8);

        String cid1 = ipfsService.upload(content);
        String cid2 = ipfsService.upload(content);

        // Mock 模式下每次生成新的 UUID
        assertNotEquals(cid1, cid2);
    }
}
