package com.collectorcoin.service.impl;

import com.collectorcoin.service.IPFSService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IPFSServiceImpl implements IPFSService {

    private static final Logger log = LoggerFactory.getLogger(IPFSServiceImpl.class);

    @Value("${ipfs.api.url:http://localhost:5001}")
    private String ipfsApiUrl;

    @Value("${ipfs.gateway.url:https://ipfs.io/ipfs/}")
    private String ipfsGatewayUrl;

    @Value("${ipfs.mock.enabled:false}")
    private boolean mockEnabled;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Mock storage for testing
    private final ConcurrentHashMap<String, byte[]> mockStorage = new ConcurrentHashMap<>();

    public IPFSServiceImpl() {
        this.httpClient = new OkHttpClient.Builder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String upload(byte[] content) {
        if (mockEnabled) {
            return mockUpload(content);
        }
        return realUpload(content);
    }

    @Override
    public String uploadJson(String jsonContent) {
        return upload(jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] fetch(String cid) {
        if (mockEnabled) {
            return mockFetch(cid);
        }
        return realFetch(cid);
    }

    @Override
    public String fetchJson(String cid) {
        byte[] content = fetch(cid);
        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public String buildGatewayUrl(String cid) {
        return ipfsGatewayUrl + cid;
    }

    // Real IPFS implementation
    private String realUpload(byte[] content) {
        String url = ipfsApiUrl + "/api/v0/add";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "data",
                        RequestBody.create(content, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("IPFS upload failed: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String cid = jsonNode.get("Hash").asText();

            log.info("Uploaded to IPFS: {}", cid);
            return cid;

        } catch (IOException e) {
            log.error("IPFS upload error", e);
            throw new RuntimeException("IPFS upload failed", e);
        }
    }

    private byte[] realFetch(String cid) {
        String url = ipfsGatewayUrl + cid;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("IPFS fetch failed: " + response.code());
            }

            log.info("Fetched from IPFS: {}", cid);
            return response.body().bytes();

        } catch (IOException e) {
            log.error("IPFS fetch error", e);
            throw new RuntimeException("IPFS fetch failed", e);
        }
    }

    // Mock implementation for testing
    private String mockUpload(byte[] content) {
        String mockCid = "Qm" + UUID.randomUUID().toString().replace("-", "").substring(0, 44);
        mockStorage.put(mockCid, content);
        log.info("[MOCK] Uploaded to IPFS: {}", mockCid);
        return mockCid;
    }

    private byte[] mockFetch(String cid) {
        byte[] content = mockStorage.get(cid);
        if (content == null) {
            throw new RuntimeException("Mock CID not found: " + cid);
        }
        log.info("[MOCK] Fetched from IPFS: {}", cid);
        return content;
    }
}
