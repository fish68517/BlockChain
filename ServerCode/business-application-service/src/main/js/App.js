"use strict";

const React = require("react");
const { BrowserRouter } = require("react-router-dom");
const Header = require("./components/toolbar/Header");
const Navigation = require("./Navigation");
const ErrorComponent = require("./components/ErrorComponent");

class App extends React.Component {
  render() {
    return (
      <BrowserRouter basename="/website">
        <ErrorComponent />
        <Header />
        <Navigation />
      </BrowserRouter>
    );
  }
}

module.exports = App;
