const {
  GET_INVESTMENT_BY_USERID,
  CREATE_INVESTMENT_BY_USERID,
  LOGOUT,
} = require("../actions/Types");

const initialState = [];

function investmentReducer(state = initialState, action) {
  const { type, payload } = action;

  switch (type) {
    case GET_INVESTMENT_BY_USERID:
      return [...payload];

    case CREATE_INVESTMENT_BY_USERID:
      return [...state, payload];

    case LOGOUT:
      return [];

    default:
      return state;
  }
}

module.exports = investmentReducer;
