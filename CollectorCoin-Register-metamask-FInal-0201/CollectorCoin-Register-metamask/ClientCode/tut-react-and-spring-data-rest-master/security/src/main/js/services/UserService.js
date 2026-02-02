const client = require('./client');
const follow = require('./follow'); // function to hop multiple links by "rel"

const authHeader = require('./AuthHeader');

const API_URL = "http://localhost:8080/api/test/";

class UserService {
  getPublicContent() {
    return axios.get(API_URL + "all");
  }

  getUserBoard() {
    return axios.get(API_URL + "user", { headers: authHeader() });
  }

  getOwnerBoard() {
    return axios.get(API_URL + "owner", { headers: authHeader() });
  }

  getInvestorBoard() {
    return axios.get(API_URL + "investor", { headers: authHeader() });
  }

  getContributorBoard() {
    return axios.get(API_URL + "contributor", { headers: authHeader() });
  }

  getRestorerBoard() {
    return axios.get(API_URL + "restorer", { headers: authHeader() });
  }

  getAdminBoard() {
    return axios.get(API_URL + "admin", { headers: authHeader() });
  }

  getBuyerBoard() {
    return axios.get(API_URL + "buyer", { headers: authHeader() });
  }
}

module.exports = new UserService();
