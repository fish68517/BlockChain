# Admin Wallet Setup Guide

## 1. Create Sepolia Testnet Wallet

### Option A: Using MetaMask
1. Install MetaMask browser extension
2. Create a new wallet or import existing
3. Switch network to "Sepolia Test Network"
4. Copy wallet address

### Option B: Using Web3j CLI
```bash
web3j wallet create
```

## 2. Get Test ETH

Visit Sepolia faucets:
- https://sepoliafaucet.com/
- https://faucet.sepolia.dev/

Request test ETH to your wallet address.

## 3. Configure Environment

Add to `.env` file:
```
ADMIN_WALLET_PRIVATE_KEY=0x_your_private_key_here
```

**SECURITY WARNING:**
- Never commit private keys to git
- Use environment variables only
- Keep backup of private key securely
