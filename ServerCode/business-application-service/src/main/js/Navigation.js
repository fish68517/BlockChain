const stompClient = require("./websocket-listener");
const { useEffect, useRef } = require("react");
const { Routes, Route, Navigate } = require("react-router-dom");
const React = require("react");

const Register = require("./forms/RegisterComponent");
const Login = require("./forms/LoginComponent");
const Logout = require("./forms/LogoutComponent");

const Portfolio = require("./pages/owner/Portfolio");
const NewProjectListing = require("./pages/owner/NewProjectListing");
const ProjectListingDetailOwner = require("./pages/owner/ProjectListingDetailsPage");

const AdminPortal = require("./pages/admin/AdminPortalPage");
const AllBids = require("./pages/admin/AllBids");
const AllAuctionBids = require("./pages/admin/AllAuctionBids");
const ProjectListingDetailAdmin = require("./pages/admin/ProjectListingDetailsPage");

const RestorerPortfolio = require("./pages/restorer/RestorerPortfolio");
const NewBid = require("./pages/restorer/NewBid");
const ProjectListingDetailRestorer = require("./pages/restorer/ProjectListingDetailsPage");

const AuctionPage = require("./pages/buyer/AuctionPage");

const InvestorPortal = require("./pages/InvestorPortalPage");
const InvestedPortfolio = require("./pages/investor/InvestedPortfolio");

const { connect } = require("react-redux");
function Navigation(props) {
  const { user, dispatch } = props;
  const isOwner = props.user?.roles?.includes("ROLE_OWNER");
  const isAdmin = props.user?.roles?.includes("ROLE_ADMIN");
  const isRestorer = props.user?.roles?.includes("ROLE_RESTORER");
  const isBuyer = props.user?.roles?.includes("ROLE_BUYER");

  const portfolioSock = useRef(null);

  useEffect(() => {
    if (isOwner) {
      portfolioSock.current = stompClient.register([
        {
          route: "/topic/projectListing/reject",
          callback: updateProjectCallback,
        },
        {
          route: `/topic/${user.id}/updateProjectListing`,
          callback: updateProjectCallback,
        },
      ]);
    }
    if (isAdmin) {
      console.log("isAdmin");
      portfolioSock.current = stompClient.register([
        { route: "/topic/newProjectListing", callback: newListingCallback },
        {
          route: "/topic/updateProjectListing",
          callback: updateProjectCallback,
        },
        { route: "/topic/biddingCreated", callback: biddingCreatedCallback },
        { route: "/topic/biddingUpdated", callback: biddingUpdatedCallback },
        {
          route: "/topic/auctionBiddingCreated",
          callback: auctionBiddingCreatedCallback,
        },
        {
          route: "/topic/auctionBiddingUpdated",
          callback: auctionBiddingUpdatedCallback,
        },
      ]);
    }

    if (isRestorer) {
      portfolioSock.current = stompClient.register([
        {
          route: `/topic/${user.id}/bidApproved`,
          callback: biddingUpdatedCallback,
        },
        {
          route: "/topic/listingAvailableForBid",
          callback: newListingCallback,
        },
      ]);
    }

    if (isBuyer) {
      portfolioSock.current = stompClient.register([
        {
          route: `/topic/${user.id}/auctionBidApproved`,
          callback: auctionBiddingUpdatedCallback,
        },
        {
          route: "/topic/listingAvailableForAuctionBid",
          callback: newListingCallback,
        },
      ]);
    }

    return () => {
      if (portfolioSock.current) {
        portfolioSock.current.disconnect();
        portfolioSock.current = null;
      }
    };
  }, [isOwner, isAdmin, isRestorer]);

  const updateProjectCallback = ({ body }) => {
    const data = JSON.parse(body);
    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: data,
    });
  };

  const biddingCreatedCallback = ({ body }) => {
    const data = JSON.parse(body);
    dispatch({
      type: CREATE_BID_LISTING_BY_BIDDERID,
      payload: data,
    });
  };

  const biddingUpdatedCallback = ({ body }) => {
    const data = JSON.parse(body);
    dispatch({
      type: UPDATE_BID_LISTING,
      payload: data,
    });
  };

  const auctionBiddingCreatedCallback = ({ body }) => {
    const data = JSON.parse(body);
    dispatch({
      type: CREATE_AUCTION_BID_LISTING_BY_BUYERID,
      payload: data,
    });
  };

  const auctionBiddingUpdatedCallback = ({ body }) => {
    const data = JSON.parse(body);
    dispatch({
      type: UPDATE_AUCTION_BID_LISTING,
      payload: data,
    });
  };

  function newListingCallback({ body }) {
    const data = JSON.parse(body);
    dispatch(addNewProjectListing(data));
  }
  return (
    <Routes>
      <Route path="login" element={<Login />} />
      <Route exact path="/logout" element={<Logout />} />
      <Route path="register" element={<Register />} />

      {!props.user && (
        <Route exact path="/" element={<Navigate to="/login" />} />
      )}

      {isOwner && (
        <>
          <Route path="/" element={<Navigate to="/myportfolio" />} />
          <Route path="/myportfolio" element={<Portfolio />} />
          <Route exact path="/newlisting" element={<NewProjectListing />} />
          <Route exact path="/newlisting" element={<NewProjectListing />} />
          <Route
            exact
            path="/listings/:id"
            element={<ProjectListingDetailOwner />}
          />
        </>
      )}

      {props.user?.roles?.includes("ROLE_ADMIN") && (
        <>
          <Route exact path="/" element={<Navigate to="/admin" />} />
          <Route exact path="/admin" element={<AdminPortal />} />
          <Route exact path="/selectfrombids" element={<AllBids />} />
          <Route
            exact
            path="/selectfromauctionbids"
            element={<AllAuctionBids />}
          />
          <Route
            exact
            path="/listings/:id"
            element={<ProjectListingDetailAdmin />}
          />
        </>
      )}

      {props.user?.roles?.includes("ROLE_RESTORER") && (
        <>
          <Route path="/" element={<Navigate to="/restorerportfolio" />} />
          <Route
            exact
            path="/restorerportfolio"
            element={<RestorerPortfolio />}
          />
          <Route exact path="/newbid" element={<NewBid />} />
          <Route
            exact
            path="/listings/:id"
            element={<ProjectListingDetailRestorer />}
          />
        </>
      )}
      {props.user?.roles?.includes("ROLE_BUYER") && (
        <>
          <Route exact path="/" element={<Navigate to="/auction" />} />
          <Route exact path="/auction" element={<AuctionPage />} />
        </>
      )}
      {props.user?.roles?.includes("ROLE_INVESTOR") && (
        <>
          <Route exact path="/" element={<Navigate to="/invest" />} />
          <Route exact path="/invest" element={<InvestorPortal />} />
          <Route
            exact
            path="/investedPortfolio"
            element={<InvestedPortfolio />}
          />
        </>
      )}
    </Routes>
  );
}

function mapStateToProps(state) {
  const { user } = state.auth;
  return {
    user,
  };
}

module.exports = connect(mapStateToProps)(Navigation);
