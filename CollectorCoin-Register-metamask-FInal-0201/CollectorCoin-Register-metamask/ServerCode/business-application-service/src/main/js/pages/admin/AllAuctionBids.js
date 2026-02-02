const React = require("react");
const { connect } = require("react-redux");
const { Button } = require("react-bootstrap");

const Modal = require("../../components/common/Modal");

const {
  getAllAuctionBids,
  selectAuctionBid,
} = require("../../actions/AuctionBid");

class AllAuctionBids extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      content: "Review submitted auction bids",
      showSelectedBids: false,
      error: null,
    };

    this.toggleBids = this.toggleBids.bind(this);
    this.handleSelectBtn = this.handleSelectBtn.bind(this);
  }

  loadFromServer() {
    this.props.dispatch(getAllAuctionBids());
  }

  // This is used to handle the two buttons "All Bids" and "Selected Bids"
  toggleBids(showSelectedBids) {
    this.setState({ showSelectedBids });
  }

  // change the status of bid listing once it gets selected
  handleSelectBtn(id, buyerAddress, projectAddress) {
    this.props.dispatch(selectAuctionBid(id))
      .then(() => {
        this.setState({ error: null });
      })
      .catch((error) => {
        console.error("Error setting buyer and redistributing:", error);
        this.setState({ error: error.message || "Unable to set the selected buyer" });
      });
  }

  componentDidMount() {
    this.loadFromServer();
  }

  render() {
    const auctionBidListingUI = this.state.showSelectedBids ? (
      <SelectedAuctionBidListings
        auctionBidListings={this.props.auctionBidListings.filter(
          (bid) => bid.isSelected
        )}
        projectListings={this.props.projectListings}
      />
    ) : (
      <AuctionBidListingList
        auctionBidListings={this.props.auctionBidListings}
        projectListings={this.props.projectListings}
        handleSelectBtn={this.handleSelectBtn}
      />
    );

    return (
      <div className="container">
        <div className="header">
          <h3>{this.state.content}</h3>
        </div>
        {this.state.error && (
          <div className="alert alert-danger" role="alert">
            {this.state.error}
          </div>
        )}
        <div className="mb-2">
          <Button variant="primary" onClick={() => this.toggleBids(false)}>
            All Auction Bids
          </Button>{" "}
          <Button variant="success" onClick={() => this.toggleBids(true)}>
            Selected Auction Bids
          </Button>
        </div>
        {auctionBidListingUI}
      </div>
    );
  }
}

function AuctionBidListingList(props) {
  const projectListingToAuctionBidsMapping = {};

  props.projectListings.forEach((listing) => {
    projectListingToAuctionBidsMapping[listing.id] = {
      listing,
      bids: props.auctionBidListings.filter(
        (bid) => bid.listingId === listing.id
      ),
    };
  });

  const renderHeader = () => (
    <thead>
      <tr>
        <th>Buyer ID</th>
        <th>Bid Amount</th>
        <th>Select Buyer Bid</th>
      </tr>
    </thead>
  );

  const listingSections = Object.keys(projectListingToAuctionBidsMapping).map(
    (key) => {
      const { bids, listing } = projectListingToAuctionBidsMapping[key];
      if (bids.length === 0) return null;
      const hasSelectedBid = bids.some((el) => el.isSelected);

      return (
        <div className="mt-2" key={key}>
          <h4>
            Bids for {listing.make} {listing.model}({listing.vin})
          </h4>
          <div>Car Value Estimate: {listing.valueEstimation}</div>
          <div>
            <table>
              {renderHeader()}
              <tbody>
                {bids.map((bid) => (
                  <AuctionBidListing
                    key={bid.id}
                    bid={bid}
                    projectListing={listing}
                    hasSelectedBid={hasSelectedBid}
                    handleSelectBtn={props.handleSelectBtn}
                  />
                ))}
              </tbody>
            </table>
          </div>
        </div>
      );
    }
  );

  return <div className="container">{listingSections}</div>;
}

function AuctionBidListing({
  bid,
  handleSelectBtn,
  projectListing,
  hasSelectedBid,
}) {
  const modalBody = (
    <div>
      Selecting a auction bid for {projectListing.make} {projectListing.model}.
      Are you sure you want to approve a bid of {bid.buyerPrice} from buyer{" "}
      {bid.buyerId}?
    </div>
  );

  return (
    <tr>
      <td>{bid.buyerId}</td>
      <td>{bid.buyerPrice}</td>
      <td>
        {bid.isSelected ? (
          <div className="badge bg-success">Selected</div>
        ) : !hasSelectedBid ? (
          <Modal
            modalHeading="Confirm Bid Selection"
            modalBody={modalBody}
            btnText="Select"
            btnClassname="btn btn-primary"
            onSubmit={() =>
              handleSelectBtn(
                bid.id,
                bid.buyerAddress,
                projectListing.projectAddress
              )
            }
          />
        ) : null}
      </td>
    </tr>
  );
}

const SelectedAuctionBidListings = ({
  auctionBidListings,
  projectListings,
}) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Listing ID</th>
          <th>Make</th>
          <th>Model</th>
          <th>VIN</th>
          <th>Value Estimation</th>
          <th>Buy Bid Amount</th>
          <th>Buyer ID</th>
        </tr>
      </thead>
      <tbody>
        {auctionBidListings.map((bid) => {
          const projectListing = projectListings.find(
            (p) => p.id === bid.listingId
          );
          return (
            <tr key={bid.id}>
              <td>{projectListing.id}</td>
              <td>{projectListing.make}</td>
              <td>{projectListing.model}</td>
              <td>{projectListing.vin}</td>
              <td>{projectListing.valueEstimation}</td>
              <td>{bid.buyerPrice}</td>
              <td>{bid.buyerId}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};

const mapStateToProps = (state) => ({
  user: state.auth.user,
  auctionBidListings: state.auctionBidListings,
  projectListings: state.projectListings,
});

module.exports = connect(mapStateToProps)(AllAuctionBids);
