const React = require('react');

class UserPortal extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
		loggedInUser: this.props.loggedInUser, loggedInUserRole: this.props.loggedInUserRole,    
  		content: "USER CONTENT"
    };
  }

  render() {
    return (
      <div className="container">
        <header className="jumbotron">
          <h3>{this.state.content}</h3>
        </header>
      </div>
    );
  }
}

module.exports = UserPortal;