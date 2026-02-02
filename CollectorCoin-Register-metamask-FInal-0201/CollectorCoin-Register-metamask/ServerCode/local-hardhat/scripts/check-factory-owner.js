const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deployer address:", deployer.address);
  
  // Factory address from application.properties
  const FACTORY_ADDRESS = "0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512";
  
  // Admin wallet address (derived from private key in application.properties)
  const { ethers } = require("ethers");
  const adminWallet = new ethers.Wallet("0xea6c44ac03bff858b476bba40716402b03e41b8e97e276d1baec7c37d42484a0");
  const ADMIN_ADDRESS = adminWallet.address;
  
  console.log("\nFactory address:", FACTORY_ADDRESS);
  console.log("Admin address (from application.properties):", ADMIN_ADDRESS);
  
  // Get factory contract instance
  const factory = await hre.ethers.getContractAt("CCProjectFactory", FACTORY_ADDRESS);
  
  // Check current owner
  const currentOwner = await factory.owner();
  console.log("\nCurrent factory owner:", currentOwner);
  console.log("Deployer address:", deployer.address);
  console.log("Admin address:", ADMIN_ADDRESS);
  
  console.log("\n=== Status ===");
  if (currentOwner.toLowerCase() === ADMIN_ADDRESS.toLowerCase()) {
    console.log("✅ Factory is owned by admin address - Ready to use!");
  } else if (currentOwner.toLowerCase() === deployer.address.toLowerCase()) {
    console.log("⚠️  Factory is owned by deployer, not admin address");
    console.log("   Run: npx hardhat run scripts/transfer-factory-ownership.js --network localhost");
  } else {
    console.log("❌ Factory is owned by a different address");
    console.log("   Current owner:", currentOwner);
    console.log("   Expected admin:", ADMIN_ADDRESS);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

