# CollectorCoin Project Context

> This document provides context for AI assistants working on this project.

## Project Overview

CollectorCoin is a blockchain-based NFT platform for collectible car restoration and investment. It allows:
- Owners to list restoration projects
- Restorers to bid on restoration work
- Investors to fund projects
- Buyers to purchase restored items via auction

## Tech Stack

| Layer | Technology |
|-------|------------|
| Smart Contracts | Solidity + Hardhat |
| Backend | Java 17 + Spring Boot 3.x |
| Frontend | React + TypeScript + Vite |
| Database | MySQL 8.0 |
| Blockchain | Ethereum (local Hardhat node) |
| UI Library | Ant Design |
| State Management | Redux Toolkit |

## Project Structure

```
CollectorCoin/
├── contracts/           # Hardhat + Solidity smart contracts
│   ├── contracts/
│   │   ├── CCToken.sol         # ERC-20 platform token
│   │   └── CollectorCoinNFT.sol # ERC-1155 dynamic NFT
│   └── scripts/
│       └── deploy.js           # Deployment script
├── frontend/            # React + TypeScript frontend
│   ├── src/
│   │   ├── components/         # Reusable components
│   │   ├── pages/              # Page components
│   │   ├── services/           # API services
│   │   ├── store/              # Redux store
│   │   └── types/              # TypeScript types
│   └── vite.config.ts
├── server/              # Java Spring Boot backend
│   └── src/main/java/com/collectorcoin/
│       ├── controller/         # REST controllers
│       ├── model/              # JPA entities
│       ├── repository/         # Data repositories
│       ├── service/            # Business logic
│       └── dto/                # Data transfer objects
└── docs/                # Documentation
```

## Business Flow

```
Owner Submit → Verify → Estimate (Mint NFT) → Launch → Restorer Bid → Select Restorer → Invest → [Auto Assign] → Restore → Auction → Sold
     ↓           ↓            ↓                 ↓           ↓              ↓            ↓          ↓           ↓         ↓        ↓
  PENDING → VERIFIED → ESTIMATED → LAUNCHED → (bidding) → FUNDING → FUNDING → RESTORING → RESTORED → AUCTION → SOLD
```

### Key Flow Details

