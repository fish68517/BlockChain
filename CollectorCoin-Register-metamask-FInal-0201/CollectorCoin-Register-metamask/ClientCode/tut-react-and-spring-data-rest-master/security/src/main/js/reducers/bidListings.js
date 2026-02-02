const {
  ALL_BID_LISTINGS,
  CREATE_BID_LISTING_BY_BIDDERID,
  UPDATE_BID_LISTING,
  LOGOUT,
  SELECT_BID_LISTING,
} = require('../actions/Types');

const initialState = [];

function bidListingReducer(state = initialState, action) {
	const { type, payload } = action;

	switch (type) {

    case CREATE_BID_LISTING_BY_BIDDERID:
      return [...state, payload];

    case ALL_BID_LISTINGS:
      return [...payload];

    case UPDATE_BID_LISTING:
      return [...state.filter(p => p.id !== payload.id), payload];

    case SELECT_BID_LISTING:
      const bid = state.find(b => b.id === payload);
      bid.isSelected = true;

      return [...state.filter(p => p.id !== bid.id), bid];

    case LOGOUT:
      return [];

    default:
			return state;
	}
}

module.exports = bidListingReducer;
