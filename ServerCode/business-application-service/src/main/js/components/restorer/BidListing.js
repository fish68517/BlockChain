const React = require("react");
const { Link } = require("react-router-dom");

const BidListing = ({ bidListing }) => {
  const { bidderId, listingId, biddingPrice, category, isSelected } =
    bidListing;
  console.log("here", bidListing);
  return (
    <div className="card mt-2">
      <div className="card-body">
        <div className="card-title">
          <div>
            <Link to={`/listings/${listingId}`}>
              {" "}
              <strong>Product Listing Id: </strong> {listingId}{" "}
            </Link>
          </div>
          <div>
            <strong>Bidder Id: </strong> {bidderId}
          </div>
        </div>
        <div className="card-text">
          <div>
            <strong>Bid Price: </strong> {biddingPrice}
          </div>
          <div>
            <strong>Category: </strong> {category}
          </div>
          {isSelected && <div className="badge bg-success">Selected</div>}
        </div>
      </div>
    </div>
  );
};

module.exports = BidListing;
