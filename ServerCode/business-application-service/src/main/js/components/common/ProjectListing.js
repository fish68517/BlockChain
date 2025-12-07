const React = require("react");
const { Link } = require("react-router-dom");

const ProjectListing = ({ projectListing, onRemove }) => {
  const { id, make, model, ccpg, fundingGoal, vin } = projectListing;
  return (
    <div className="card mt-2">
      <div className="card-body">
        <div className="card-title">
          <Link to={`/listings/${id}`}>
            <h5>
              {make} {model}
            </h5>
          </Link>
          <div className="text-small">VIN: {vin}</div>
        </div>
        <div className="card-text">
          <div>
            <strong>CCPG: </strong>
            {ccpg}
          </div>
          <div>
            <strong>Funding Goal: </strong>
            {fundingGoal}
          </div>
        </div>
        <button className="btn btn-link p-0" onClick={() => onRemove(id)}>
          Remove
        </button>
      </div>
    </div>
  );
};

module.exports = ProjectListing;
