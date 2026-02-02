const hre = require("hardhat");

async function main() {
  // Deploy CCToken
  console.log("Deploying CCToken...");
  const CCToken = await hre.ethers.getContractFactory("CCToken");
  const ccToken = await CCToken.deploy();
  await ccToken.waitForDeployment();
  const tokenAddress = await ccToken.getAddress();
  console.log("CCToken deployed to:", tokenAddress);

  // Deploy CCMarketPlace
  console.log("Deploying CCMarketPlace...");
  const CCMarketPlace = await hre.ethers.getContractFactory("CCMarketPlace");
  const marketplace = await CCMarketPlace.deploy();
  await marketplace.waitForDeployment();
  const address = await marketplace.getAddress();
  console.log("CCMarketPlace deployed to:", address);

  // Wait for confirmations (skip on localhost)
  if (hre.network.name !== 'localhost' && hre.network.name !== 'hardhat') {
    console.log("Waiting for confirmations...");
    await marketplace.deploymentTransaction().wait(5);
  }

  // Verify on Etherscan
  if (process.env.ETHERSCAN_API_KEY) {
    console.log("Verifying on Etherscan...");
    await hre.run("verify:verify", {
      address: address,
      constructorArguments: [],
    });
  }

  console.log("Deployment complete!");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
