const React = require('react');
const ReactDOM = require('react-dom');
const { connect } = require('react-redux');
const { CardGroup } = require('react-bootstrap');


const ProjectListingInvestor = require('./ProjectListingInvestor');


const ProjectListingListInvestor = ({ onInvest, projectListings, user }) => {
  const approvedProjectListings = projectListings.filter(
		(projectListing) => (projectListing["isBidSuccess"] && !projectListing["isFundingEnough"])
	);
  console.log('list', approvedProjectListings);
  return (
    <div>
      <CardGroup>
        {approvedProjectListings.map(projectListing =>
          <ProjectListingInvestor
            key={projectListing.id}
            projectListing={projectListing}
            user = {user}
          />
        )}
      </CardGroup>
    </div>
  )
}

module.exports = ProjectListingListInvestor
