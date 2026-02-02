# How to Mint CCT Tokens for Investor

## The Problem

When an investor tries to invest, they get the error:
```
ERC20: transfer amount exceeds balance
```

This means the investor's MetaMask account doesn't have any CCT tokens to invest with.

## Solution: Mint Tokens to Investor Account

The investor account needs CCT tokens minted to it before they can invest.

### Step 1: Get Your Investor MetaMask Address

1. Open MetaMask
2. Make sure you're logged in as the **Investor** account
3. Copy your account address (starts with `0x...`)

### Step 2: Mint Tokens Using the Script

Run the mint script:

```bash
cd ServerCode/local-hardhat

# Replace 0x... with your investor MetaMask address
# Replace 100000000000000000000 with amount in wei (100 tokens default)
INVESTOR_ADDRESS=0xYOUR_INVESTOR_ADDRESS \
  AMOUNT=100000000000000000000 \
  npx hardhat run scripts/mint-tokens-to-investor.js --network localhost
```

**Example:**
```bash
INVESTOR_ADDRESS=0xBcd4042DE499D14e55001CcbB24a551F3b954096 \
  AMOUNT=100000000000000000000 \
  npx hardhat run scripts/mint-tokens-to-investor.js --network localhost
```

This will mint 100 CCT tokens to the investor account.

### Step 3: Verify Tokens Were Minted

After running the script, you should see:
```
✅ Tokens minted successfully!
New investor balance: 100000000000000000000 wei ( 100.0 tokens)
```

### Step 4: Try Investing Again

Now when the investor tries to invest:
1. They should have enough tokens
2. The investment transaction should succeed
3. No more "transfer amount exceeds balance" error

## Amount Examples

- **100 tokens:** `100000000000000000000` (100 * 10^18)
- **10 tokens:** `10000000000000000000` (10 * 10^18)
- **5 tokens:** `5000000000000000000` (5 * 10^18)
- **1 token:** `1000000000000000000` (1 * 10^18)

## Important Notes

- ✅ The deployer account (Account #0 from Hardhat) is the owner of the token contract
- ✅ Only the owner can mint tokens
- ✅ The script uses the deployer account automatically
- ✅ Tokens are minted on the local Hardhat network only (not real tokens)
- ✅ You can mint as many tokens as you want for testing

## Troubleshooting

### Error: "Ownable: caller is not the owner"

This means the deployer account is not the owner. Make sure:
- You deployed contracts using the same Hardhat node
- The deployer account matches the one used to deploy the token contract

### Error: "Token contract not found"

Make sure:
- Hardhat node is running
- Contracts are deployed
- Token address in script matches `application.properties`

