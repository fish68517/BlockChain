'use strict';

const React = require('react');
const { BrowserRouter } = require('react-router-dom');
const { connect } = require("react-redux");

const Navigation = require('./Navigation');
const Header = require('./components/toolbar/Header');
class App extends React.Component {
	render() {
		return (
      <BrowserRouter>
        <Header />
        <Navigation />
		  </BrowserRouter>
		)
	}
}

function mapStateToProps(state) {
  const { user } = state.auth;
  return {
    user,
  };
}

const mapDispatchToProps = {
	loadLoggedInUserInfo: (user, roles) => (loadLoggedInUserInfo(user, roles)),
	unloadLoggedInUserInfo: (user, roles) => (unloadLoggedInUserInfo(user, roles))
};

module.exports = connect(mapStateToProps, mapDispatchToProps)(App);
