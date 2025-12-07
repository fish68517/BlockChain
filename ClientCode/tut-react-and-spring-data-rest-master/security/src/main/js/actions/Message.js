const SET_MESSAGE = require('./Types');
const CLEAR_MESSAGE = require('./Types');

module.exports = setMessage = (message) => ({
  type: SET_MESSAGE,
  payload: message,
});

module.exports = clearMessage = () => ({
  type: CLEAR_MESSAGE,
});