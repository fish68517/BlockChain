const React = require("react");
const { useState } = require("react");
const { connect } = require("react-redux");
const { Container } = require("react-bootstrap");
const { getWalletAddress } = require("../../utils/web3Utils");

const {
  createProjectListingByUserID,
} = require("../../actions/ProjectListings");
const ProjectListingForm = require("./ProjectListingForm");

const NewProjectListing = ({ user, dispatch }) => {
  const [status, setStatus] = useState();
  const [error, setError] = useState();


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
      let walletAddress;
      if (window.ethereum) {
        walletAddress = await getWalletAddress();
      } else {
        setError("MetaMask is required to get your wallet address. Please install MetaMask.");
        return;
      }

      // Add wallet address to form data (projectAddress will be created by admin on approval)
      formData.append("walletAddress", walletAddress);
      // Note: projectAddress is not created here - it will be created by admin when approving
      createProjectListing(formData);
    } catch (error) {
      console.error("Error creating project:", error);
      setError(error.message || "Unable to create project listing");
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
