const {
  RETRIEVE_PROJECT_LISTINGS,
  ALL_LISTINGS,
  GET_PROJECT_LISTING_BY_USERNAME,
  CREATE_PROJECT_LISTING,
  CREATE_BID_LISTING_BY_BIDDERID,
  CREATE_PROJECT_LISTING_BY_USERNAME,
  UPDATE_PROJECT_LISTING,
  DELETE_ALL_PROJECT_LISTINGS,
  DELETE_PROJECT_LISTINGS_BY_USER,
  DELETE_PROJECT_BY_ID,
  LOGOUT,
} = require('../actions/Types');

const initialState = [];

function projectListingReducer(state = initialState, action) {
	const { type, payload } = action;

	switch (type) {
		case CREATE_PROJECT_LISTING:
			return [...state, payload];

    case ALL_LISTINGS:
      return [...payload];

    case DELETE_PROJECT_BY_ID:
      return state.filter(p => p.id !== payload.id);

    case UPDATE_PROJECT_LISTING:
      return [...state.filter(p => p.id !== payload.id), payload];

    case LOGOUT:
      return [];

      default:
			return state;
	}
}

module.exports = projectListingReducer;
