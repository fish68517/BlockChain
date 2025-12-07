const React = require("react");
const { connect } = require("react-redux");
const { useParams } = require("react-router-dom");
const config = require("config");

const http = require("../../common/http-common");
const EditableProjectListing = require("./EditableProjectListing");
const ProjectListingStatus = require("../../components/common/ProjectListingStatus");

function ProjectListingDetails({ projectListings }) {
  const { id } = useParams();
  const listing = projectListings.find((p) => p.id === parseInt(id));
  const isEditable = listing.pendingTasks.includes("Edit Details");

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

  if (!listing) {
    return <div>Could not find listing with id {id}.</div>;
  }

  return (
    <div className="container project-details-page">
      <h3>Project Details</h3>
      <div className="my-1">
        <ProjectListingStatus listing={listing} />
      </div>
      {isEditable ? (
        <EditableProjectListing listing={listing} onFileClick={fetchFile} />
      ) : (
        <>
          <div>
            {listing.make} {listing.model}
          </div>
          <div>CCPG: {listing.ccpg}</div>
          <div>VIN: {listing.vin}</div>
          <div>Initial funding requested: {listing.fundingGoal}</div>
          <button className="btn btn-link p-0" onClick={fetchFile}>
            Proof of ownership
          </button>
          {listing.valueEstimation && (
            <div>
              Value Estimation: {listing.valueEstimation} and Repair Cost
              Estimation: {listing.repairCostEstimation}
            </div>
          )}
        </>
      )}
    </div>
  );
}

const mapStateToProps = ({ auth, projectListings }) => ({
  user: auth.user,
  projectListings,
});

module.exports = connect(mapStateToProps)(ProjectListingDetails);
