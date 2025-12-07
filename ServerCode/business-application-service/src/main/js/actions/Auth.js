const AuthService = require("../services/AuthService");
const {
  REGISTER_SUCCESS,
  REGISTER_FAIL,
  LOGIN_SUCCESS,
  LOGOUT,
  LOGIN_FAIL,
  SET_MESSAGE,
} = require("./Types");

const register = (username, email, password, role) => (dispatch) => {
  return AuthService.register(username, email, password, [role]).then(
    (response) => {
      dispatch({
        type: REGISTER_SUCCESS,
      });

      dispatch({
        type: SET_MESSAGE,
        payload: response.data.message,
      });

      return Promise.resolve();
    },
    (error) => {
      const data = error.response && error.response.data;
      let message;
      // if data is an object without a 'message' key, it's validation errors
      if (data && typeof data === "object" && !data.message) {
        message = Object.values(data).join(", ");
      } else {
        message = (data && data.message) || error.message || error.toString();
      }
      dispatch({ type: REGISTER_FAIL });
      dispatch({ type: SET_MESSAGE, payload: message });
      return Promise.reject();
    }
  );
};
const login = (username, password) => (dispatch) => {
  return AuthService.login(username, password)
    .then(({ data }) => {
      dispatch({
        type: LOGIN_SUCCESS,
        payload: data,
      });

      return Promise.resolve();
    })
    .catch((error) => {
      const message =
        error.response?.data?.message || error.message || "Login error";

      dispatch({
        type: LOGIN_FAIL,
      });

      dispatch({
        type: SET_MESSAGE,
        payload: message,
      });

      return Promise.reject();
    });
};

const logout = () => (dispatch) => {
  return AuthService.logout().then(() => {
    dispatch({
      type: LOGOUT,
    });
  });
};
module.exports = {
  register,
  login,
  logout,
};
