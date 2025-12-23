const React = require("react");
const { useState } = require("react");
const { connect } = require("react-redux");
const { Container } = require("react-bootstrap");
const { createProjectContract } = require("../../utils/blockchainAPI");
const { getWalletAddress } = require("../../utils/web3Utils");

const {
  createProjectListingByUserID,
} = require("../../actions/ProjectListings");
const ProjectListingForm = require("./ProjectListingForm");

const NewProjectListing = ({ user, dispatch }) => {
  const [status, setStatus] = useState();
  const [error, setError] = useState();

  const appendZeros = (amount, count) => {
    amount = amount.toString();
    for (let i = 0; i < count; i++) {
      amount = amount + "0";
    }
    return amount;
  };

  const createProjectListing = (formData) => {
    dispatch(createProjectListingByUserID(user.id, formData))
      .then(() => {
        setError();
        setStatus("Successfully created listing.");
      })
      .catch((e) => {
        console.log(e);
        setError("Error creating listing.");
      });
  };

  const createProjectListingHandler = async (formData) => {
    try {
      // Get owner wallet address (still need this from user's MetaMask for the owner field)
      let walletAddress;
      if (window.ethereum) {
        walletAddress = await getWalletAddress();
      } else {
        setError("MetaMask is required to get your wallet address. Please install MetaMask.");
        return;
      }

      // Parse form data
      const listing = JSON.parse(formData.get("listing"));
      const vinNumber = listing.vin;
      const make = listing.make;
      const model = listing.model;
      const ccpg = appendZeros(listing.ccpg, 18);
      const fundingGoal = appendZeros(listing.fundingGoal, 18);

      // Create project contract via backend API
      const projectAddress = await createProjectContract({
        vin: vinNumber,
        make: make,
        model: model,
        ccpg: ccpg,
        fundingGoal: fundingGoal,
        ownerAddress: walletAddress
      });

      // Add to form data and save to database
      formData.append("walletAddress", walletAddress);
      formData.append("projectAddress", projectAddress);
      createProjectListing(formData);
    } catch (error) {
      console.error("Error creating project:", error);
      setError(error.message || "Unable to create Project Listing contract");
    }
  };

  const onError = (msg) => setError(msg);

  return (
    <Container>
      <div className="my-4">
        <h3>Create New Project Listing</h3>
        <p className="text-muted">Submit your vehicle project to start the restoration process</p>
      </div>
      <div className="card">
        <div className="card-body">
          <ProjectListingForm
            onSubmit={createProjectListingHandler}
            onError={onError}
          />
        </div>
      </div>
      {status && (
        <div className="alert alert-success mt-3">
          {status}
        </div>
      )}
      {error && (
        <div className="alert alert-danger mt-3">
          {error}
        </div>
      )}
    </Container>
  );
};

const mapStateToProps = (state) => ({ user: state.auth.user });

module.exports = connect(mapStateToProps)(NewProjectListing);
