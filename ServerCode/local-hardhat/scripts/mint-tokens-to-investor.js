const hre = require("hardhat");

async function main() {
  // Get signers
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deployer (owner):", deployer.address);

  // Contract address from application.properties
  const TOKEN_ADDRESS = "0x5FbDB2315678afecb367f032d93F642f64180aa3";

  // Get investor address from environment variable or use a default
  const INVESTOR_ADDRESS = process.env.INVESTOR_ADDRESS;
  const AMOUNT = process.env.AMOUNT || "100000000000000000000"; // 100 tokens in wei (default)

  if (!INVESTOR_ADDRESS) {
    console.error("Please set INVESTOR_ADDRESS environment variable");
    console.log("Usage: INVESTOR_ADDRESS=0x... AMOUNT=100000000000000000000 npx hardhat run scripts/mint-tokens-to-investor.js --network localhost");
    console.log("\nTo get your MetaMask investor address:");
    console.log("1. Open MetaMask");
    console.log("2. Copy your account address (0x...)");
    console.log("3. Set it as INVESTOR_ADDRESS");
    process.exit(1);
  }

  console.log("\n=== Minting CCT Tokens ===");
  console.log("Investor address:", INVESTOR_ADDRESS);
  console.log("Amount to mint:", AMOUNT, "wei (", hre.ethers.utils.formatEther(AMOUNT), "tokens)");

  // Get contract instance (use fully qualified name to avoid ambiguity)
  const token = await hre.ethers.getContractAt("contracts/CCToken.sol:CCToken", TOKEN_ADDRESS);

  // Check current balance
  try {
    const currentBalance = await token.balanceOf(INVESTOR_ADDRESS);
    console.log("\nCurrent investor balance:", currentBalance.toString(), "wei (", hre.ethers.utils.formatEther(currentBalance), "tokens)");
  } catch (error) {
    console.error("Error checking balance:", error.message);
    console.error("   Make sure the token contract is deployed at:", TOKEN_ADDRESS);
    process.exit(1);
  }

  // Mint tokens
  console.log("\nMinting tokens...");
  try {
    const mintTx = await token.mint(INVESTOR_ADDRESS, AMOUNT);
    console.log("Transaction hash:", mintTx.hash);
    await mintTx.wait();
    console.log("Tokens minted successfully!");

    // Verify new balance
    const newBalance = await token.balanceOf(INVESTOR_ADDRESS);
    console.log("\nNew investor balance:", newBalance.toString(), "wei (", hre.ethers.utils.formatEther(newBalance), "tokens)");
    console.log("\nInvestor can now invest in projects!");
  } catch (error) {
    console.error("Error minting tokens:", error.message);
    if (error.message.includes("Ownable: caller is not the owner")) {
      console.error("\n   The deployer account is not the owner of the token contract.");
      console.error("   Make sure you're using the correct deployer account.");
      console.error("   Deployer address:", deployer.address);
    }
    throw error;
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

