const React = require('react');
const ReactDOM = require('react-dom');
const { connect } = require('react-redux');
const { CardGroup } = require('react-bootstrap');

const BidListing = require('./BidListing');

const BidListingList = ({ bidListings }) => {

  return (
    <div>
      <CardGroup>
        {bidListings.map(bidListing =>
          <BidListing
            key={bidListing.id}
            bidListing={bidListing}
          />
        )}
      </CardGroup>
    </div>
  )
}

module.exports = BidListingList;
