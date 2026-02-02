const React = require('react');
const { useState } = require('react');
const { connect } = require('react-redux');
const { updateCCProjectListing } = require('../../utils/web3Utils');
const MetaConnectModal = require('../../components/web3/MetaConnectModal');

const { editProjectListingById } = require('../../actions/ProjectListings');
const ProjectListingForm = require('./ProjectListingForm');

function EditableProjectListing({ dispatch, listing, onFileClick }) {
  const [error, setError] = useState();
  const [showModal, setShowModal] = useState(false);

  const editProjectListing = (formData) => {
    dispatch(editProjectListingById(listing.id, formData))
    .catch(e => {
      console.log(e);
      setError('Error updating listing.');
    });
  };

  const editProjectListingHandler = async (formData) => {
    if(window.ethereum) {
      await updateCCProjectListing(listing.projectAddress, formData);
      editProjectListing(formData);
    } else {
      setShowModal(true);
      setError("Unable to edit Project Listing contract");
      throw "Unable to edit Project Listing contract";
    }
  }

  const onError = msg => setError(msg);

  return (
    <>
      <MetaConnectModal setShowModal={setShowModal} showModal={showModal} />
      <div className="my-3">
        <h4>Edit Listing</h4>
      </div>
      {listing.adminMessage &&
        <div>Request details: {listing.adminMessage}</div>
      }
      <ProjectListingForm listing={listing} onSubmit={editProjectListingHandler} onError={onError} onFileClick={onFileClick} />
      {error && <div className="d-inline-block mt-2 p-2 alert alert-danger">{error}</div>}
    </>
  );
}

function mapStateToProps({ auth }) {
  return {
    user: auth.user,
  }
}

module.exports = connect(mapStateToProps)(EditableProjectListing);
