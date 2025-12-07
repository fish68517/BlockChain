const {
    GET_INVESTMENT_BY_USERID,
    CREATE_INVESTMENT_BY_USERID,
  } = require('./Types');
  
  const regeneratorRuntime = require('regenerator-runtime');
  
  const { getInvestmentsByUserID,createInvestmentsByUserID} = require('../services/InvestmentsService');


  
  const getInvestmentsByUserIDAction = (userId, pageSize = 10, pageNumber = 1) => async (dispatch) => {
    try {
      const res = await getInvestmentsByUserID(userId, pageSize, pageNumber);
  
      dispatch({
        type: GET_INVESTMENT_BY_USERID,
        payload: res.data,
      })
  
      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };


  const createInvestmentsByUserIDAction = (userId, pageSize = 10, pageNumber = 1) => async (dispatch) => {
    try {
      const res = await createInvestmentsByUserID(userId, pageSize, pageNumber);
  
      dispatch({
        type: CREATE_INVESTMENT_BY_USERID,
        payload: res.data,
      })
  
      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };
  
  
  module.exports = {
    getInvestmentsByUserID: getInvestmentsByUserIDAction,
    createInvestmentsByUserID: createInvestmentsByUserIDAction,

  }
  