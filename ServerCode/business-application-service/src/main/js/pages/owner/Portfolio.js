const React = require("react");
const { useState, useEffect, useRef } = require("react");
const { connect } = require("react-redux");

const {
  getProjectListingsByUserID,
  deleteProjectById,
} = require("../../actions/ProjectListings");
const ProjectListingsList = require("../../components/common/ProjectListingsList");

function Portfolio({ user, dispatch, listings }) {
  const [pageSize, setPageSize] = useState(10);
  const [pageNumber, setPageNumber] = useState(0);
  const [maxPage, setMaxPage] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState();

  useEffect(() => {
    setIsLoading(true);

    if (user.accessToken) {
      dispatch(getProjectListingsByUserID(user.id, pageSize, pageNumber))
        .then((data) => {
          setIsLoading(false);
          console.log("recieved", data);
        })
        .catch((err) => {
          setIsLoading(false);
          console.log(err);
          setError(err.message);
        });
    }
  }, [user.accessToken]);

  const onRemove = (id) => {
    dispatch(deleteProjectById(id));
  };

  const renderBody = () => {
    if (isLoading) return <div>Loading....</div>;
    if (error) return <div>{error}</div>;

    return (
      <ProjectListingsList onRemove={onRemove} projectListings={listings} />
    );
  };

  return (
    <div className="container">
      <div className="page-title my-3">
        <h3>My Portfolio</h3>
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

module.exports = connect(mapStateToProps)(Portfolio);
