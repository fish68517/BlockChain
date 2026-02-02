export interface AuctionRequest {
  auctionPrice: number;
}

export interface FinalizeAuctionRequest {
  winnerAddress: string;
}

export interface Auction {
  id: number;
  listingId: number;
  startPrice: number;
  currentBid?: number;
  highestBidder?: string;
  endTime?: string;
  status: 'ACTIVE' | 'ENDED' | 'CANCELLED';
}

export interface Bid {
  id: number;
  auctionId: number;
  bidderAddress: string;
  amount: number;
  transactionHash?: string;
  createdAt: string;
}

// Restorer Bid types
export interface RestorerBid {
  id: number;
  listingId: number;
  restorerId: number;
  restorerAddress: string;
  bidAmount: number;
  status: 'PENDING' | 'SELECTED' | 'REJECTED';
  createdAt: string;
}

export interface RestorerBidRequest {
  listingId: number;
  restorerId: number;
  restorerAddress: string;
  bidAmount: number;
}
