package com.collectorcoin.service;

/**
 * Service interface for IPFS interactions.
 */
public interface IPFSService {

    /**
     * Upload content to IPFS
     * @param content The content bytes to upload
     * @return IPFS CID (Content Identifier)
     */
    String upload(byte[] content);

    /**
     * Upload JSON string to IPFS
     * @param jsonContent The JSON string to upload
     * @return IPFS CID (Content Identifier)
     */
    String uploadJson(String jsonContent);

    /**
     * Fetch content from IPFS by CID
     * @param cid The IPFS CID
     * @return Content bytes
     */
    byte[] fetch(String cid);

    /**
     * Fetch JSON content from IPFS by CID
     * @param cid The IPFS CID
     * @return JSON string
     */
    String fetchJson(String cid);

    /**
     * Build IPFS gateway URL for a CID
     * @param cid The IPFS CID
     * @return Full gateway URL
     */
    String buildGatewayUrl(String cid);
}
