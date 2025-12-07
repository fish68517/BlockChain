const React = require("react");
const { Nav } = require("react-bootstrap");
const { NavLink } = require("react-router-dom");

const RestorerNav = () => {
  return (
    <Nav className="navbar-nav" defaultActiveKey="/restorerportfolio" as="ul">
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/restorerportfolio">
          My Portfolio
        </NavLink>
      </Nav.Item>
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/newbid">
          New Bid
        </NavLink>
      </Nav.Item>
    </Nav>
  );
};

module.exports = RestorerNav;
