const { createStore, applyMiddleware } = require('redux');
const thunkMiddleware = require('redux-thunk').default;
const { composeWithDevTools } = require('redux-devtools-extension');
const throttle = require('lodash/throttle');

const rootReducer = require('../reducers/allReducers');
const { saveState, loadState } = require('../utils/localStorage');

function configureStore() {
  const middlewares = [thunkMiddleware];
  const middlewareEnhancer = applyMiddleware(...middlewares);
  const enhancers = [middlewareEnhancer];
  const composedEnhancers = composeWithDevTools(...enhancers);

  const store = createStore(rootReducer, loadState(), composedEnhancers);
  store.subscribe(throttle(() => {
    saveState(store.getState());
  }, 1000));

  return store;
};

module.exports = configureStore();
