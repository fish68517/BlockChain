const React = require("react");
const { Nav } = require("react-bootstrap");
const { NavLink } = require("react-router-dom");

const AdminNav = () => {
  return (
    <Nav className="navbar-nav" defaultActiveKey="/admin" as="ul">
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/admin">
          New Projects
        </NavLink>
      </Nav.Item>
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/selectfrombids">
          All Bids
        </NavLink>
      </Nav.Item>
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/selectfromauctionbids">
          All Buy Bids
        </NavLink>
      </Nav.Item>
    </Nav>
  );
};

module.exports = AdminNav;
