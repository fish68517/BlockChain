const { combineReducers } = require("redux");
const authReducer = require('./auth');
const messageReducer = require('./message');
const projectListingReducer = require('./projectListings');
const investmentReducer = require('./investment');
const bidListingReducer = require('./bidListings');
const auctionBidListingReducer = require('./auctionBidListings');


const allReducers = combineReducers({
	auth: authReducer,
	message: messageReducer,
	projectListings: projectListingReducer,
	investments: investmentReducer,
	bidListings: bidListingReducer,
	auctionBidListings: auctionBidListingReducer
});



module.exports = allReducers;
