const React = require('react');
const ReactDOM = require('react-dom');
const { connect } = require('react-redux');
const { CardGroup } = require('react-bootstrap');

const ProjectListing = require('./ProjectListing');

const ProjectListingList = ({ onRemove, projectListings }) => {
  console.log('list', projectListings);
  return (
    <div>
      <CardGroup>
        {projectListings.map(projectListing =>
          <ProjectListing
            key={projectListing.id}
            onRemove={onRemove}
            projectListing={projectListing}
          />
        )}
      </CardGroup>
    </div>
  )
}

module.exports = ProjectListingList;
