const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deploying with:", deployer.address);

  const CCToken = await hre.ethers.getContractFactory("contracts/CCToken.sol:CCToken");
  const ccToken = await CCToken.deploy();
  await ccToken.deployed();
  console.log("CCToken deployed to:", ccToken.address);

  const CCProjectFactory = await hre.ethers.getContractFactory("contracts/CCProjectFactory.sol:CCProjectFactory");
  const factory = await CCProjectFactory.deploy(ccToken.address);
  await factory.deployed();
  console.log("CCProjectFactory deployed to:", factory.address);

  // Transfer ownership to admin account (from application.properties)
  const { ethers } = require("ethers");
  const adminWallet = new ethers.Wallet("0xea6c44ac03bff858b476bba40716402b03e41b8e97e276d1baec7c37d42484a0");
  const ADMIN_ADDRESS = adminWallet.address;
  
  console.log("Transferring to admin:", ADMIN_ADDRESS);
  
  const tx = await factory.transferOwnership(ADMIN_ADDRESS);
  await tx.wait();
  
  // Verify ownership transfer
  const newOwner = await factory.owner();
  if (newOwner.toLowerCase() === ADMIN_ADDRESS.toLowerCase()) {
    console.log("Ownership transferred successfully!");
  } else {
    console.log("Ownership transfer failed!");
  }

}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});