import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import type { ProjectListing } from '@/types';
import { listingService } from '@/services';

interface ListingState {
  items: ProjectListing[];
  current: ProjectListing | null;
  loading: boolean;
  error: string | null;
}

const initialState: ListingState = {
  items: [],
  current: null,
  loading: false,
  error: null,
};

export const fetchListings = createAsyncThunk(
  'listing/fetchAll',
  async () => {
    const response = await listingService.getAll();
    return response as unknown as ProjectListing[];
  }
);

export const fetchListingById = createAsyncThunk(
  'listing/fetchById',
  async (id: number) => {
    const response = await listingService.getById(id);
    return response as unknown as ProjectListing;
  }
);

const listingSlice = createSlice({
  name: 'listing',
  initialState,
  reducers: {
    clearCurrent: (state) => {
      state.current = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchListings.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchListings.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchListings.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch';
      })
      .addCase(fetchListingById.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchListingById.fulfilled, (state, action) => {
        state.loading = false;
        state.current = action.payload;
      })
      .addCase(fetchListingById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch';
      });
  },
});

export const { clearCurrent, clearError } = listingSlice.actions;
export default listingSlice.reducer;
