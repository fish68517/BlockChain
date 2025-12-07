const React = require("react");
const { Nav } = require("react-bootstrap");
const { NavLink } = require("react-router-dom");

const BuyerNav = () => {
  return (
    <Nav className="navbar-nav" as="ul">
      {/* <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/">Buyer Portal</NavLink>
        </Nav.Item> */}
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/auction">
          Auction
        </NavLink>
      </Nav.Item>
    </Nav>
  );
};

module.exports = BuyerNav;
