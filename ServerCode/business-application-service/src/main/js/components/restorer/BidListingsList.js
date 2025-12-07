const React = require("react");
const { CardGroup } = require("react-bootstrap");

const BidListing = require("./BidListing");

const BidListingsList = ({ bidListings }) => {
  return (
    <div>
      <CardGroup>
        {bidListings.map((bidListing) => (
          <BidListing key={bidListing.id} bidListing={bidListing} />
        ))}
      </CardGroup>
    </div>
  );
};

module.exports = BidListingsList;
