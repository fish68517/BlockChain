const axios = require('axios');
const createBaseRequest = require('../common/http-common');
const { getAuthHeader } = require('../utils/auth');


const getInvestmentsByUserID = (userID, size, number) => {
  const params = {
    page: number,
    size,
  };

  return createBaseRequest().get(`/users/id/${userID}/investments`, { params });
};

const createInvestmentsByUserID = (userID, data) => {
  return axios.post(`http://localhost:8080/api/users/${userID}/investments`, data, {
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
    }
  });
};


const InvestmentService = {
	getInvestmentsByUserID,
  createInvestmentsByUserID
};

module.exports = InvestmentService;
