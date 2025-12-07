const SET_MESSAGE = require("../actions/Types");
const CLEAR_MESSAGE = require("../actions/Types");

const initialState = {};

module.exports = function (state = initialState, action) {
  const { type, payload } = action;

  switch (type) {
    case SET_MESSAGE:
      return { message: payload };

    case CLEAR_MESSAGE:
      return { message: "" };

    default:
      return state;
  }
}
