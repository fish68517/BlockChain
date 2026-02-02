import api from './api';
import type { InvestRequest, AssignRestorerRequest, ApiResponse, Investment } from '@/types';

export const investmentService = {
  invest: (listingId: number, data: InvestRequest) =>
    api.post<ApiResponse>(`/listings/${listingId}/invest`, data),

  getByListing: (listingId: number) =>
    api.get<Investment[]>(`/investments/listing/${listingId}`),

  assignRestorer: (listingId: number, data: AssignRestorerRequest) =>
    api.post<ApiResponse>(`/listings/${listingId}/assign-restorer`, data),

  completeRestoration: (listingId: number) =>
    api.post<ApiResponse>(`/listings/${listingId}/complete-restoration`),
};
