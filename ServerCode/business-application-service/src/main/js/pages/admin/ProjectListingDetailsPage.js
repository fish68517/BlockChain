const React = require("react");
const { useState } = require("react");
const { connect } = require("react-redux");
const { useParams } = require("react-router-dom");
const config = require("config");

const http = require("../../common/http-common");
const {
  addValueEstimation,
  submitReview,
  rejectListing,
  assignRestoration,
  postItemForSale,
  startAuction,
} = require("../../actions/ProjectListings");
const ValueEstimation = require("../../components/admin/ValueEstimation");
const Approve = require("../../components/admin/Approve");
const PostItemForSale = require("../../components/admin/PostItemForSale");
const StartAuction = require("../../components/admin/StartAuction");
const AssignRestoration = require("../../components/admin/AssignRestoration");
const ProjectListingStatus = require("../../components/common/ProjectListingStatus");


function ProjectListingDetails({ projectListings, dispatch }) {
  const { id } = useParams();
  const listing = projectListings.find((p) => p.id === parseInt(id));

  const [error, setError] = useState(null);

  const fetchFile = () => {
    http()
      .get(`${config.uploadsUrl}${listing.file.path}`, {
        responseType: "blob",
      })
      .then(({ data }) => {
        const href = URL.createObjectURL(data);
        const link = document.createElement("a");

        link.href = href;
        link.setAttribute("download", listing.file.fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(href);
      });
  };

  const onSubmitEstimationsHandler = (valueEstimation, repairCost) => {
    return dispatch(
      addValueEstimation(id, {
        valueEstimation,
        repairCostEstimation: repairCost,
      })
    ).catch((error) => {
      console.error("Error saving estimations:", error);
      setError(error.message || "Unable to update estimations");
      throw error;
    });
  };

  const onAssignRestoration = () => {
    return dispatch(assignRestoration(id)).catch((error) => {
      console.error("Error assigning restoration:", error);
      setError(error.message || "Unable to assign restoration");
      throw error;
    });
  };

  const onPostItemForSale = () => {
    return dispatch(postItemForSale(id));
  };

  const onStartAuction = () => {
    return dispatch(startAuction(id)).catch((error) => {
      console.error("Error starting auction:", error);
      setError(error.message || "Unable to start auction");
      throw error;
    });
  };

  const onSubmitApprovalHandler = (areDetailsVerified, isTitleReceived, message = null) => {
    return dispatch(
      submitReview(id, {
        verifyDetails: areDetailsVerified,
        receiveTitle: isTitleReceived,
        adminMessage: message,
      })
    ).catch((error) => {
      console.error("Error in approval process:", error);
      setError(error.message || "Unable to complete approval process");
      throw error;
    });
  };

  const onSubmitReject = () => {
    return dispatch(rejectListing(id));
  };

  if (!listing) {
    return <div>Could not find listing with id {id}.</div>;
  }

  const canEditEstimation = listing.pendingTasks.includes("Value Estimation");
  const canAssignRestoration =
    listing.pendingTasks.includes("Assign Restoration");
  const canPostItemForSale = listing.pendingTasks.includes("Post Item");
  const canStartAuction = listing.pendingTasks.includes("Auction");
  const showEstimationDialog = canEditEstimation || listing.valueEstimation;
  const canApprove =
    listing.pendingTasks.includes("Verify Details") ||
    listing.pendingTasks.includes("Receive Title");

  return (
    <div className="container project-details-page">
      <h3>Project Details</h3>
      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}
      <div className="my-3">
        <ProjectListingStatus listing={listing} />
      </div>
      <div className="mb-2">
        <strong>Vehicle: </strong>{listing.make} {listing.model}
      </div>
      <div className="mb-2">
        <strong>CCPG: </strong><span className="text-primary">{listing.ccpg}</span>
      </div>
      <div className="mb-2">
        <strong>VIN: </strong>{listing.vin}
      </div>
      <div className="mb-2">
        <strong>Initial funding requested: </strong><span className="text-success fw-bold">{listing.fundingGoal}</span>
      </div>
      {listing.description && (
        <div className="mb-2">
          <strong>Description: </strong>{listing.description}
        </div>
      )}
      <button className="btn btn-outline-primary mt-2" onClick={fetchFile}>
        📄 View Proof of Ownership
      </button>
      <Approve
        listing={listing}
        isEditable={canApprove}
        onSave={onSubmitApprovalHandler}
        onReject={onSubmitReject}
      />
      <PostItemForSale
        listing={listing}
        isEditable={canPostItemForSale}
        onPostItemForSale={onPostItemForSale}
      />
      <StartAuction
        listing={listing}
        isEditable={canStartAuction}
        onStartAuction={onStartAuction}
      />
      <AssignRestoration
        listing={listing}
        isEditable={canAssignRestoration}
        onAssignRestoration={onAssignRestoration}
      />
      {showEstimationDialog && (
        <ValueEstimation
          listing={listing}
          isEditable={canEditEstimation}
          saveEstimations={onSubmitEstimationsHandler}
        />
      )}
    </div>
  );
}

const mapStateToProps = ({ auth, projectListings }) => ({
  user: auth.user,
  projectListings,
});

module.exports = connect(mapStateToProps)(ProjectListingDetails);
