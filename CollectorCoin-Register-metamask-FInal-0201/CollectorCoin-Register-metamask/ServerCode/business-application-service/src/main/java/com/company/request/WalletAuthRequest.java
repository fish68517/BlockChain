package com.company.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class WalletAuthRequest {
  
  @NotBlank
  @Size(min = 42, max = 42, message = "Wallet address must be 42 characters")
  private String walletAddress;

  @NotBlank
  private String signature;

  @NotBlank
  private String message;

  // Getters and Setters
  public String getWalletAddress() {
    return walletAddress;
  }

  public void setWalletAddress(String walletAddress) {
    this.walletAddress = walletAddress;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}