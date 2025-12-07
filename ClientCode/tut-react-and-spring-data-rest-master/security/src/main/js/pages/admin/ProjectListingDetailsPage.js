const React = require('react');
const { useState } = require('react');
const { connect } = require('react-redux');
const { useParams } = require('react-router-dom');
const config = require('config');

const http = require('../../common/http-common');
const { addValueEstimation, submitReview, rejectListing, assignRestoration, postItemForSale, startAuction } = require('../../actions/ProjectListings');
const ValueEstimation = require('../../components/admin/ValueEstimation');
const Approve = require('../../components/admin/Approve');
const PostItemForSale = require('../../components/admin/PostItemForSale');
const StartAuction = require('../../components/admin/StartAuction');
const AssignRestoration = require('../../components/admin/AssignRestoration');
const ProjectListingStatus = require('../../components/common/ProjectListingStatus');
const MetaConnectModal = require('../../components/web3/MetaConnectModal');

const {
  approveCCProjectListing,
  saveEstCCProjectListing,
  assignRestorationCCProjectListing,
  openAuctionCCProjectListing
} = require('../../utils/web3Utils');

function ProjectListingDetails({ projectListings, dispatch }) {
  const { id } = useParams();
  const listing = projectListings.find(p => p.id === parseInt(id));

  const [showModal, setShowModal] = useState(false);

  const fetchFile = () => {
    http().get(`${config.uploadsUrl}${listing.file.path}`, {
      responseType: 'blob',
    })
    .then(({ data }) => {
      const href = URL.createObjectURL(data);
      const link = document.createElement('a');

      link.href = href;
      link.setAttribute('download', listing.file.fileName);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(href);
    });
  }

  const onSubmitEstimations = (valueEstimation, repairCost) => {
    return dispatch(addValueEstimation(id, { valueEstimation, repairCostEstimation: repairCost }));
  }

  const onSubmitEstimationsHandler = async (valueEstimation, repairCost) => {
    if(window.ethereum) {
      await saveEstCCProjectListing(listing.projectAddress, valueEstimation, repairCost);
    } else {
      setShowModal(true);
      throw "Unable to update estimations in smart contract";
    }
    return onSubmitEstimations(valueEstimation, repairCost);
  }

  const onAssignRestoration = async () => {
    if(window.ethereum) {
      await assignRestorationCCProjectListing(listing.projectAddress);
    } else {
      setShowModal(true);
      throw "Unable to assign restoration in smart contract";
    }
    return dispatch(assignRestoration(id));
  }

  const onPostItemForSale = () => {
    return dispatch(postItemForSale(id));
  }

  const onStartAuction = async () => {
    if(window.ethereum) {
      await openAuctionCCProjectListing(listing.projectAddress);
    } else {
      setShowModal(true);
      throw "Unable to start auction in smart contract";
    }
    return dispatch(startAuction(id));
  }

  const onSubmitApproval = (areDetailsVerified, isTitleReceived, message) => {
    return dispatch(submitReview(id,
      {
        verifyDetails: areDetailsVerified,
        receiveTitle: isTitleReceived,
        adminMessage: message,
      }
    ));
  }

  const onSubmitApprovalHandler = async (areDetailsVerified, isTitleReceived, message = null) => {
    if(window.ethereum) {
      await approveCCProjectListing(listing.projectAddress);
    } else {
      setShowModal(true);
      throw "Unable to update approval in smart contract";
    }
    return onSubmitApproval(areDetailsVerified, isTitleReceived, message);
  }

  const onSubmitReject = () => {
    return dispatch(rejectListing(id));
  }

  if (!listing) {
    return (
      <div>Could not find listing with id {id}.</div>
    );
  }

  const canEditEstimation = listing.pendingTasks.includes('Value Estimation');
  const canAssignRestoration = listing.pendingTasks.includes('Assign Restoration');
  const canPostItemForSale = listing.pendingTasks.includes('Post Item');
  const canStartAuction = listing.pendingTasks.includes('Auction');
  const showEstimationDialog = canEditEstimation || listing.valueEstimation;
  const canApprove = listing.pendingTasks.includes('Verify Details') || listing.pendingTasks.includes('Receive Title');

  return (
    <div className="container project-details-page">
      <MetaConnectModal setShowModal={setShowModal} showModal={showModal} />
      <h3>Project Details</h3>
      <div className="my-1">
        <ProjectListingStatus listing={listing} />
      </div>
      <div>{listing.make} {listing.model}</div>
      <div>CCPG: {listing.ccpg}</div>
      <div>VIN: {listing.vin}</div>
      <div>Initial funding requested: {listing.fundingGoal}</div>
      {listing.description &&
        <div>Description: {listing.description}</div>
      }
      <button className="btn btn-link p-0" onClick={fetchFile}>Proof of ownership</button>
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
      {showEstimationDialog &&
        <ValueEstimation
          listing={listing}
          isEditable={canEditEstimation}
          saveEstimations={onSubmitEstimationsHandler}
        />
      }
    </div>
  );
}

const mapStateToProps = ({ auth, projectListings }) => ({
  user: auth.user,
  projectListings,
})


module.exports = connect(mapStateToProps)(ProjectListingDetails);
