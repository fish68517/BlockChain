const React = require('react');
const { useState, useEffect } = require('react');
const { connect } = require('react-redux');
const { useParams } = require('react-router-dom');
const config = require('config');

const http = require('../../common/http-common');
const { finishRestoration } = require('../../actions/ProjectListings');
const FinishRestoration = require('../../components/restorer/FinishRestoration');
const ProjectListingStatus = require('../../components/common/ProjectListingStatus');

function ProjectListingDetails({projectListings, dispatch}) {
  const { id } = useParams();
  const [listing, setListing] = useState();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    http().get(`projectListings/${id}`)
    .then(({ data }) => {
      setListing(data);
      setIsLoading(false);
    })
  }, []);

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

  if (isLoading) {
    return (
      <div className="container mt-2">Loading...</div>
    );
  }

  if (!listing && !isLoading) {
    return (
      <div className="container mt-2">Could not find listing with id {id}.</div>
    );
  }

  const onFinishRestoration = () => {
    return dispatch(finishRestoration(id));
  }

  const canFinishRestoration = listing.pendingTasks.includes('Restoration Finished');

  return (
    <div className="container project-details-page mt-2">
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
      <FinishRestoration
        listing={listing}
        isEditable={canFinishRestoration}
        onFinishRestoration={onFinishRestoration}
      />
    </div>
  );
}

const mapStateToProps = ({ auth, projectListings }) => ({
  user: auth.user,
  projectListings,
})


module.exports = connect(mapStateToProps)(ProjectListingDetails);
