const axios = require("axios");
const createBaseRequest = require("../common/http-common");
const { getAuthHeader } = require("../utils/auth");

const getInvestmentsByUserID = (userID, size, number) => {
  const params = {
    page: number,
    size,
  };

  return createBaseRequest().get(`/users/id/${userID}/investments`, { params });
};

const createInvestmentsByUserID = (userID, data) => {
  return axios.post(
    `http://localhost:8090/api/users/${userID}/investments`,
    data,
    {
      headers: {
        "Content-Type": "application/json",
        ...getAuthHeader(),
      },
    }
  );
};

const createInvestmentsByUserIDWithTransaction = (userID, data, transactionHash) => {
  const requestData = {
    ...data,
    transactionHash: transactionHash
  };
  return axios.post(
    `http://localhost:8090/api/users/${userID}/investments/with-transaction`,
    requestData,
    {
      headers: {
        "Content-Type": "application/json",
        ...getAuthHeader(),
      },
    }
  );
};

const InvestmentService = {
  getInvestmentsByUserID,
  createInvestmentsByUserID,
  createInvestmentsByUserIDWithTransaction,
};

module.exports = InvestmentService;
