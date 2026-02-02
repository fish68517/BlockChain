# Getting ETH on Hardhat Local Network

## The Issue
MetaMask is showing "Insufficient funds" because your account doesn't have ETH to pay for gas fees. Even though this is a local network (no real money), you still need ETH on the local chain to pay transaction fees.

## Solution: Use a Pre-Funded Hardhat Account

When you run `npx hardhat node`, Hardhat automatically creates 20 accounts, each pre-funded with **10,000 ETH**. You should use one of these accounts in MetaMask.

### Step 1: Get a Pre-Funded Account Private Key

1. Look at the terminal where you're running `npx hardhat node`
2. You should see output like this:
   ```
   Account #0: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266 (10000 ETH)
   Private Key: 0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80
   
   Account #1: 0x70997970C51812dc3A010C7d01b50e0d17dc79C8 (10000 ETH)
   Private Key: 0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d
   ```

3. **Copy the private key** of one of these accounts (Account #0, #1, #2, etc.)

### Step 2: Import Account to MetaMask

1. Open MetaMask
2. Click the account icon (circle) in the top right
3. Click **"Import Account"**
4. Select **"Private Key"**
5. Paste the private key you copied
6. Click **"Import"**

### Step 3: Switch to the Imported Account

1. Make sure the imported account is selected in MetaMask
2. Make sure you're on "Hardhat Local" network
3. You should see a balance of **~10,000 ETH** (for the local network)

### Step 4: Try Investing Again

Now when you click "Invest", MetaMask should have enough ETH to pay for gas fees.

---

## Alternative: Fund Your Current Account Using Hardhat Console

If you want to fund your current MetaMask account instead of importing a new one:

1. **Get your MetaMask account address:**
   - Open MetaMask
   - Copy your account address (0x...)

2. **Fund it using Hardhat console:**
   ```bash
   cd ServerCode/local-hardhat
   npx hardhat console --network localhost
   ```

3. **In the console, run:**
   ```javascript
   const [signer] = await ethers.getSigners();
   const recipient = "YOUR_METAMASK_ADDRESS_HERE";
   const tx = await signer.sendTransaction({
     to: recipient,
     value: ethers.utils.parseEther("100") // Send 100 ETH
   });
   await tx.wait();
   console.log("Funded account with 100 ETH");
   ```

4. **Exit console:** Type `.exit` and press Enter

5. **Check MetaMask:** Your account should now show ~100 ETH balance

---

## Quick Solution: Use Account #0

The easiest solution is to import Account #0 from Hardhat:

**Private Key (Account #0):**
```
0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80
```

This account has 10,000 ETH on the local network and is ready to use.

---

## Why This Happens

- **Every blockchain transaction requires gas fees** (paid in ETH)
- **Even on local/test networks**, you need ETH to pay gas
- **Hardhat pre-funds accounts** when you start the node
- **Your MetaMask account** wasn't pre-funded, so it needs ETH

---

## Important Notes

- ✅ **This is NOT real ETH** - it's only valid on your local Hardhat network
- ✅ **It has NO monetary value** - it's for testing only
- ✅ **You can create as much as you want** on local networks
- ✅ **Use pre-funded accounts** for easiest setup







