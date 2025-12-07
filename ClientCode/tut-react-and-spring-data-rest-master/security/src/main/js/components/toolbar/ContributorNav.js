const React = require('react');
const { Nav } = require('react-bootstrap');

const { Container } = require('react-bootstrap');
const { Button } = require('react-bootstrap');
const { ButtonToolbar } = require('react-bootstrap');
const { LinkContainer } = require('react-router-bootstrap');

const ContributorNav = () => {
	return (
		<Container className="p-3 mb-4 rounded-3">
			<ButtonToolbar className="custom-btn-toolbar">
            	<LinkContainer to="/">
              		<Button variant="outline-dark">My Portfolio</Button>
            	</LinkContainer>
            	<LinkContainer to="/availableproject">
              		<Button variant="outline-dark">Available Projects</Button>
            	</LinkContainer>
			</ButtonToolbar>
		</Container>
	)
}

module.exports = ContributorNav;