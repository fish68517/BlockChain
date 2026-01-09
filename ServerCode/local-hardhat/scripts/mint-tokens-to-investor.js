const hre = require("hardhat");

async function main() {
  const TOKEN_ADDRESS = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
  const INVESTOR_ADDRESS = process.env.INVESTOR_ADDRESS;
  const AMOUNT = process.env.AMOUNT || "100000000000000000000"; //100 CCT

  console.log("Investor address:", INVESTOR_ADDRESS);
  console.log("Amount to mint:", AMOUNT, "wei (", hre.ethers.utils.formatEther(AMOUNT), "tokens)");

  // Get contract instance
  const token = await hre.ethers.getContractAt("contracts/CCToken.sol:CCToken", TOKEN_ADDRESS);

  // Check current balance
  try {
    const currentBalance = await token.balanceOf(INVESTOR_ADDRESS);
    console.log("\nCurrent investor balance:", currentBalance.toString(), "wei (", hre.ethers.utils.formatEther(currentBalance), "tokens)");
  } catch (error) {
    console.error("Error checking balance:", error.message);
    process.exit(1);
  }

  // Mint tokens
  try {
    const mintTx = await token.mint(INVESTOR_ADDRESS, AMOUNT);
    await mintTx.wait();
    console.log("Tokens minted successfully!");

    // Verify new balance
    const newBalance = await token.balanceOf(INVESTOR_ADDRESS);
    console.log("\nNew investor balance:", newBalance.toString(), "wei (", hre.ethers.utils.formatEther(newBalance), "tokens)");
  } catch (error) {
    console.error("Error minting tokens:", error.message);
    throw error;
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });


