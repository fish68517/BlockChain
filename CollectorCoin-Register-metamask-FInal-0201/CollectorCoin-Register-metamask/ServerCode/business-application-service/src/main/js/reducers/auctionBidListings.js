const {
  ALL_AUCTION_BID_LISTINGS,
  CREATE_AUCTION_BID_LISTING_BY_BUYERRID,
  UPDATE_AUCTION_BID_LISTING,
  LOGOUT,
  SELECT_AUCTION_BID_LISTING,
} = require("../actions/Types");

const initialState = [];

function auctionBidListingReducer(state = initialState, action) {
  const { type, payload } = action;

  switch (type) {
    case CREATE_AUCTION_BID_LISTING_BY_BUYERRID:
      return [...state, payload];

    case ALL_AUCTION_BID_LISTINGS:
      return [...payload];

    case UPDATE_AUCTION_BID_LISTING:
      return [...state.filter((p) => p.id !== payload.id), payload];

    case SELECT_AUCTION_BID_LISTING:
      const bid = state.find((b) => b.id === payload);
      bid.isSelected = true;

      return [...state.filter((p) => p.id !== bid.id), bid];

    case LOGOUT:
      return [];

    default:
      return state;
  }
}

module.exports = auctionBidListingReducer;
