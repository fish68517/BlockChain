const React = require('react');
const { Link } = require('react-router-dom');

const ProjectListingInvestor = ({ Investment, onInvest }) => {
  const { id, make, model, ccpg, fundingGoal, vin } = Investment.listing;
  return (
    <div className="card mt-2">
      <div className="card-body">
        <div className="card-title">
            <h5>{make}{' '}{model}</h5>
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
          <div>
            <strong>Invested amount: </strong>
            {Investment.amount}
          </div>
        </div>
      </div>
    </div>
  )
}

module.exports = ProjectListingInvestor;
