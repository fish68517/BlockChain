const React = require("react");
const { connect } = require("react-redux");
const { Button } = require("react-bootstrap");
const MetaConnectModal = require("../../components/web3/MetaConnectModal");
const { setRestorerCCProjectListing } = require("../../utils/web3Utils");

const Modal = require("../../components/common/Modal");
const { getAllBids, selectBid } = require("../../actions/BidListings");

class AllBids extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      content: "Review submitted restoration bids",
      showSelectedBids: false,
      showModal: false,
    };

    this.toggleBids = this.toggleBids.bind(this);
    this.handleSelectBtn = this.handleSelectBtn.bind(this);
    this.setShowModal = this.setShowModal.bind(this);
  }

  loadFromServer() {
    this.props.dispatch(getAllBids());
  }

  // This is used to handle the two buttons "All Bids" and "Selected Bids"
  toggleBids(showSelectedBids) {
    this.setState({ showSelectedBids });
  }

  // change the status of bid listing once it gets selected
  async handleSelectBtn(id, biddingPrice, restorerAddress, projectAddress) {
    if (window.ethereum) {
      console.log(biddingPrice, restorerAddress, projectAddress);
      await setRestorerCCProjectListing(
        projectAddress,
        restorerAddress,
        biddingPrice
      );
    } else {
      this.setShowModal(true);
      throw "Unable to set the selected restorer";
    }
    this.props.dispatch(selectBid(id));
  }

  componentDidMount() {
    this.loadFromServer();
  }

  setShowModal(show) {
    this.setState({
      showModal: show,
    });
  }

  render() {
    const bidListingUI = this.state.showSelectedBids ? (
      <SelectedBidListings
        bidListings={this.props.bidListings.filter((bid) => bid.isSelected)}
        projectListings={this.props.projectListings}
      />
    ) : (
      <BidListingList
        bidListings={this.props.bidListings}
        projectListings={this.props.projectListings}
        handleSelectBtn={this.handleSelectBtn}
      />
    );

    return (
      <div className="container">
        <MetaConnectModal
          showModal={this.state.showModal}
          setShowModal={this.setShowModal}
        />
        <div className="header">
          <h3>{this.state.content}</h3>
        </div>
        <div className="mb-2">
          <Button variant="primary" onClick={() => this.toggleBids(false)}>
            All Bids
          </Button>{" "}
          <Button variant="success" onClick={() => this.toggleBids(true)}>
            Selected Bids
          </Button>
        </div>
        {bidListingUI}
      </div>
    );
  }
}

function BidListingList(props) {
  const projectListingToBidsMapping = {};

  props.projectListings.forEach((listing) => {
    projectListingToBidsMapping[listing.id] = {
      listing,
      bids: props.bidListings.filter((bid) => bid.listingId === listing.id),
    };
  });

  const renderHeader = () => (
    <thead>
      <tr>
        <th>Bidder ID</th>
        <th>Bid Amount</th>
        <th>Select Restoration Bid</th>
      </tr>
    </thead>
  );

  const listingSections = Object.keys(projectListingToBidsMapping).map(
    (key) => {
      const { bids, listing } = projectListingToBidsMapping[key];
      if (bids.length === 0) return null;
      const hasSelectedBid = bids.some((el) => el.isSelected);

      return (
        <div className="mt-2" key={key}>
          <h4>
            Bids for {listing.make} {listing.model}({listing.vin})
          </h4>
          <div>Repair Cost Estimate: {listing.repairCostEstimation}</div>
          <div>
            <table>
              {renderHeader()}
              <tbody>
                {bids.map((bid) => (
                  <BidListing
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

function BidListing({ bid, handleSelectBtn, projectListing, hasSelectedBid }) {
  const modalBody = (
    <div>
      Selecting a bid for {projectListing.make} {projectListing.model}. The bid
      price is going to be set as the funding goal for investors. Are you sure
      you want to approve a bid of {bid.biddingPrice} from restorer{" "}
      {bid.bidderId}?
    </div>
  );

  return (
    <tr>
      <td>{bid.bidderId}</td>
      <td>{bid.biddingPrice}</td>
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
                bid.biddingPrice,
                bid.restorerAddress,
                projectListing.projectAddress
              )
            }
          />
        ) : null}
      </td>
    </tr>
  );
}

const SelectedBidListings = ({ bidListings, projectListings }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Listing ID</th>
          <th>Make</th>
          <th>Model</th>
          <th>VIN</th>
          <th>Repair Cost Estimation</th>
          <th>Restoration Bid Amount</th>
          <th>Restorer ID</th>
        </tr>
      </thead>
      <tbody>
        {bidListings.map((bid) => {
          const projectListing = projectListings.find(
            (p) => p.id === bid.listingId
          );
          return (
            <tr key={bid.id}>
              <td>{projectListing.id}</td>
              <td>{projectListing.make}</td>
              <td>{projectListing.model}</td>
              <td>{projectListing.vin}</td>
              <td>{projectListing.repairCostEstimation}</td>
              <td>{bid.biddingPrice}</td>
              <td>{bid.bidderId}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};

const mapStateToProps = (state) => ({
  user: state.auth.user,
  bidListings: state.bidListings,
  projectListings: state.projectListings,
});

module.exports = connect(mapStateToProps)(AllBids);
