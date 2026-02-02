const axios = require("axios");
const { getAuthHeader } = require("../utils/auth");

function createBaseRequest() {
  const authHeader = getAuthHeader();
  return axios.create({
    baseURL: "http://localhost:8090/api",
    headers: {
      "Content-type": "application/json",
      ...authHeader,
    },
  });
}

module.exports = createBaseRequest;
