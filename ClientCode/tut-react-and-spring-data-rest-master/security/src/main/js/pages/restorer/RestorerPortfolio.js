const React = require('react');
const { useState, useEffect, useRef } = require('react');
const { connect } = require('react-redux');
const stompClient = require('../../websocket-listener');

const { getBidListings } = require('../../actions/BidListings');
const BidListingsList = require('../../components/restorer/BidListingsList');
const { getAllProjectListings } = require('../../services/ProjectListingsService');

function RestorerPortfolio({ user, dispatch, bidListings }) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState();

	useEffect(() => {
    setIsLoading(true);

    if (user.accessToken) {
      dispatch(getBidListings(user.id))
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

  const renderBody = () => {
    if (isLoading) return <div>Loading....</div>
    if (error) return <div>{error}</div>

    return (
      <BidListingsList
        bidListings={bidListings}
      />
    )
  }

  return (
    <div className="container">
      <div className="page-title my-3">
        <h3>My Portfolio</h3>
      </div>
      {renderBody()}
    </div>
  );
}

function mapStateToProps({ auth, bidListings }) {
  return {
    user: auth.user,
    bidListings: bidListings,
  }
}

module.exports = connect(mapStateToProps)(RestorerPortfolio);
