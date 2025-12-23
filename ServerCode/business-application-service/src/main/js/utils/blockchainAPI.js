const createBaseRequest = require("../common/http-common");

/**
 * Backend Blockchain API Service
 * 
 * This service replaces the old web3Utils.js functions for admin operations.
 * All blockchain operations are now handled by the backend.
 * No MetaMask required for admin operations.
 */

/**
 * Create a new project contract on the blockchain
 * @param {Object} projectData - Project data with vin, make, model, ccpg, fundingGoal, ownerAddress
 * @returns {Promise<string>} Project contract address
 */
const createProjectContract = async (projectData) => {
  try {
    const response = await createBaseRequest().post("/blockchain/admin/create-project", {
      vin: projectData.vin,
      make: projectData.make,
      model: projectData.model,
      ccpg: projectData.ccpg,
      fundingGoal: projectData.fundingGoal,
      ownerAddress: projectData.ownerAddress
    });
    return response.data; // Returns project address as string
  } catch (error) {
    console.error("Error creating project contract:", error);
    throw new Error(error.response?.data || "Failed to create project contract");
  }
};

/**
 * Approve a project on the blockchain
 * @param {string} projectAddress - The project contract address
 */
const approveProject = async (projectAddress) => {
  try {
    await createBaseRequest().post("/blockchain/admin/approve-project", null, {
      params: { projectAddress }
    });
  } catch (error) {
    console.error("Error approving project:", error);
    const errorMsg = error.response?.data || error.message || "Failed to approve project";
    throw new Error(errorMsg);
  }
};

/**
 * Save estimations for a project
 * @param {string} projectAddress - The project contract address
 * @param {string} valueEst - Value estimate (as string)
 * @param {string} repairEst - Repair cost estimate (as string)
 */
const saveEstimations = async (projectAddress, valueEst, repairEst) => {
  try {
    await createBaseRequest().post("/blockchain/admin/save-estimations", null, {
      params: {
        projectAddress,
        valueEst,
        repairEst
      }
    });
  } catch (error) {
    console.error("Error saving estimations:", error);
    throw new Error(error.response?.data || "Failed to save estimations");
  }
};

/**
 * Set restorer for a project
 * @param {string} projectAddress - The project contract address
 * @param {string} restorerAddress - The restorer's Ethereum address
 * @param {string} fundingGoal - The funding goal (as string)
 */
const setRestorer = async (projectAddress, restorerAddress, fundingGoal) => {
  try {
    await createBaseRequest().post("/blockchain/admin/set-restorer", null, {
      params: {
        projectAddress,
        restorerAddress,
        fundingGoal
      }
    });
  } catch (error) {
    console.error("Error setting restorer:", error);
    throw new Error(error.response?.data || "Failed to set restorer");
  }
};

/**
 * Assign restoration for a project
 * @param {string} projectAddress - The project contract address
 */
const assignRestoration = async (projectAddress) => {
  try {
    await createBaseRequest().post("/blockchain/admin/assign-restoration", null, {
      params: { projectAddress }
    });
  } catch (error) {
    console.error("Error assigning restoration:", error);
    throw new Error(error.response?.data || "Failed to assign restoration");
  }
};

/**
 * Open auction for a project
 * @param {string} projectAddress - The project contract address
 */
const openAuction = async (projectAddress) => {
  try {
    await createBaseRequest().post("/blockchain/admin/open-auction", null, {
      params: { projectAddress }
    });
  } catch (error) {
    console.error("Error opening auction:", error);
    throw new Error(error.response?.data || "Failed to open auction");
  }
};

/**
 * Set buyer for a project
 * @param {string} projectAddress - The project contract address
 * @param {string} buyerAddress - The buyer's Ethereum address
 */
const setBuyer = async (projectAddress, buyerAddress) => {
  try {
    await createBaseRequest().post("/blockchain/admin/set-buyer", null, {
      params: {
        projectAddress,
        buyerAddress
      }
    });
  } catch (error) {
    console.error("Error setting buyer:", error);
    throw new Error(error.response?.data || "Failed to set buyer");
  }
};

/**
 * Redistribute funds for a project
 * @param {string} projectAddress - The project contract address
 */
const redistribute = async (projectAddress) => {
  try {
    await createBaseRequest().post("/blockchain/admin/redistribute", null, {
      params: { projectAddress }
    });
  } catch (error) {
    console.error("Error redistributing:", error);
    throw new Error(error.response?.data || "Failed to redistribute");
  }
};

/**
 * Get token balance for an address
 * @param {string} address - Ethereum address
 * @returns {Promise<string>} Token balance
 */
const getTokenBalance = async (address) => {
  try {
    const response = await createBaseRequest().get(`/blockchain/balance/${address}`);
    return response.data.toString();
  } catch (error) {
    console.error("Error getting token balance:", error);
    throw new Error(error.response?.data || "Failed to get token balance");
  }
};

module.exports = {
  createProjectContract,
  approveProject,
  saveEstimations,
  setRestorer,
  assignRestoration,
  openAuction,
  setBuyer,
  redistribute,
  getTokenBalance
};

