const {
  ALL_BID_LISTINGS,
  ALL_LISTINGS,
  UPDATE_BID_LISTING,
  CREATE_BID_LISTING_BY_BIDDERID,
  UPDATE_PROJECT_LISTING,
  SELECT_BID_LISTING,
} = require("./Types");

const { getListingsPendingBid } = require("../services/ProjectListingsService");

const {
  createBidListingByBidderID,
  getBidListingByBidderId,
  updateBidById,
  getAllBids,
  selectBid,
} = require("../services/BiddingService");

const getAllBidsAction = () => async (dispatch) => {
  try {
    const res = await getAllBids();

    dispatch({
      type: ALL_BID_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const selectBidAction = (id) => async (dispatch) => {
  try {
    const res = await selectBid(id); // returns updated LISTING not BID
    dispatch({
      type: SELECT_BID_LISTING,
      payload: id,
    });

    dispatch({
      type: UPDATE_PROJECT_LISTING,
      payload: res.data,
    });
  } catch (err) {
    return Promise.reject(err);
  }
};

const getBidListings = (userId) => async (dispatch) => {
  try {
    const res = await getBidListingByBidderId(userId);

    dispatch({
      type: ALL_BID_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const getListingsPendingBidAction = () => async (dispatch) => {
  try {
    // TBD replace with correct fetch
    const res = await getListingsPendingBid();

    dispatch({
      type: ALL_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

// async create
const createBidListingByBidderIDAction =
  (bidderID, data) => async (dispatch) => {
    try {
      const res = await createBidListingByBidderID(bidderID, data);

      dispatch({
        type: CREATE_BID_LISTING_BY_BIDDERID,
        payload: res.data,
      });

      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };

const updateBidByIdAction = (bidId, data) => async (dispatch) => {
  try {
    const res = await updateBidById(bidId, data);

    dispatch({
      type: UPDATE_BID_LISTING,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

module.exports = {
  getListingsPendingBid: getListingsPendingBidAction,
  getBidListings,
  createBidListingByBidderID: createBidListingByBidderIDAction,
  updateBidById: updateBidByIdAction,
  getAllBids: getAllBidsAction,
  selectBid: selectBidAction,
};
