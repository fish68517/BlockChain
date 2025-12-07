const React = require('react');

class Home extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      content: "HOME CONTENT"
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

module.exports = Home;