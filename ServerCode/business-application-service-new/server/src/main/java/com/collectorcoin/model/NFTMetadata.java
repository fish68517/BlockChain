package com.collectorcoin.model;

import java.util.List;

/**
 * NFT Metadata following OpenSea standard
 */
public class NFTMetadata {

    private String name;
    private String description;
    private String image;
    private String externalUrl;
    private List<Attribute> attributes;

    public NFTMetadata() {}

    public NFTMetadata(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

    public List<Attribute> getAttributes() { return attributes; }
    public void setAttributes(List<Attribute> attributes) { this.attributes = attributes; }
}
