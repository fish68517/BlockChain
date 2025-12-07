// We require the Hardhat Runtime Environment explicitly here. This is optional
// but useful for running the script in a standalone fashion through `node <script>`.
//
// You can also run a script with `npx hardhat run <script>`. If you do that, Hardhat
// will compile your contracts, add the Hardhat Runtime Environment's members to the
// global scope, and execute the script.
const hre = require("hardhat");
//
// async function main() {
//   const currentTimestampInSeconds = Math.round(Date.now() / 1000);
//   const ONE_YEAR_IN_SECS = 365 * 24 * 60 * 60;
//   const unlockTime = currentTimestampInSeconds + ONE_YEAR_IN_SECS;
//
//   const lockedAmount = hre.ethers.utils.parseEther("1");
//
//   const Lock = await hre.ethers.getContractFactory("Lock");
//   const lock = await Lock.deploy(unlockTime, { value: lockedAmount });
//
//   await lock.deployed();
//
//   console.log(
//     `Lock with 1 ETH and unlock timestamp ${unlockTime} deployed to ${lock.address}`
//   );
// }
//
// // We recommend this pattern to be able to use async/await everywhere
// // and properly handle errors.
// main().catch((error) => {
//   console.error(error);
//   process.exitCode = 1;
// });


const main = async () => {
  const CCProject = await hre.ethers.getContractFactory("CCProject");
  const ccProject = await CCProject.deploy("VIN","HONDA","CRV",true,3000,10000,"0x4BDEBC0ed6078a8aa4b1d8B8Be023bB393fB4ecf");
  await ccProject.deployed();


  const CCToken = await  hre.ethers.getContractFactory("CCSwapToken");
  const ccToken = await CCToken.deploy();
  await ccToken.deployed();
  console.log("CCSwapToken deployed to: ", ccToken.address);


  const CCProjectFactory = await  hre.ethers.getContractFactory("CCProjectFactory");
  const ccProjectFactory = await CCProjectFactory.deploy();
  await ccProjectFactory.deployed();
  console.log("CCProjectFactory deployed to: ", ccProjectFactory.address);

}
const runMain = async () => {
  try {
    await main();
    process.exit(0);
  } catch (error) {
    console.log(error);
    process.exit(1);
  }
}

runMain();