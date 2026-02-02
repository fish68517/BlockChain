const React = require('react');
const { connect } = require('react-redux');

const { Route, Routes, Navigate } = require('react-router-dom');

//const RestorerPortfolio = require('./RestorerPortfolio');
const AuctionPage = require('./AuctionPage');

class BuyerPortal extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            loggedInUser: this.props.loggedInUser, loggedInUserRole: this.props.loggedInUserRole,
            projectListings: [], attributes: [], links: {},
            content: "BUYER CONTENT"
        };
    }

  render() {
    if (!this.props.user) {
      return <Navigate to="/login" />;
    }

    return (
      <Routes>
        <Route exact path="/" element={<Navigate to="/auction" />} />
        {/* <Route exact path="/restorerportfolio" element={<RestorerPortfolio />} /> */}
        <Route exact path="/auction" element={<AuctionPage />} />
      </Routes>
    );
  }
}

function mapStateToProps(state) {
  const { user } = state.auth;
  return {
    user,
  };
}

module.exports = connect(mapStateToProps)(BuyerPortal);