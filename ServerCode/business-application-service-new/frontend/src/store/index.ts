import { configureStore } from '@reduxjs/toolkit';
import listingReducer from './slices/listingSlice';
import walletReducer from './slices/walletSlice';
import authReducer from './slices/authSlice';
import workflowReducer from './slices/workflowSlice';

export const store = configureStore({
  reducer: {
    listing: listingReducer,
    wallet: walletReducer,
    auth: authReducer,
    workflow: workflowReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
