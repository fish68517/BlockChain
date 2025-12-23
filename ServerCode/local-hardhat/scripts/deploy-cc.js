const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deploying with:", deployer.address);

  // Use fully qualified name to disambiguate
  const CCToken = await hre.ethers.getContractFactory("contracts/CCToken.sol:CCToken");
  const ccToken = await CCToken.deploy();
  await ccToken.deployed();
  console.log("CCToken deployed to:", ccToken.address);

  const CCProjectFactory = await hre.ethers.getContractFactory("contracts/CCProjectFactory.sol:CCProjectFactory");
  const factory = await CCProjectFactory.deploy(ccToken.address);
  await factory.deployed();
  console.log("CCProjectFactory deployed to:", factory.address);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});