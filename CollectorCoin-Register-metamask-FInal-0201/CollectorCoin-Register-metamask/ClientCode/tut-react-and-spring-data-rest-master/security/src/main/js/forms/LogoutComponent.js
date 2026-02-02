const React = require('react');
const { Navigate } = require('react-router-dom');
const { connect } = require('react-redux');
const { logout } = require("../actions/Auth");

class Logout extends React.Component {
  componentDidMount() {
    this.props.dispatch(logout());
  }

  render () {
    return <Navigate to="/login" />
  }
};

module.exports = connect()(Logout);
