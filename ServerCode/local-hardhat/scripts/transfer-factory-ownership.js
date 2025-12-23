const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log("Current deployer address:", deployer.address);
  
  // Factory address from application.properties
  const FACTORY_ADDRESS = "0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512";
  
  // Admin wallet address (derived from private key in application.properties)
  // Private key: 0xea6c44ac03bff858b476bba40716402b03e41b8e97e276d1baec7c37d42484a0
  // We'll derive the address from the private key
  const { ethers } = require("ethers");
  const adminWallet = new ethers.Wallet("0xea6c44ac03bff858b476bba40716402b03e41b8e97e276d1baec7c37d42484a0");
  const ADMIN_ADDRESS = adminWallet.address;
  
  console.log("Factory address:", FACTORY_ADDRESS);
  console.log("Admin address (target owner):", ADMIN_ADDRESS);
  
  // Get factory contract instance
  const factory = await hre.ethers.getContractAt("CCProjectFactory", FACTORY_ADDRESS);
  
  // Check current owner
  const currentOwner = await factory.owner();
  console.log("\nCurrent factory owner:", currentOwner);
  
  if (currentOwner.toLowerCase() === ADMIN_ADDRESS.toLowerCase()) {
    console.log("✅ Factory is already owned by admin address!");
    return;
  }
  
  // Check if deployer is the current owner
  if (currentOwner.toLowerCase() !== deployer.address.toLowerCase()) {
    console.log("❌ Deployer is not the current owner. Cannot transfer ownership.");
    console.log("Please use the account that owns the factory to transfer ownership.");
    return;
  }
  
  console.log("\nTransferring factory ownership to admin address...");
  
  // Transfer ownership
  const tx = await factory.transferOwnership(ADMIN_ADDRESS);
  console.log("Transaction hash:", tx.hash);
  
  await tx.wait();
  console.log("✅ Ownership transferred!");
  
  // Verify new owner
  const newOwner = await factory.owner();
  console.log("New factory owner:", newOwner);
  
  if (newOwner.toLowerCase() === ADMIN_ADDRESS.toLowerCase()) {
    console.log("✅ Verification successful! Factory is now owned by admin address.");
  } else {
    console.log("❌ Verification failed. Owner mismatch.");
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

