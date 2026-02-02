const {
  REGISTER_SUCCESS,
  REGISTER_FAIL,
  LOGIN_SUCCESS,
  LOGIN_FAIL,
  LOGOUT,
} = require("../actions/Types");
const {
  WALLET_AUTH_SUCCESS,
  WALLET_AUTH_FAIL,
} = require("../actions/WalletAuth");

const initialState = { isLoggedIn: false, user: null };

module.exports = function (state = initialState, action) {
  const { type, payload } = action;
  switch (type) {
    case REGISTER_SUCCESS:
      return {
        ...state,
        isLoggedIn: false,
      };
    case REGISTER_FAIL:
      return {
        ...state,
        isLoggedIn: false,
      };
    case LOGIN_SUCCESS:
      const { username, roles, accessToken, id } = payload;
      return {
        ...state,
        isLoggedIn: true,
        user: {
          id,
          username,
          roles,
          accessToken,
        },
      };
    case LOGIN_FAIL:
      return {
        ...state,
        isLoggedIn: false,
        user: null,
      };
    case LOGOUT:
      return {
        ...state,
        isLoggedIn: false,
        user: null,
      };
    case WALLET_AUTH_SUCCESS:
      return {
        ...state,
        isLoggedIn: true,
        user: action.payload.user,
      };
    case WALLET_AUTH_FAIL:
      return {
        ...state,
        isLoggedIn: false,
        user: null,
      };
    default:
      return state;
  }
};
