"use strict";
// 1. 注入 NodeJS 兼容变量 (修复 Webpack 5 + Ethers v5 白屏问题)
// ==========================================
import process from 'process';
import { Buffer } from 'buffer';

// 强制注入到全局环境 window 中
window.process = process;
window.global = window;
window.Buffer = Buffer;


const React = require("react");
const ReactDOM = require("react-dom");
const { Provider } = require("react-redux");

const App = require("./App");
const store = require("./app/configureStore");

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById("react")
);
