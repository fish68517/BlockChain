export const ListingStatus = {
  PENDING: 'PENDING',
  VERIFIED: 'VERIFIED',
  ESTIMATED: 'ESTIMATED',
  LAUNCHED: 'LAUNCHED',
  FUNDING: 'FUNDING',
  FUNDED: 'FUNDED',
  RESTORING: 'RESTORING',
  RESTORED: 'RESTORED',
  AUCTION: 'AUCTION',
  SOLD: 'SOLD',
  REJECTED: 'REJECTED',
} as const;

export type ListingStatus = typeof ListingStatus[keyof typeof ListingStatus];

export interface ProjectListing {
  id: number;
  projectAddress?: string;
  title: string;
  description?: string;
  vin?: string;
  imageUrl?: string;
  valueEstimation?: number;
  repairEstimation?: number;
  // Owner estimation (submitted by owner)
  ownerValueEstimation?: number;
  ownerRepairEstimation?: number;
  // Restorer bid fields
  selectedRestorerId?: number;
  selectedRestorerBid?: number;
  fundingTarget?: number;
  currentFunding?: number;
  status: ListingStatus;
  processId?: number;
  createdAt: string;
  updatedAt: string;
  // NFT fields
  nftTokenId?: number;
  nftMetadataUri?: string;
  nftMinted?: boolean;
  nftLaunched?: boolean;
}

export interface CreateListingRequest {
  title: string;
  description?: string;
  vin?: string;
  imageUrl?: string;
  ownerValueEstimation: number;
  ownerRepairEstimation: number;
}

export interface UpdateListingRequest {
  title?: string;
  description?: string;
  vin?: string;
  status?: string;
}

export interface EstimateRequest {
  valueEstimation: number;
  repairEstimation: number;
}
