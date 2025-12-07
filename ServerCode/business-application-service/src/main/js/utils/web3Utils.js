const { ethers } = require("ethers");

const CCTOKEN_ADDRESS = "0xE109d1176B4c7C3Fa53b897C669AA5fB5Ab20a11";
const FACTORY_ADDRESS = "0x379eae4847bdf385641faefE2a5C5A671A69c997";

const CCTOKEN_ABI = require("../contracts/abi/CCToken.json");
const FACTORY_ABI = require("../contracts/abi/CCProjectFactory.json");
const PROJECT_ABI = require("../contracts/abi/CCProject.json");

const getCCTokenBalance = async () => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const ccTokenContract = new ethers.Contract(
      CCTOKEN_ADDRESS,
      CCTOKEN_ABI,
      signer
    );
    const walletAddress = await signer.getAddress();
    const decimals = await ccTokenContract.decimals();
    const tokens = await ccTokenContract.balanceOf(walletAddress);
    return await ethers.utils.formatEther(tokens, decimals);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const appendZeros = (amount, count) => {
  amount = amount.toString();
  for (let i = 0; i < count; i++) {
    amount = amount + "0";
  }
  return amount;
};

const getWalletAddress = async () => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    return await signer.getAddress();
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const approveFunding = async (projectAddress, amount) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const ccTokenContract = new ethers.Contract(
      CCTOKEN_ADDRESS,
      CCTOKEN_ABI,
      signer
    );
    const tx = await ccTokenContract.increaseAllowance(
      projectAddress,
      appendZeros(amount, 18)
    );
    await tx.wait();
    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const createCCProjectContract = async (formData) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const ownerAddress = formData.get("walletAddress");
    const listing = JSON.parse(formData.get("listing"));
    const vinNumber = listing.vin;
    const make = listing.make;
    const model = listing.model;
    const ccpg = appendZeros(listing.ccpg, 18);
    const fundingGoal = appendZeros(listing.fundingGoal, 18);

    let projectAddresses = await factoryContract.getProjects();
    const projectCount = projectAddresses.length;

    const tx = await factoryContract.createNewProject(
      vinNumber,
      make,
      model,
      ccpg,
      fundingGoal,
      ownerAddress
    );
    await tx.wait();
    console.log(tx.hash);

    projectAddresses = await factoryContract.getProjects();
    while (projectAddresses.length == projectCount) {
      projectAddresses = await factoryContract.getProjects();
    }

    projectAddress = projectAddresses[projectAddresses.length - 1];
    console.log(projectAddress);

    return projectAddress;
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const approveCCProjectListing = async (projectAddress) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.approveProject(projectAddress);
    await tx.wait();
    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const saveEstCCProjectListing = async (
  projectAddress,
  valueEstimate,
  repairEstimate
) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.saveEstimations(
      projectAddress,
      valueEstimate,
      repairEstimate
    );
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const setRestorerCCProjectListing = async (
  projectAddress,
  restorerAddress,
  fundingGoal
) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.setRestorer(
      projectAddress,
      restorerAddress,
      appendZeros(fundingGoal, 18)
    );
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const assignRestorationCCProjectListing = async (projectAddress) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.assignForRestoration(projectAddress);
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const openAuctionCCProjectListing = async (projectAddress) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.setAuctionOpen(projectAddress);
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const setBuyerCCProjectListing = async (projectAddress, buyerAddress) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.setBuyer(projectAddress, buyerAddress);
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const redistributeCCProjectListing = async (projectAddress) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const tx = await factoryContract.redistribute(projectAddress);
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const updateCCProjectListing = async (projectAddress, formData) => {
  try {
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      FACTORY_ADDRESS,
      FACTORY_ABI,
      signer
    );

    const listing = JSON.parse(formData.get("listing"));
    const vinNumber = listing.vin;
    const make = listing.make;
    const model = listing.model;
    const ccpg = appendZeros(listing.ccpg, 18);
    const fundingGoal = appendZeros(listing.fundingGoal, 18);

    const tx = await factoryContract.editProjectDetails(
      projectAddress,
      vinNumber,
      make,
      model,
      ccpg,
      fundingGoal
    );
    await tx.wait();
    console.log(tx);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const investInCCProjectListing = async (projectAddress, amount) => {
  try {
    await approveFunding(projectAddress, amount);
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const projectContract = new ethers.Contract(
      projectAddress,
      PROJECT_ABI,
      signer
    );

    const tx = await projectContract.acceptFunds(appendZeros(amount, 18));
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const bidForCCProjectListing = async (projectAddress, amount) => {
  try {
    await approveFunding(projectAddress, amount);
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const projectContract = new ethers.Contract(
      projectAddress,
      PROJECT_ABI,
      signer
    );

    const tx = await projectContract.addNewAuctionBid(appendZeros(amount, 18));
    await tx.wait();

    console.log(tx.hash);
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

module.exports = {
  getCCTokenBalance,
  getWalletAddress,
  approveFunding,
  createCCProjectContract,
  updateCCProjectListing,
  approveCCProjectListing,
  saveEstCCProjectListing,
  setRestorerCCProjectListing,
  assignRestorationCCProjectListing,
  openAuctionCCProjectListing,
  setBuyerCCProjectListing,
  redistributeCCProjectListing,
  investInCCProjectListing,
  bidForCCProjectListing,
};
