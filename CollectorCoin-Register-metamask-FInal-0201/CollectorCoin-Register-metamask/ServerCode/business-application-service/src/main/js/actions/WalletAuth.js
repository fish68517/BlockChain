const WalletAuthService = require("../services/WalletAuthService");

const WALLET_AUTH_SUCCESS = "WALLET_AUTH_SUCCESS";
const WALLET_AUTH_FAIL = "WALLET_AUTH_FAIL";
const SET_MESSAGE = "SET_MESSAGE";

// Helper functions defined locally (not imported)
const isMetaMaskInstalled = () => {
  return typeof window !== 'undefined' && 
         typeof window.ethereum !== 'undefined' && 
         window.ethereum.isMetaMask;
};

const connectMetaMask = async () => {
  if (!isMetaMaskInstalled()) {
    throw new Error("MetaMask is not installed. Please install MetaMask to continue.");
  }
  
  try {
    const accounts = await window.ethereum.request({ method: "eth_requestAccounts" });
    return accounts[0];
  } catch (error) {
    if (error.code === 4001) {
      throw new Error("Please connect to MetaMask to continue.");
    }
    throw error;
  }
};

const signMessage = async (message) => {
  if (!isMetaMaskInstalled()) {
    throw new Error("MetaMask is not installed");
  }

  const accounts = await window.ethereum.request({ method: "eth_accounts" });
  if (accounts.length === 0) {
    throw new Error("Please connect your wallet first");
  }

  const account = accounts[0];
  
  try {
    const signature = await window.ethereum.request({
      method: "personal_sign",
      params: [message, account]
    });
    return { signature, account };
  } catch (error) {
    if (error.code === 4001) {
      throw new Error("Signature request was rejected");
    }
    throw error;
  }
};

// Connect wallet and check if registered
const connectWallet = () => async (dispatch) => {
  // 打印
  console.log("Connecting wallet...");
  try {
    if (!isMetaMaskInstalled()) {
      throw new Error("MetaMask not installed");
    }

    const walletAddress = await connectMetaMask();
    const response = await WalletAuthService.checkWalletExists(walletAddress);
    console.log(response.data.exists)

    return {
      walletAddress,
    //   isRegistered: response.data.exists
    isRegistered: false
    };
  } catch (error) {
    dispatch({
      type: SET_MESSAGE,
      payload: error.message || "Failed to connect wallet"
    });
    throw error;
  }
};

// Sign in with wallet
const walletLogin = (walletAddress) => async (dispatch) => {
  try {
    // Get nonce from server
    const nonceResponse = await WalletAuthService.getNonce(walletAddress);
    const message = nonceResponse.data.message;

    // Sign the message
    const { signature } = await signMessage(message);

    // Send to server for verification
    const response = await WalletAuthService.walletSignin(walletAddress, signature, message);

    // Store auth data
    localStorage.setItem("user", JSON.stringify(response.data));

    dispatch({
      type: WALLET_AUTH_SUCCESS,
      payload: { user: response.data }
    });

    return response.data;
  } catch (error) {
    dispatch({
      type: WALLET_AUTH_FAIL
    });
    dispatch({
      type: SET_MESSAGE,
      payload: error.response?.data || error.message || "Wallet login failed"
    });
    throw error;
  }
};

// Register with wallet
const walletRegister = (walletAddress, username, email, role) => async (dispatch) => {
  try {
    // Get nonce from server
    const nonceResponse = await WalletAuthService.getNonce(walletAddress);
    const message = nonceResponse.data.message;

    // Sign the message
    const { signature } = await signMessage(message);

    // Send to server
    const response = await WalletAuthService.walletSignup(
      walletAddress,
      signature,
      message,
      username,
      email,
      [role]
    );

    // Store auth data
    localStorage.setItem("user", JSON.stringify(response.data));

    dispatch({
      type: WALLET_AUTH_SUCCESS,
      payload: { user: response.data }
    });

    return response.data;
  } catch (error) {
    dispatch({
      type: WALLET_AUTH_FAIL
    });
    dispatch({
      type: SET_MESSAGE,
      payload: error.response?.data || error.message || "Wallet registration failed"
    });
    throw error;
  }
};

module.exports = {
  connectWallet,
  walletLogin,
  walletRegister,
  WALLET_AUTH_SUCCESS,
  WALLET_AUTH_FAIL
};