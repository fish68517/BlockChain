'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const { Provider } = require('react-redux');

const App = require("./App");
const store = require('./app/configureStore');

console.log(store.getState())

ReactDOM.render(
	<Provider store={store}>
    <App />
	</Provider>,
	document.getElementById('react')
)
