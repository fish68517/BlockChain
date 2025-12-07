const {
  ALL_AUCTION_BID_LISTINGS,
  ALL_LISTINGS,
  UPDATE_AUCTION_BID_LISTING,
  CREATE_AUCTION_BID_LISTING_BY_BUYERID,
  UPDATE_PROJECT_LISTING,
  SELECT_AUCTION_BID_LISTING,
} = require("./Types");

const {
  getListingsPendingAuctionBid,
} = require("../services/ProjectListingsService");

const {
  createAuctionBidListingByBuyerID,
  getAuctionBidListingByBuyerId,
  updateAuctionBidById,
  getAllAuctionBids,
  selectAuctionBid,
} = require("../services/AuctionService");

const getAllAuctionBidsAction = () => async (dispatch) => {
  try {
    const res = await getAllAuctionBids();

    dispatch({
      type: ALL_AUCTION_BID_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const selectAuctionBidAction = (id) => async (dispatch) => {
  try {
    const res = await selectAuctionBid(id); // returns updated LISTING not BID
    dispatch({
      type: SELECT_AUCTION_BID_LISTING,
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

const getAuctionBidListings = (userId) => async (dispatch) => {
  try {
    const res = await getAuctionBidListingByBuyerId(userId);

    dispatch({
      type: ALL_AUCTION_BID_LISTINGS,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

const getListingsPendingAuctionBidAction = () => async (dispatch) => {
  try {
    // TBD replace with correct fetch
    const res = await getListingsPendingAuctionBid();

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
const createAuctionBidListingByBuyerIDAction =
  (buyerID, data) => async (dispatch) => {
    try {
      const res = await createAuctionBidListingByBuyerID(buyerID, data);

      dispatch({
        type: CREATE_AUCTION_BID_LISTING_BY_BUYERID,
        payload: res.data,
      });

      return Promise.resolve(res.data);
    } catch (err) {
      return Promise.reject(err);
    }
  };

const updateAuctionBidByIdAction = (bidId, data) => async (dispatch) => {
  try {
    const res = await updateAuctionBidById(bidId, data);

    dispatch({
      type: UPDATE_AUCTION_BID_LISTING,
      payload: res.data,
    });

    return Promise.resolve(res.data);
  } catch (err) {
    return Promise.reject(err);
  }
};

module.exports = {
  getListingsPendingAuctionBid: getListingsPendingAuctionBidAction,
  getAuctionBidListings: getAuctionBidListings,
  createAuctionBidListingByBuyerID: createAuctionBidListingByBuyerIDAction,
  updateAuctionBidById: updateAuctionBidByIdAction,
  getAllAuctionBids: getAllAuctionBidsAction,
  selectAuctionBid: selectAuctionBidAction,
};
