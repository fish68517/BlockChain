import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Auction API
export const auctionApi = {
  getAllBids: () => api.get('/auction-bids'),
  getBidsByBuyer: (buyerId: number) => api.get(`/auction-bids/buyer/${buyerId}`),
  getBidsByListing: (listingId: number) => api.get(`/auction-bids/listing/${listingId}`),
  createBid: (data: { buyerId: number; listingId: number; bidAmount: number; transactionHash?: string }) => 
    api.post('/auction-bids', data),
  updateBid: (bidId: number, bidAmount: number) => 
    api.put(`/auction-bids/${bidId}`, { bidAmount }),
  selectBid: (bidId: number) => 
    api.post(`/auction-bids/${bidId}/select`),
};

// Investment API
export const investmentApi = {
  getAllInvestments: () => api.get('/investments'),
  getInvestmentsByUser: (userId: number) => api.get(`/investments/user/${userId}`),
  getInvestmentsByListing: (listingId: number) => api.get(`/investments/listing/${listingId}`),
  createInvestment: (data: { userId: number; listingId: number; amount: number; transactionHash?: string }) => 
    api.post('/investments', data),
  getUserTotalInvestment: (userId: number) => api.get(`/investments/user/${userId}/total`),
};

export default api;
