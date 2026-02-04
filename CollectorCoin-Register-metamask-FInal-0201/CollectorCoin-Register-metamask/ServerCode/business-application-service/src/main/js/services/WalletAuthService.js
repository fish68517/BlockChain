const createBaseRequest = require("../common/http-common"); 
const axios = require("axios");
const API_URL = "http://localhost:8090/api/auth/";



class WalletAuthService {
  
  // 获取随机数 Nonce
getNonce(publicAddress) {
  // 这种写法对应 @PathVariable，完美匹配后端 /nonce/{address}
  return createBaseRequest().get(`/auth/wallet/nonce/${publicAddress}`);
}

    // Check if wallet is already registered
  checkWalletExists(walletAddress) {
    return axios.get(API_URL + "wallet/exists/" + walletAddress);
  }


  // 钱包注册
  walletSignup(walletAddress, signature, message, username, email, roles) {
    // 替换 axios.post -> createBaseRequest().post
    return createBaseRequest().post("/auth/wallet/signup", {
      walletAddress,
      signature,
      message,
      username,
      email,
      roles
    });
  }

  // 钱包登录
  walletSignin(walletAddress, signature, message) {
    return createBaseRequest().post("/auth/wallet/signin", {
      walletAddress,
      signature,
      message
    });
  }
}


module.exports = new WalletAuthService();