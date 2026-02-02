import api from './api';
import axios from 'axios';
import type { ProjectListing, CreateListingRequest, UpdateListingRequest, EstimateRequest } from '@/types';

export const uploadImage = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await axios.post<{ url: string }>('/api/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data.url;
};

export const listingService = {
  getAll: () => api.get<ProjectListing[]>('/listings'),

  getById: (id: number) => api.get<ProjectListing>(`/listings/${id}`),

  create: (data: CreateListingRequest) => api.post<ProjectListing>('/listings', data),

  update: (id: number, data: UpdateListingRequest) => api.put<ProjectListing>(`/listings/${id}`, data),

  delete: (id: number) => api.delete(`/listings/${id}`),

  verify: (id: number) => api.post<ProjectListing>(`/listings/${id}/verify`),

  estimate: (id: number, data: EstimateRequest) => api.post<ProjectListing>(`/listings/${id}/estimate`, data),

  launch: (id: number) => api.post<ProjectListing>(`/listings/${id}/launch`),
};
