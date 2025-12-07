const React = require('react');
const { Nav } = require('react-bootstrap');
const { NavLink } = require('react-router-dom');

const OwnerNav = () => {
	return (
    <Nav className="navbar-nav" as="ul">
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/myportfolio">My Portfolio</NavLink>
      </Nav.Item>
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/newlisting">New Project</NavLink>
      </Nav.Item>
      {/* <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/auction">Auction</NavLink>
      </Nav.Item> */}
    </Nav>
	)
}

module.exports = OwnerNav;
