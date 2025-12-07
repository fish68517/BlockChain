const {
  REGISTER_SUCCESS,
  REGISTER_FAIL,
  LOGIN_SUCCESS,
  LOGIN_FAIL,
  LOGOUT,
  SET_MESSAGE,
} = require("./Types");

const AuthService = require("../services/AuthService");

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
      const message =
        (error.response &&
          error.response.data &&
          error.response.data.message) ||
        error.message ||
        error.toString();

      dispatch({
        type: REGISTER_FAIL,
      });

      dispatch({
        type: SET_MESSAGE,
        payload: message,
      });

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
      const message = error.response?.data?.message ||
          error.message ||
          "Login error";

      dispatch({
        type: LOGIN_FAIL,
      });

      dispatch({
        type: SET_MESSAGE,
        payload: message,
      });

      return Promise.reject();
    }
  );
};

const logout = () => (dispatch) => {
  return AuthService.logout().then(() => {
    dispatch({
      type: LOGOUT,
    });
  });
};

module.exports = {
  login, register, logout
}
