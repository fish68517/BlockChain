const React = require('react');
const { NavLink } = require('react-router-dom');
const { Nav } = require('react-bootstrap');

const InvestorNav = () => {
	return (
    <Nav className="navbar-nav" as="ul">
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/">Investor Portal</NavLink>
      </Nav.Item>
      <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/investedPortfolio">Invested Portfolio</NavLink>
      </Nav.Item>
      {/* <Nav.Item className="nav-item" as="li">
        <NavLink className="nav-link" to="/auction">Auction</NavLink>
      </Nav.Item> */}
    </Nav>
	)
}

module.exports = InvestorNav;
