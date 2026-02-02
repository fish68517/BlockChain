import api from './api';
import type { AuctionRequest, FinalizeAuctionRequest, ApiResponse } from '@/types';

export const auctionService = {
  postAuction: (listingId: number, data: AuctionRequest) =>
    api.post<ApiResponse>(`/listings/${listingId}/post-auction`, data),

  finalize: (listingId: number, data: FinalizeAuctionRequest) =>
    api.post<ApiResponse>(`/listings/${listingId}/finalize`, data),
};
