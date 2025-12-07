const React = require('react');
const { useState } = require('react');
const { connect } = require('react-redux');
const { Container } = require('react-bootstrap');
const MetaConnectModal = require('../../components/web3/MetaConnectModal');
const { getWalletAddress, createCCProjectContract } = require('../../utils/web3Utils');

const { createProjectListingByUserID } = require('../../actions/ProjectListings');
const ProjectListingForm = require('./ProjectListingForm');

const NewProjectListing = ({ user, dispatch }) => {
  const [status, setStatus] = useState();
  const [error, setError] = useState();
  const [showModal, setShowModal] = useState(false);

  const createProjectListing = (formData) => {
    dispatch(createProjectListingByUserID(user.id, formData))
    .then(() => {
      setError();
      setStatus('Successfully created listing.');

    }).catch(e => {
      console.log(e);
      setError('Error creating listing.');
    });
  };

  const createProjectListingHandler = async (formData) => {
    if(window.ethereum) {
      const walletAddress = await getWalletAddress();
      formData.append('walletAddress', walletAddress);
      const projectAddress = await createCCProjectContract(formData);
      formData.append('projectAddress', projectAddress);
      createProjectListing(formData);
    } else {
      setShowModal(true);
      setError("Unable to create Project Listing contract");
      throw "Unable to create Project Listing contract";
    }
  };

  const onError = msg => setError(msg);

  return (
    <Container>
      <MetaConnectModal showModal={showModal} setShowModal={setShowModal} />
      <div className="my-3">
        <h3>Create New Project Listing</h3>
      </div>
      <ProjectListingForm onSubmit={createProjectListingHandler} onError={onError} />
      {status && <div className="d-inline-block mt-2 p-2 alert alert-success">{status}</div>}
      {error && <div className="d-inline-block mt-2 p-2 alert alert-danger">{error}</div>}
    </Container>
  )
}

const mapStateToProps = (state) => ({ user: state.auth.user });

module.exports = connect(mapStateToProps)(NewProjectListing);
