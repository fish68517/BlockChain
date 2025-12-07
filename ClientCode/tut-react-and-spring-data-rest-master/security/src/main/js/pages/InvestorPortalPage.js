const React = require('react');
const { useState, useEffect, useRef } = require('react');
const { connect } = require('react-redux');

const stompClient = require('../websocket-listener');

const { getAllProjectListings } = require('../actions/ProjectListings');
const ProjectListingListInvestor = require('./investor/ProjectListingListInvestor');


const root = '/api';

function InvestorPortal({ user, dispatch, listings }) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState();
  const portfolioSock = useRef(null);

	useEffect(() => {
    setIsLoading(true);

    if (user.accessToken) {
      dispatch(getAllProjectListings()) // TBD filter out listings that the user had already invested in
      .then(data => {
        setIsLoading(false);
        console.log('received', data);
      })
      .catch(err => {
        setIsLoading(false);
        console.log(err);
        setError(err.message);
      })
    }

    // portfolioSock.current = stompClient.register([
		// 	{ route: '/topic/projectListing/reject', callback: socketCallback }
		// ]);
	}, [user.accessToken]);

  // const socketCallback = (message) => {
  //   console.log("SOCKET");
  //   console.log(message)
  // }

  const renderBody = () => {
    if (isLoading) return <div>Loading....</div>
    if (error) return <div>{error}</div>

    return (
      <ProjectListingListInvestor
        projectListings={listings}
        user = {user}
      />
    )
  }

  return (
    <div className="container">
      <div className="page-title my-3">
        <h3>All listings:</h3>
      </div>
      {renderBody()}
    </div>
  );
}

function mapStateToProps({ auth, projectListings }) {
  return {
    user: auth.user,
    listings: projectListings,
  }
}

module.exports = connect(mapStateToProps)(InvestorPortal);
