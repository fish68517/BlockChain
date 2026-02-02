const axios = require("axios");
const API_URL = "http://localhost:8090/api/auth/";

class WalletAuthService {
  // Get nonce message for wallet to sign
  getNonce(walletAddress) {
    return axios.get(API_URL + "wallet/nonce/" + walletAddress);
  }

  // Check if wallet is already registered
  checkWalletExists(walletAddress) {
    return axios.get(API_URL + "wallet/exists/" + walletAddress);
  }

  // Sign in with wallet
  walletSignin(walletAddress, signature, message) {
    return axios.post(API_URL + "wallet/signin", {
      walletAddress,
      signature,
      message
    });
  }

  // Sign up with wallet
  walletSignup(walletAddress, signature, message, username, email, role) {
    return axios.post(API_URL + "wallet/signup", {
      walletAddress,
      signature,
      message,
      username,
      email,
      role
    });
  }
}

module.exports = new WalletAuthService();