const React = require("react");
const { useState, useEffect, useRef } = require("react");
const { connect } = require("react-redux");

const { getAllProjectListings } = require("../actions/ProjectListings");
const ProjectListingListInvestor = require("./investor/ProjectListingListInvestor");

function InvestorPortal({ user, dispatch, listings }) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState();

  useEffect(() => {
    setIsLoading(true);

    if (user.accessToken) {
      dispatch(getAllProjectListings()) // TBD filter out listings that the user had already invested in
        .then((data) => {
          setIsLoading(false);
          console.log("received", data);
        })
        .catch((err) => {
          setIsLoading(false);
          console.log(err);
          setError(err.message);
        });
    }
  }, [user.accessToken]);

  const renderBody = () => {
    if (isLoading) return <div>Loading....</div>;
    if (error) return <div>{error}</div>;

    return (
      <ProjectListingListInvestor projectListings={listings} user={user} />
    );
  };

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
  };
}

module.exports = connect(mapStateToProps)(InvestorPortal);
