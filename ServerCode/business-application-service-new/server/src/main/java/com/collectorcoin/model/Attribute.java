package com.collectorcoin.model;

/**
 * NFT Attribute following OpenSea standard
 */
public class Attribute {

    private String traitType;
    private Object value;
    private String displayType;

    public Attribute() {}

    public Attribute(String traitType, Object value) {
        this.traitType = traitType;
        this.value = value;
    }

    public Attribute(String traitType, Object value, String displayType) {
        this.traitType = traitType;
        this.value = value;
        this.displayType = displayType;
    }

    // Getters and Setters
    public String getTraitType() { return traitType; }
    public void setTraitType(String traitType) { this.traitType = traitType; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public String getDisplayType() { return displayType; }
    public void setDisplayType(String displayType) { this.displayType = displayType; }
}
