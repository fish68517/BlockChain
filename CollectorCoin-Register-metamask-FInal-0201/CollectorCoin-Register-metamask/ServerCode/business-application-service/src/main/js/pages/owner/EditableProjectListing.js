const React = require("react");
const { useState } = require("react");
const { connect } = require("react-redux");

const { editProjectListingById } = require("../../actions/ProjectListings");
const ProjectListingForm = require("./ProjectListingForm");

function EditableProjectListing({ dispatch, listing, onFileClick }) {
  const [error, setError] = useState();

  const editProjectListingHandler = async (formData) => {
    try {
      // Owner only updates backend database
      // Blockchain contract update will be handled by admin during review/approval
      dispatch(editProjectListingById(listing.id, formData)).catch((e) => {
        console.log(e);
        setError("Error updating listing.");
      });
    } catch (error) {
      console.error("Error updating listing:", error);
      setError(error.message || "Unable to update listing");
    }
  };

  const onError = (msg) => setError(msg);

  return (
    <>
      <div className="my-3">
        <h4>Edit Listing</h4>
        <p className="text-muted">
          Note: Changes will be saved to the database. If a blockchain contract exists, 
          the admin will sync these changes during the review process.
        </p>
      </div>
      {listing.adminMessage && (
        <div>Request details: {listing.adminMessage}</div>
      )}
      <ProjectListingForm
        listing={listing}
        onSubmit={editProjectListingHandler}
        onError={onError}
        onFileClick={onFileClick}
      />
      {error && (
        <div className="d-inline-block mt-2 p-2 alert alert-danger">
          {error}
        </div>
      )}
    </>
  );
}

function mapStateToProps({ auth }) {
  return {
    user: auth.user,
  };
}

module.exports = connect(mapStateToProps)(EditableProjectListing);