1. **Owner Submit**: Owner creates listing with their own value/repair estimations
2. **Verify**: Admin verifies the project is legitimate
3. **Estimate**: Admin gives final estimation (referencing owner's), mints NFT
4. **Launch**: NFT goes live, restorers can bid
5. **Restorer Bid**: Multiple restorers submit bids (lowest price strategy)
6. **Select Restorer**: Admin selects lowest bid, sets fundingTarget
7. **Invest**: Investors fund the project with CCT
8. **Auto Assign**: When funding reaches target, restorer is auto-assigned
9. **Restore**: Restorer completes the work
10. **Auction**: Admin posts for auction, buyers bid
11. **Sold**: Winner receives NFT, investors get returns

## Key Entities

### ProjectListing (server/src/main/java/com/collectorcoin/model/ProjectListing.java)

Main entity representing a restoration project:
- `id`, `title`, `description`, `vin`, `imageUrl`
- `ownerValueEstimation`, `ownerRepairEstimation` - Owner's estimates
- `valueEstimation`, `repairEstimation` - Admin's final estimates
- `selectedRestorerId`, `selectedRestorerAddress`, `selectedRestorerBid` - Selected restorer info
- `fundingTarget`, `currentFunding` - Funding tracking
- `nftTokenId`, `nftMetadataUri`, `nftMinted`, `nftLaunched` - NFT info
- `status` - Current status in workflow

### RestorerBid (server/src/main/java/com/collectorcoin/model/RestorerBid.java)

Restorer bidding entity:
- `listingId` - Associated listing
- `restorerId`, `restorerAddress` - Restorer info
- `bidAmount` - Bid amount in CCT
- `status` - PENDING, SELECTED, or REJECTED

### Status Values

```java
PENDING    // Initial state after creation
VERIFIED   // Admin verified the project
ESTIMATED  // Admin estimated value, NFT minted
LAUNCHED   // NFT launched, open for restorer bids
FUNDING    // Restorer selected, accepting investments
RESTORING  // Funding complete, restoration in progress
RESTORED   // Restoration complete
AUCTION    // Posted for auction
SOLD       // Auction complete, NFT transferred
REJECTED   // Project rejected by admin
```

## Smart Contracts

### CCToken.sol (ERC-20)

Platform currency for investments and trading:
- Symbol: CCT
- Initial supply: 1,000,000 CCT
- Functions: `mint()`, `burn()`, `transferForInvestment()`

### CollectorCoinNFT.sol (ERC-1155)

Dynamic NFT representing restoration projects:
- `mintDNFT()` - Mint new project NFT
- `launchDNFT()` - Launch NFT for investment
- `addInvestor()` - Record investor contribution
- `updateRestorer()` - Assign restorer
- `markRestorationComplete()` - Mark restoration done
- `setItemForSale()` - Post for auction
- `redistributeFunds()` - Distribute returns to investors
- `transferToWinner()` - Transfer NFT to auction winner

## Key Services

### InvestmentProxyService.java

Handles investment operations:
- `processInvestment()` - Process investor contribution
- `autoAssignRestorer()` - Auto-assign when funding target reached
- `assignRestorer()` - Manual restorer assignment
- `completeRestoration()` - Mark restoration complete
- `postForAuction()` - Start auction
- `transferToWinner()` - Complete auction

### BlockchainService.java

Interfaces with smart contracts via Web3j.

## API Endpoints

### Listings
- `GET /api/listings` - Get all listings
- `POST /api/listings` - Create listing
- `PUT /api/listings/{id}/verify` - Verify listing
- `PUT /api/listings/{id}/estimate` - Estimate and mint NFT
- `PUT /api/listings/{id}/launch` - Launch NFT

### Restorer Bids
- `GET /api/restorer-bids/listing/{listingId}` - Get bids for listing
- `POST /api/restorer-bids` - Submit bid
- `POST /api/restorer-bids/select/{bidId}` - Select winning bid

### Investments
- `POST /api/investments/invest` - Make investment
- `GET /api/investments/listing/{listingId}` - Get investors for listing

### Auctions
- `POST /api/auctions/start` - Start auction
- `POST /api/auctions/bid` - Place bid
- `POST /api/auctions/finalize` - End auction

## Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/collectorcoin
spring.datasource.username=root
spring.datasource.password=123456

# Blockchain
blockchain.enabled=false  # Set true for real blockchain calls
blockchain.rpc.url=http://localhost:8545
blockchain.contract.marketplace.address=0x...

# Admin wallet (Hardhat Account #0)
blockchain.admin.private-key=0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80
```

## Development Commands

```bash
# Start Hardhat node
cd contracts && npx hardhat node

# Deploy contracts
cd contracts && npx hardhat run scripts/deploy.js --network localhost

# Start backend
cd server && mvn spring-boot:run

# Start frontend
cd frontend && pnpm dev
```

## Common Issues

1. **ERC1155InsufficientBalance**: Set `blockchain.enabled=false` for demo mode
2. **Empty investor list**: Check `investmentService.getByListing()` is called
3. **Auto-assign not working**: Verify `selectedRestorerAddress` is set when selecting bid

## Recent Changes (v2.0)

1. Added CCToken (ERC-20) for platform currency
2. Added Owner estimation fields (ownerValueEstimation, ownerRepairEstimation)
3. Implemented Restorer Bidding system
4. Implemented auto-assign restorer when funding target reached
5. Fixed autoAssignRestorer() to actually call blockchain

## File Quick Reference

| Purpose | File |
|---------|------|
| Main entity | `server/.../model/ProjectListing.java` |
| Restorer bid entity | `server/.../model/RestorerBid.java` |
| Bid controller | `server/.../controller/RestorerBidController.java` |
| Investment service | `server/.../service/InvestmentProxyService.java` |
| Blockchain service | `server/.../service/BlockchainService.java` |
| Frontend types | `frontend/src/types/listing.ts` |
| Listing form | `frontend/src/components/listing/ListingForm.tsx` |
| Investment page | `frontend/src/pages/Investment.tsx` |
| CCToken contract | `contracts/contracts/CCToken.sol` |
| NFT contract | `contracts/contracts/CollectorCoinNFT.sol` |
