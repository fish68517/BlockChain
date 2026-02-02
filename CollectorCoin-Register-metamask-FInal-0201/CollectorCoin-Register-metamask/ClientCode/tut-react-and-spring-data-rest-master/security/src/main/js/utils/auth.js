const store = require('../app/configureStore');

function getAuthHeader() {
  const { user } = store.getState().auth;
  if (user && user.accessToken) {
      return { Authorization: `Bearer ${user.accessToken}` };
  } else {
      return {};
  }
}

module.exports = {
  getAuthHeader,
}
