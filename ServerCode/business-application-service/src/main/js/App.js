"use strict";
// 手动填补 process 环境，防止 Web3 库崩溃
import process from 'process';
window.process = process;

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
