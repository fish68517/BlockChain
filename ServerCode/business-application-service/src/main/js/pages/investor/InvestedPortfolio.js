const React = require("react");
const { useState, useEffect, useRef } = require("react");
const { connect } = require("react-redux");

const { getInvestmentsByUserID } = require("./../../actions/Investments");
const InvestedProjectListingList = require("./InvestedProjectListingList");

const root = "/api";

function InvestedPortfolio({ user, dispatch, listings, investments }) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState();
  const portfolioSock = useRef(null);

  const [pageSize, setPageSize] = useState(10);
  const [pageNumber, setPageNumber] = useState(0);

  const [invested, setInvested] = useState([]);

  useEffect(() => {
    setIsLoading(true);

    if (user.accessToken) {
      dispatch(getInvestmentsByUserID(user.id, pageSize, pageNumber))
        .then((data) => {
          setIsLoading(false);
          setInvested(data);
          console.log("received", data);
          console.log("investedlistings", invested.listing);
        })
        .catch((err) => {
          setIsLoading(false);
          console.log(err);
          setError(err.message);
        });
    }

    // portfolioSock.current = stompClient.register([
    // 	{ route: '/topic/projectListing/reject', callback: socketCallback }
    // ]);
  }, [user.accessToken]);

  const socketCallback = (message) => {
    console.log("SOCKET");
    console.log(message);
  };

  // TODO: Make invest api call here
  const onInvest = (id) => {
    //dispatch(investProject(userid,projectid));
  };

  const renderBody = () => {
    if (isLoading) return <div>Loading....</div>;
    if (error) return <div>{error}</div>;

    return (
      <>
        <InvestedProjectListingList
          onInvest={onInvest}
          Investments={investments}
        />
      </>
    );
  };

  return (
    <div className="container">
      <div className="page-title my-3">
        <h3>My investments:</h3>
      </div>
      {renderBody()}
    </div>
  );
}

function mapStateToProps({ auth, projectListings, investments }) {
  return {
    user: auth.user,
    listings: projectListings,
    investments: investments,
  };
}

module.exports = connect(mapStateToProps)(InvestedPortfolio);
