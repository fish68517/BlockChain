const hre = require("hardhat");

async function main() {
  // Get signers
  const [deployer, investor] = await hre.ethers.getSigners();
  console.log("Deployer:", deployer.address);
  console.log("Investor:", investor.address);
  
  // Contract addresses from application.properties
  const TOKEN_ADDRESS = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
  const FACTORY_ADDRESS = "0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512";
  
  // Replace with your project address
  const PROJECT_ADDRESS = process.env.PROJECT_ADDRESS || "YOUR_PROJECT_ADDRESS_HERE";
  const FUNDING_GOAL = process.env.FUNDING_GOAL || "5000000000000000000"; // 5 tokens in wei
  
  if (PROJECT_ADDRESS === "YOUR_PROJECT_ADDRESS_HERE") {
    console.error("❌ Please set PROJECT_ADDRESS environment variable");
    console.log("Usage: PROJECT_ADDRESS=0x... FUNDING_GOAL=5000000000000000000 npx hardhat run scripts/fund-project.js --network localhost");
    process.exit(1);
  }
  
  console.log("\n=== Funding Project ===");
  console.log("Project address:", PROJECT_ADDRESS);
  console.log("Funding goal:", FUNDING_GOAL);
  
  // Get contract instances (use fully qualified names to avoid ambiguity)
  const token = await hre.ethers.getContractAt("contracts/CCToken.sol:CCToken", TOKEN_ADDRESS);
  const project = await hre.ethers.getContractAt("contracts/CCProject.sol:CCProject", PROJECT_ADDRESS);
  
  // Check current funding status
  const fundsDeposited = await project.fundsDeposited();
  const fundingGoal = await project.fundingGoal();
  const fundingLive = await project.fundingLive();
  
  console.log("\nCurrent status:");
  console.log("  Funds deposited:", fundsDeposited.toString());
  console.log("  Funding goal:", fundingGoal.toString());
  console.log("  Funding live:", fundingLive);
  
  const remaining = fundingGoal.sub(fundsDeposited);
  console.log("  Remaining needed:", remaining.toString());
  
  if (remaining.eq(0)) {
    console.log("\n✅ Funding goal already reached!");
    return;
  }
  
  // Mint tokens to investor if needed
  console.log("\nMinting tokens to investor...");
  const investorBalance = await token.balanceOf(investor.address);
  console.log("Investor token balance:", investorBalance.toString());
  
  if (investorBalance.lt(remaining)) {
    console.log("Minting tokens...");
    const mintTx = await token.mint(investor.address, remaining);
    await mintTx.wait();
    console.log("✅ Tokens minted");
  }
  
  // Approve project to spend tokens
  console.log("\nApproving project to spend tokens...");
  const approveTx = await token.connect(investor).approve(PROJECT_ADDRESS, remaining);
  await approveTx.wait();
  console.log("✅ Approval granted");
  
  // Fund the project
  console.log("\nFunding project...");
  try {
    const fundTx = await project.connect(investor).acceptFunds(remaining);
    console.log("Transaction hash:", fundTx.hash);
    await fundTx.wait();
    console.log("✅ Project funded successfully!");
    
    // Verify
    const newFundsDeposited = await project.fundsDeposited();
    const newFundingLive = await project.fundingLive();
    console.log("\nUpdated status:");
    console.log("  Funds deposited:", newFundsDeposited.toString());
    console.log("  Funding goal:", fundingGoal.toString());
    console.log("  Funding live:", newFundingLive);
    
    if (newFundsDeposited.eq(fundingGoal) && !newFundingLive) {
      console.log("\n✅ Funding complete! You can now call assignForRestoration()");
    }
  } catch (error) {
    console.error("❌ Error funding project:", error.message);
    throw error;
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

