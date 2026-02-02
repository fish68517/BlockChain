const React = require("react");
const { Link } = require("react-router-dom");

const ProjectListing = ({ projectListing, onRemove }) => {
  const { id, make, model, ccpg, fundingGoal, vin } = projectListing;
  return (
    <div className="card">
      <div className="card-header">
        <Link to={`/listings/${id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
          <h5 className="mb-0">
            {make} {model}
          </h5>
        </Link>
      </div>
      <div className="card-body">
        <div className="text-small mb-3" style={{ color: '#718096' }}>VIN: {vin}</div>
        <div className="card-text mb-3">
          <div className="mb-2">
            <strong>CCPG: </strong>
            <span className="text-primary">{ccpg}</span>
          </div>
          <div className="mb-2">
            <strong>Funding Goal: </strong>
            <span className="text-success fw-bold">{fundingGoal}</span>
          </div>
        </div>
        <button className="btn btn-outline-danger btn-sm" onClick={() => onRemove(id)}>
          Remove
        </button>
      </div>
    </div>
  );
};

module.exports = ProjectListing;
