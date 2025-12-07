const React = require("react");
const { useNavigate, Link } = require("react-router-dom");

const OwnerNav = require("./OwnerNav");
const RestorerNav = require("./RestorerNav");
const AdminNav = require("./AdminNav");
const BuyerNav = require("./BuyerNav");
const InvestorNav = require("./InvestorNav");

const { logout } = require("../../actions/Auth");
const { connect } = require("react-redux");

const MetaConnect = require("../web3/MetaConnect");

function Header({ dispatch, user }) {
  const navigate = useNavigate();

  const onLogout = () => {
    dispatch(logout()).then(() => navigate("/login"));
  };

  const renderRoleBasedNav = () => {
    if (!user) return null;

    const { roles } = user;
    if (roles.includes("ROLE_OWNER")) {
      return <OwnerNav />;
    }

    if (roles.includes("ROLE_ADMIN")) {
      return <AdminNav />;
    }

    if (roles.includes("ROLE_INVESTOR")) {
      return <InvestorNav />;
    }

    if (roles.includes("ROLE_RESTORER")) {
      return <RestorerNav />;
    }

    if (roles.includes("ROLE_BUYER")) {
      return <BuyerNav />;
    }
  };

  return (
    <header className="py-3 text-bg-dark">
      <nav className="navbar navbar-expand-md navbar-dark container">
        <Link className="navbar-brand" to="/">
          CollectorCoin
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-toggle="collapse"
          data-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div
          className="collapse navbar-collapse justify-content-between"
          id="navbarSupportedContent"
        >
          {renderRoleBasedNav()}
          <>
            {user && <MetaConnect />}
            {user && (
              <button className="btn btn-outline-light" onClick={onLogout}>
                Log out
              </button>
            )}
          </>
        </div>
      </nav>
    </header>
  );
}

function mapStateToProps(state) {
  const { user } = state.auth;

  return {
    user,
  };
}

module.exports = connect(mapStateToProps)(Header);
