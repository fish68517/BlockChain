const React = require('react');
const { useState, useEffect } = require('react');
const { connect } = require('react-redux');
const { Link } = require('react-router-dom');
const { Button } = require('react-bootstrap');
const { getWalletAddress } = require('../../utils/web3Utils');
const MetaConnectModal = require('../../components/web3/MetaConnectModal');

const {
  getListingsPendingBid,
  createBidListingByBidderID,
  getBidListings,
  updateBidById,
} = require('../../actions/BidListings');

function NewBid({ user, dispatch, listings, bids }) {
  const [isLoading, setIsLoading] = useState(false);
  const [status, setStatus] = useState();
  const [error, setError] = useState();
  const [showModal, setShowModal] = useState(false);

	useEffect(() => {
    setIsLoading(true);
    const getListings = () => dispatch(getListingsPendingBid());
    const getBids = () => dispatch(getBidListings(user.id));

    if (user.accessToken) {
      Promise.all([getListings(), getBids()])
      .then(data => {
        setIsLoading(false);
        console.log('recieved', data);
      })
      .catch(err => {
        setIsLoading(false);
        console.log(err);
        setError(err.message);
      })
    }
  }, [user.accessToken]);
  

  useEffect(() => {
    let timeout;
    if (status !== '') {
      timeout = setTimeout(() => setStatus(''), 3000);
    }

    return () => clearTimeout(timeout);
  }, [status]);

  const updateBid = (bidId, listingId, bidPrice) => {
    if (bidPrice <= 0) // invalid bid
      setError("Bid value must be greater than 0");
    else {
      setError('');
      dispatch(updateBidById(bidId, { biddingPrice: bidPrice }))
      .then(() => {
        setError('');
        setStatus(`Successfully created a bid for listing ${listingId}.`)
      }).catch(e => {
        console.log(e);
        setError(`Error creating a bid for listing ${listingId}`);
      });
    }
  }

  const createBid = async (listingId, bidPrice) => {
    if (bidPrice <= 0) // invalid bid
      setError("Bid value must be greater than 0");
    else {
      setError('');
      if(window.ethereum) {
        const walletAddress = await getWalletAddress();
        dispatch(createBidListingByBidderID(user.id, { listingId, biddingPrice: bidPrice, restorerAddress: walletAddress }))
        .then(() => {
          setError('');
          setStatus(`Successfully created a bid for listing ${listingId}.`)
        }).catch(e => {
          console.log(e);
          setError(`Error creating a bid for listing ${listingId}`);
        });
      } else {
        setShowModal(true);
        throw "Unable to retrieve restorer wallet address";
      }
    }
  }

  return (
    <div className="container">
      <MetaConnectModal setShowModal={setShowModal} showModal={showModal} />
      <div className="page-title my-3">
        <h3>My Portfolio</h3>
      </div>
      {status && <div className="d-inline-block mt-2 p-2 alert alert-success">{status}</div>}
      {error && <div className="d-inline-block mt-2 p-2 alert alert-danger">{error}</div>}
        <div>
            <table>
                <thead>
                    <tr>
                        <th>New Listing ID</th>
                        <th>CCPG</th>
                        <th>VIN Number</th>
                        <th>Model</th>
                        <th>Make</th>
                        <th>Repair Cost Estimate</th>
                    </tr>
                </thead>
                <tbody>
                  {listings.map(projectListing => {
                    const existingBid = bids.find(b => b.listingId === projectListing.id);
                    return (
                      <NewBidRow
                        key={projectListing.id}
                        projectListing={projectListing}
                        existingBid={existingBid}
                        onCreate={createBid}
                        onUpdate={updateBid}
                      />
                    );
                  })}
                </tbody>
            </table>
        </div>
    </div>
  );
}

const NewBidRow = ({ projectListing, existingBid, onCreate, onUpdate }) => {
  const [bidPrice, setBidPrice] = useState(existingBid ? existingBid.biddingPrice : 0);

  return (
    <tr key={projectListing.id}>
      <td><Link to={`/listings/${projectListing.id}`}>{projectListing.id}</Link></td>
      <td>{projectListing.ccpg}</td>
      <td>{projectListing.vin}</td>
      <td>{projectListing.model}</td>
      <td>{projectListing.make}</td>
      <td>{projectListing.repairCostEstimation}</td>
      <td>
        <input
          type="number"
          min="1"
          id="bidPrice"
          defaultValue={bidPrice}
          onChange={e => setBidPrice(e.target.value)}
        />
        {existingBid ?
          <Button variant="info" onClick={() => onUpdate(existingBid.id, projectListing.id, bidPrice)}>
            Re-bid
          </Button> :
          <Button type="button" id="bidbtn" onClick={() => onCreate(projectListing.id, bidPrice)}>
            Bid
          </Button>
        }
      </td>
    </tr>
  );
}

function mapStateToProps({ auth, bidListings, projectListings }) {
  return {
    user: auth.user,
    listings: projectListings,
    bids: bidListings,
  }
}

module.exports = connect(mapStateToProps)(NewBid);
