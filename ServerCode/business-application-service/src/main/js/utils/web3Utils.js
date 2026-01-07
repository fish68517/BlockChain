const { ethers } = require("ethers");
const createBaseRequest = require("../common/http-common");

const CCTOKEN_ABI = require("../contracts/abi/CCToken.json");
const FACTORY_ABI = require("../contracts/abi/CCProjectFactory.json");
const PROJECT_ABI = require("../contracts/abi/CCProject.json");

let CCTOKEN_ADDRESS = null;
let FACTORY_ADDRESS = null;
let contractAddressesPromise = null;

/**
 * Fetch contract addresses from backend API
 * These are read from application.properties server-side
 */
const fetchContractAddresses = async () => {
  if (contractAddressesPromise) {
    return contractAddressesPromise;
  }

  contractAddressesPromise = (async () => {
    try {
      const response = await createBaseRequest().get("/api/blockchain/config/contracts");
      CCTOKEN_ADDRESS = response.data.tokenAddress;
      FACTORY_ADDRESS = response.data.factoryAddress;
      console.log("Contract addresses loaded from backend:", {
        token: CCTOKEN_ADDRESS,
        factory: FACTORY_ADDRESS
      });
      return { token: CCTOKEN_ADDRESS, factory: FACTORY_ADDRESS };
    } catch (error) {
      console.error("Error fetching contract addresses from backend:", error);
      // Fallback to hardcoded values if API call fails
      CCTOKEN_ADDRESS = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
      FACTORY_ADDRESS = "0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512";
      console.warn("Using fallback contract addresses");
      return { token: CCTOKEN_ADDRESS, factory: FACTORY_ADDRESS };
    }
  })();

  return contractAddressesPromise;
};

/**
 * Fetching contract addresses from backend if not already loaded
 */
const getContractAddresses = async () => {
  if (!CCTOKEN_ADDRESS || !FACTORY_ADDRESS) {
    await fetchContractAddresses();
  }
  return {
    token: CCTOKEN_ADDRESS,
    factory: FACTORY_ADDRESS
  };
};

const getCCTokenBalance = async () => {
  try {
    // To ensure contract addresses are loaded first
    const addresses = await getContractAddresses();
    const tokenAddress = addresses.token;
    
    await window.ethereum.request({ method: "eth_requestAccounts" });
    
    // Get chain ID directly from MetaMask first
    const chainIdHex = await window.ethereum.request({ method: "eth_chainId" });
    const chainId = parseInt(chainIdHex, 16);
    const expectedChainId = 31337; // Hardhat local network
    
    console.log("MetaMask Chain ID:", chainId, "Expected:", expectedChainId);
    
    if (chainId !== expectedChainId) {
      // Try to switch network automatically
      try {
        await window.ethereum.request({
          method: 'wallet_switchEthereumChain',
          params: [{ chainId: '0x7A69' }], // 31337 in hex
        });
        // Wait a moment for network to switch
        await new Promise(resolve => setTimeout(resolve, 1000));
        // Check again
        const newChainIdHex = await window.ethereum.request({ method: "eth_chainId" });
        const newChainId = parseInt(newChainIdHex, 16);
        if (newChainId !== expectedChainId) {
          throw new Error(`Please switch to Hardhat Local network (Chain ID: ${expectedChainId}). Current network: Chain ID ${newChainId}`);
        }
      } catch (switchError) {
        if (switchError.code === 4902) {
          try {
            await window.ethereum.request({
              method: 'wallet_addEthereumChain',
              params: [{
                chainId: '0x7A69', // 31337 in hex
                chainName: 'Hardhat Local',
                nativeCurrency: {
                  name: 'Ethereum',
                  symbol: 'ETH',
                  decimals: 18
                },
                rpcUrls: ['http://127.0.0.1:8545'],
                blockExplorerUrls: null
              }],
            });
            // Wait a moment for network to be added and switched
            await new Promise(resolve => setTimeout(resolve, 1000));
          } catch (addError) {
            throw new Error(`Please manually switch to Hardhat Local network (Chain ID: ${expectedChainId}). Current network: Chain ID ${chainId}. Error: ${addError.message}`);
          }
        } else {
          throw new Error(`Please switch to Hardhat Local network (Chain ID: ${expectedChainId}). Current network: Chain ID ${chainId}. Error: ${switchError.message}`);
        }
      }
    }
    
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
    const errorMessage = ex.message || "Failed to get token balance. Make sure Hardhat node is running and MetaMask is connected to Localhost 8545 (Chain ID: 31337).";
    throw errorMessage;
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
  
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    // To ensure contract addresses are loaded
    const addresses = await getContractAddresses();
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
    const signer = provider.getSigner();
    const projectContract = new ethers.Contract(
      projectAddress,
      PROJECT_ABI,
      signer
    );

    const tx = await projectContract.acceptFunds(appendZeros(amount, 18));
    const receipt = await tx.wait();

    console.log("Investment transaction hash:", receipt.transactionHash);
    // Return transaction hash for backend synchronization
    return receipt.transactionHash;
  } catch (ex) {
    console.log(ex);
    throw "Web3 Error!";
  }
};

const bidForCCProjectListing = async (projectAddress, amount) => {
  try {
    await approveFunding(projectAddress, amount);
    
    const provider = new ethers.providers.Web3Provider(
      window.ethereum,
      "any"
    );
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
