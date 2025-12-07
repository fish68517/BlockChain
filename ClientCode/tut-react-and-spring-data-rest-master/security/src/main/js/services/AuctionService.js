const createBaseRequest = require('../common/http-common');

const createAuctionBidListingByBuyerID = (buyerID, data) => {
  return createBaseRequest().post(`/buyer/${buyerID}/auctionBid`, data);
};

const getAllAuctionBids = () => {
  return createBaseRequest().get('/auctionBidListings');
};

const getAuctionBidListingByBuyerId = (buyerId) => {
  return createBaseRequest().get(`/buyer/${buyerId}/auctionBidListings`);
};

const selectAuctionBid = (bidId) => {
  return createBaseRequest().post(`/auctionBidding/${bidId}/select`);
}

const updateAuctionBidById = (bidId, data) => {
  return createBaseRequest().put(`/auctionBid/${bidId}`, data);
}

const AuctionService = {
    getAllAuctionBids,
    getAuctionBidListingByBuyerId,
    createAuctionBidListingByBuyerID,
    selectAuctionBid,
    updateAuctionBidById,
};

module.exports = AuctionService;
