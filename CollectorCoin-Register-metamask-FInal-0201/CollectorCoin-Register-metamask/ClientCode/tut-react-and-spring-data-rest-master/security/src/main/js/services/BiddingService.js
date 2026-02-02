const createBaseRequest = require('../common/http-common');

const createBidListingByBidderID = (bidderID, data) => {
  return createBaseRequest().post(`/restorer/${bidderID}/bid`, data);
};

const getAllBids = () => {
  return createBaseRequest().get('/bidListings');
};

const getBidListingByBidderId = (bidderId) => {
  return createBaseRequest().get(`/restorer/${bidderId}/bidListings`);
};

const selectBid = (bidId) => {
  return createBaseRequest().post(`/bidding/${bidId}/select`);
}

const updateBidById = (bidId, data) => {
  return createBaseRequest().put(`/bid/${bidId}`, data);
}

const BiddingService = {
	getAllBids,
	getBidListingByBidderId,
	createBidListingByBidderID,
  selectBid,
  updateBidById,
};

module.exports = BiddingService;
