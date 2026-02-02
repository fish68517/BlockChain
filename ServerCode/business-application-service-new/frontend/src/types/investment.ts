export interface Investment {
  id: number;
  listingId: number;
  investorAddress: string;
  amount: number;
  transactionHash?: string;
  createdAt: string;
}

export interface InvestRequest {
  investorAddress: string;
  amount: number;
}

export interface AssignRestorerRequest {
  restorerAddress: string;
}

export interface ApiResponse {
  status: string;
  message: string;
}
