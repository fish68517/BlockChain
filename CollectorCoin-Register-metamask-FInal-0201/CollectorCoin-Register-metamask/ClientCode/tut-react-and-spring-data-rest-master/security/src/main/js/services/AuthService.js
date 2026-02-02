const axios = require("axios");
const API_URL = "http://localhost:8080/api/auth/";

class AuthService {
  login(username, password) {
    return axios.post(API_URL + "signin", { username, password })
  }

  logout() {
    return axios.post(API_URL + "signout");
  }

  register(username, email, password, role) {
    return axios.post(API_URL + "signup", {
      username,
      email,
      password,
      role,
    });
  }
}

module.exports = new AuthService();
