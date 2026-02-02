import api from './api';
import type { RestorerBid, RestorerBidRequest } from '@/types';

export const restorerBidService = {
  getByListing: (listingId: number) =>
    api.get<RestorerBid[]>(`/restorer-bids/listing/${listingId}`),

  submitBid: (data: RestorerBidRequest) =>
    api.post<RestorerBid>('/restorer-bids', data),

  selectBid: (bidId: number) =>
    api.post(`/restorer-bids/select/${bidId}`),
};
