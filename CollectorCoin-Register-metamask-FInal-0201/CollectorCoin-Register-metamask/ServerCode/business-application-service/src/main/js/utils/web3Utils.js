const { ethers } = require("ethers");

const CCTOKEN_ADDRESS = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
const FACTORY_ADDRESS = "0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512";

const CCTOKEN_ABI = require("../contracts/abi/CCToken.json");
const FACTORY_ABI = require("../contracts/abi/CCProjectFactory.json");
const PROJECT_ABI = require("../contracts/abi/CCProject.json");


const getContractAddresses = () => {
  return {
    token: CCTOKEN_ADDRESS,
    factory: FACTORY_ADDRESS
  };
};

const getCCTokenBalance = async () => {
  try {
    const addresses = getContractAddresses();
    const tokenAddress = addresses.token;
    
    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    
    // Check if contract exists at address
    const code = await provider.getCode(tokenAddress);
    if (code === "0x" || code === "0x0") {
      throw new Error(`Token contract not found at address ${tokenAddress}. Make sure contracts are deployed to Hardhat network.`);
    }
    
    const ccTokenContract = new ethers.Contract(
      tokenAddress,
      CCTOKEN_ABI,
      signer
    );
    const walletAddress = await signer.getAddress();
    const decimals = await ccTokenContract.decimals();
    const tokens = await ccTokenContract.balanceOf(walletAddress);
    return await ethers.utils.formatEther(tokens, decimals);
  } catch (ex) {
    console.error("Error getting token balance:", ex);
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
    const addresses = getContractAddresses();
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const ccTokenContract = new ethers.Contract(
      addresses.token,
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
    const addresses = getContractAddresses();
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
  
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const addresses = getContractAddresses();
    const provider = await new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    provider.send("eth_requestAccounts", []);
    const signer = provider.getSigner();
    const factoryContract = new ethers.Contract(
      addresses.factory,
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
    const receipt = await tx.wait();

    console.log("Investment transaction hash:", receipt.transactionHash);
    return receipt.transactionHash;
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


/**
 * Check if MetaMask is installed
 */
const isMetaMaskInstalled = () => {
  return typeof window.ethereum !== 'undefined' && window.ethereum.isMetaMask;
};

/**
 * Connect to MetaMask and get accounts
 */
const connectMetaMask = async () => {
  if (!isMetaMaskInstalled()) {
    throw new Error("MetaMask is not installed. Please install MetaMask to continue.");
  }
  
  try {
    const accounts = await window.ethereum.request({ method: "eth_requestAccounts" });
    return accounts[0];
  } catch (error) {
    if (error.code === 4001) {
      throw new Error("Please connect to MetaMask to continue.");
    }
    throw error;
  }
};

/**
 * Sign a message with MetaMask
 */
const signMessage = async (message) => {
  if (!isMetaMaskInstalled()) {
    throw new Error("MetaMask is not installed");
  }

  const accounts = await window.ethereum.request({ method: "eth_accounts" });
  if (accounts.length === 0) {
    throw new Error("Please connect your wallet first");
  }

  const account = accounts[0];
  
  try {
    const signature = await window.ethereum.request({
      method: "personal_sign",
      params: [message, account]
    });
    return { signature, account };
  } catch (error) {
    if (error.code === 4001) {
      throw new Error("Signature request was rejected");
    }
    throw error;
  }
};

/**
 * Get current connected account
 */
const getCurrentAccount = async () => {
  if (!isMetaMaskInstalled()) {
    return null;
  }
  
  const accounts = await window.ethereum.request({ method: "eth_accounts" });
  return accounts.length > 0 ? accounts[0] : null;
};

/**
 * Listen for account changes
 */
const onAccountChange = (callback) => {
  if (isMetaMaskInstalled()) {
    window.ethereum.on('accountsChanged', callback);
  }
};

/**
 * Remove account change listener
 */
const removeAccountChangeListener = (callback) => {
  if (isMetaMaskInstalled()) {
    window.ethereum.removeListener('accountsChanged', callback);
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
  isMetaMaskInstalled,
  connectMetaMask,
  signMessage,
  getCurrentAccount,
  onAccountChange,
  removeAccountChangeListener,
};
