# Step-by-Step UI Demo Testing Guide

## 📋 Prerequisites

Before starting, ensure you have:

1. ✅ **Backend server running** on `http://localhost:8090`
2. ✅ **Hardhat node running** on `http://127.0.0.1:8545`
3. ✅ **Factory ownership transferred** to admin wallet
4. ✅ **Contracts deployed** to Hardhat node
5. ✅ **Frontend application running** (usually on `http://localhost:3000` or similar)

### Setup Commands

```bash
# Terminal 1: Start Hardhat node
cd ServerCode/local-hardhat
npx hardhat node

# Terminal 2: Deploy contracts (if not already deployed)
cd ServerCode/local-hardhat
npx hardhat run scripts/deploy-cc.js --network localhost

# Terminal 3: Transfer factory ownership (if not already done)
cd ServerCode/local-hardhat
npx hardhat run scripts/transfer-factory-ownership.js --network localhost

# Terminal 4: Start backend server
cd ServerCode
./launch.sh  # or your launch command
```

---

## 👤 STEP 1: Owner Creates Project

### Actions:
1. **Login as Owner** user
2. Navigate to **"Create New Project Listing"** or **"New Listing"**
3. Fill out the form:
   - **Make:** `Honda`
   - **Model:** `Civic`
   - **VIN:** `1HGBH41JXMN109186`
   - **CCPG:** `1` (will be converted to wei automatically)
   - **Funding Goal:** `5` (will be converted to wei automatically)
   - **Description:** `Classic 1970 Honda Civic restoration project`
   - **Upload file:** Select a proof of ownership file (PDF/image)
4. Click **"Submit"** button

### ✅ Expected Result:
- **MetaMask popup appears** (to get owner's wallet address)
- Approve MetaMask request
- Success message: `"Successfully created listing."`
- Project is created on blockchain via backend API
- Project appears in project listings with status "Pending Approval"

### 🔍 What Happens:
- Frontend gets owner's wallet address from MetaMask
- Frontend calls backend: `POST /api/blockchain/admin/create-project`
- Backend creates project contract on blockchain
- Backend returns project address
- Frontend saves project to database with `projectAddress` field

---

## 👤 STEP 2: Admin Approves Project

### Actions:
1. **Login as Admin** user
2. Navigate to the project details page (click on the project you just created)
3. Scroll to the **"Approve"** section
4. Check the boxes:
   - ✅ **Verify Details:** Checked
   - ✅ **Receive Title:** Checked
5. Optionally add a message: `"Project approved. Ready for value estimation."`
6. Click **"Save"** button

### ✅ Expected Result:
- **NO MetaMask popup!** ✅ (This is the key improvement!)
- Success message appears
- Green **"Approved"** status box appears on the page
- Project status changes to "Approved" in the listing
- Next task becomes available: "Value Estimation"

### 🔍 What Happens:
- Frontend calls backend: `POST /api/blockchain/admin/approve-project?projectAddress=...`
- Backend signs transaction with admin wallet (server-side)
- Smart contract's `isApproved` is set to `true`
- Database is updated with approval status
- jBPM workflow advances to next step

---

## 👤 STEP 3: Admin Saves Value Estimations

### Actions:
1. Still on the project details page (as Admin)
2. Scroll to the **"Value Estimation"** section
3. Enter values:
   - **Value Estimate:** `10` (represents 10 CollectorCoin tokens)
   - **Repair Cost Estimate:** `5` (represents 5 CollectorCoin tokens)
4. Click **"Save"** button

### ✅ Expected Result:
- **NO MetaMask popup!** ✅
- Success message appears
- Values are saved and displayed on the page
- Next task becomes available: "Assign Restoration" (after restorer bidding)

### 🔍 What Happens:
- Frontend converts values to wei: `10` → `"10000000000000000000"` wei
- Frontend calls backend: `POST /api/blockchain/admin/save-estimations?projectAddress=...&valueEst=...&repairEst=...`
- Backend updates smart contract with estimations
- Database is updated
- Events are emitted: `EstimationsSaved`

---

## 👤 STEP 4: Restorer Submits Bid

### Actions:
1. **Login as Restorer** user
2. Navigate to **"New Bid"** or **"Restoration Bids"** page
3. Find the project you want to bid on (should show approved projects)
4. Enter **Bid Amount:** `5` (the amount you want for restoration)
5. Click **"Create Bid"** or **"Submit Bid"** button

### ✅ Expected Result:
- **MetaMask popup appears** (to get restorer's wallet address)
- Approve MetaMask request
- Success message: `"Successfully created a bid for listing X."`
- Bid appears in the bid listings

### 🔍 What Happens:
- Frontend gets restorer's wallet address from MetaMask
- Frontend saves bid to database:
  - `biddingPrice`: `5`
  - `restorerAddress`: restorer's wallet address
  - `listingId`: project ID
- **No blockchain transaction yet** - just database entry
- Admin can now see this bid in "All Bids" page

---

## 👤 STEP 5: Admin Selects Restorer

### Actions:
1. **Login as Admin** user
2. Navigate to **"All Bids"** page
3. Find the project you want to assign
4. Review the bids (should show all restorers who bid)
5. Click **"Select"** button next to the restorer you want to choose
6. Confirm in the modal: "Are you sure you want to approve a bid of X from restorer Y?"

### ✅ Expected Result:
- **NO MetaMask popup!** ✅
- Success message appears
- Selected bid shows green **"Selected"** badge
- Project status updates: `fundingLive = true` (funding can now begin)
- Next task: "Assign Restoration" becomes available (after funding)

### 🔍 What Happens:
- Frontend converts bid amount to wei: `5` → `"5000000000000000000"` wei
- Frontend calls backend: `POST /api/blockchain/admin/set-restorer?projectAddress=...&restorerAddress=...&fundingGoal=...`
- Backend updates smart contract:
  - Sets `selectedRestorer` = restorer's address
  - Sets `fundingGoal` = bid amount
  - Sets `fundingLive = true`
- Database is updated (bid marked as selected)
- Event emitted: `RestorerSelected`

---

## 👤 STEP 6: Investor Funds Project

### Actions:
1. **Login as Investor** user
2. Navigate to **"Investor Portal"** or project listings
3. Find the approved project (status should show it's ready for funding)
4. Enter **Investment Amount:** `5` (CollectorCoin tokens you want to invest)
5. Click **"Invest"** button

### ✅ Expected Result:
- **MetaMask popup #1:** Approve token spending (allow project to withdraw tokens from your account)
- **MetaMask popup #2:** Confirm the investment transaction (`acceptFunds`)
- Success message: `"Successfully created investment for listing X."`
- Project's funding progress updates

### 🔍 What Happens:
- Frontend calls `approveFunding()` - approves project contract to spend tokens (MetaMask popup #1)
- Frontend calls `acceptFunds()` - transfers tokens to project contract (MetaMask popup #2)
- Smart contract updates:
  - `fundsDeposited` increases
  - Investor added to `investors` array
  - If `fundsDeposited == fundingGoal`, then `fundingLive = false`
- Database is updated with investment record
- Event emitted: `InvestorAdded`

**Note:** If project needs `5` tokens and investor invests `5`, funding is complete!

---

## ⚠️ STEP 6a: Alternative - Fund via Script (For Testing)

If you don't have investor functionality or want to test quickly:

```bash
# Get the project address from Step 1 (check backend logs or database)
cd ServerCode/local-hardhat
PROJECT_ADDRESS=0x... FUNDING_GOAL=5000000000000000000 \
  npx hardhat run scripts/fund-project.js --network localhost
```

This script:
- Mints tokens to a test investor account
- Approves project to spend tokens
- Funds the project to reach the funding goal

---

## 👤 STEP 7: Admin Assigns Restoration

### Actions:
1. **Login as Admin** user
2. Navigate back to the project details page
3. Scroll to **"Assign Restoration"** section
4. Click **"Assign Restoration"** button

### ✅ Expected Result:
- **NO MetaMask popup!** ✅
- Success message appears
- Restoration is assigned to the selected restorer
- Funds are transferred to restorer's wallet (on blockchain)
- Next task: "Post Item for Sale" becomes available

### 🔍 What Happens:
- Frontend calls backend: `POST /api/blockchain/admin/assign-restoration?projectAddress=...`
- Backend checks:
  - `fundingLive == false` (funding must be complete)
  - `fundsDeposited == fundingGoal` (goal must be reached)
- Backend transfers `fundingGoal` tokens from project contract to restorer's address
- Event emitted: `FundingAllocated`
- Database is updated

---

## 👤 STEP 8: Restorer Finishes Restoration

### Actions:
1. **Login as Restorer** user
2. Navigate to **"My Projects"** or project details page
3. Find the project assigned to you
4. Scroll to **"Finish Restoration"** section
5. Click **"Finish Restoration"** button

### ✅ Expected Result:
- Success message appears
- Project status updates to "Restoration Finished"
- Next task: "Start Auction" becomes available for admin

### 🔍 What Happens:
- **No blockchain transaction** - this is a database-only operation
- Database field `isRestorationFinished = true`
- jBPM workflow advances to next step

---

## 👤 STEP 9: Admin Posts for Sale & Opens Auction

### Actions:
1. **Login as Admin** user
2. Navigate to project details page
3. Scroll to **"Post Item for Sale"** section
4. Click **"Post Item for Sale"** button (this is a DB operation)
5. Scroll to **"Start Auction"** section
6. Click **"Start Auction"** button

### ✅ Expected Result:
- **NO MetaMask popup!** ✅ (for opening auction)
- Success message appears
- Project status updates: Auction is now open
- Next task: "Set Buyer" becomes available (after buyers bid)

### 🔍 What Happens:
- "Post Item for Sale" updates database: `isPostedForSale = true`
- "Start Auction" calls backend: `POST /api/blockchain/admin/open-auction?projectAddress=...`
- Backend sets `openAuction = true` in smart contract
- Event emitted: `AuctionIsOpen`
- Buyers can now place bids

---

## 👤 STEP 10: Buyer Places Auction Bid

### Actions:
1. **Login as Buyer** user
2. Navigate to **"Auction"** or **"Buyer Portal"** page
3. Find the open auction project
4. Enter **Bid Amount:** `15` (CollectorCoin tokens you're willing to pay)
5. Click **"Place Bid"** or **"Bid"** button

### ✅ Expected Result:
- **MetaMask popup #1:** Approve token spending (allow project to withdraw tokens)
- **MetaMask popup #2:** Confirm the bid transaction (`addNewAuctionBid`)
- Success message: `"Successfully created a bid for listing X."`
- Your bid appears in the auction bids list

### 🔍 What Happens:
- Frontend calls `approveFunding()` - approves project contract (MetaMask popup #1)
- Frontend calls `addNewAuctionBid()` - transfers tokens to project contract (MetaMask popup #2)
- Smart contract:
  - Stores bid amount in `auctionBids[buyerAddress]`
  - Adds buyer to `bidders` array
  - Holds tokens in project contract
- Database is updated with bid record
- Event emitted: `BuyBidReceived`

**Note:** Multiple buyers can bid. Only one will win.

---

## 👤 STEP 11: Admin Selects Buyer & Redistributes

### Actions:
1. **Login as Admin** user
2. Navigate to **"All Auction Bids"** page
3. Review all bids for the project
4. Find the **highest bidder** (or the one you want to select)
5. Click **"Select"** button next to that buyer
6. Confirm in modal: "Are you sure you want to approve a bid of X from buyer Y?"

### ✅ Expected Result:
- **NO MetaMask popup!** ✅ (Two blockchain operations happen, but both server-side)
- Success message appears
- Selected bid shows green **"Selected"** badge
- Project status: "Completed"
- Funds are redistributed:
  - Winning buyer receives NFT/ownership
  - Other buyers get refunded
  - Investors receive their share
  - Owner receives their share

### 🔍 What Happens:
- Frontend calls backend: `POST /api/blockchain/admin/set-buyer?projectAddress=...&buyerAddress=...`
  - Backend sets `selectedBuyer` in contract
  - Backend calls `refundBids()` - refunds all non-winning bidders
  - Event emitted: `BuyerSelected`, `BuyBidRefunded`
  
- Frontend calls backend: `POST /api/blockchain/admin/redistribute?projectAddress=...`
  - Backend calculates distribution (investors + owner + commission)
  - Backend transfers tokens to investors based on their investment shares
  - Backend transfers tokens to owner
  - Event emitted: `FundsRedistributed`
  
- Database is updated: `isAuctionAccept = true`, `isRedistributed = true`

---

## ✅ Complete Workflow Summary

| Step | Actor | Action | MetaMask? | Blockchain |
|------|-------|--------|-----------|------------|
| 1 | Owner | Create project | ✅ (get address) | ✅ Contract created |
| 2 | Admin | Approve project | ❌ | ✅ `approveProject()` |
| 3 | Admin | Save estimations | ❌ | ✅ `saveEstimations()` |
| 4 | Restorer | Submit bid | ✅ (get address) | ❌ DB only |
| 5 | Admin | Select restorer | ❌ | ✅ `setRestorer()` |
| 6 | Investor | Fund project | ✅✅ (2 popups) | ✅ `acceptFunds()` |
| 7 | Admin | Assign restoration | ❌ | ✅ `assignForRestoration()` |
| 8 | Restorer | Finish restoration | ❌ | ❌ DB only |
| 9 | Admin | Open auction | ❌ | ✅ `setAuctionOpen()` |
| 10 | Buyer | Place bid | ✅✅ (2 popups) | ✅ `addNewAuctionBid()` |
| 11 | Admin | Set buyer & redistribute | ❌ | ✅ `setBuyer()` + `redistribute()` |

---

## 🎯 Key Demo Points

### ✅ **Highlight These Improvements:**

1. **"Notice: Admin operations don't show MetaMask popups!"**
   - Show approving a project - no popup
   - Show saving estimations - no popup
   - Show selecting restorer - no popup
   - Show assigning restoration - no popup
   - Show opening auction - no popup
   - Show setting buyer - no popup

2. **"All admin blockchain operations happen server-side"**
   - Open browser Network tab
   - Show API calls to `/api/blockchain/admin/*`
   - Explain these are REST API calls, not direct blockchain calls

3. **"Better synchronization"**
   - Blockchain and database updates happen together
   - No risk of them getting out of sync
   - Admin doesn't need to manually approve each transaction

4. **"Still user-friendly for other roles"**
   - Investors and buyers still use MetaMask (as expected)
   - Owner needs MetaMask only to get wallet address
   - Restorers need MetaMask only to get wallet address

---

## 🐛 Troubleshooting

### Error: "Ownable: caller is not the owner"
- **Solution:** Make sure factory ownership was transferred to admin wallet
- Run: `npx hardhat run scripts/transfer-factory-ownership.js --network localhost`

### Error: "funding is still in progress"
- **Solution:** Project must be fully funded before assigning restoration
- Use the funding script or have an investor fund the project

### Error: "Transaction reverted"
- **Solution:** Check previous steps were completed in order
- Verify contract addresses in `application.properties`
- Check Hardhat node is running

### Error: "401 Unauthorized"
- **Solution:** Make sure you're logged in as the correct user role
- Refresh the page and login again
- Check JWT token is valid

### Error: "No contract code found"
- **Solution:** Hardhat node was restarted (contracts are lost)
- Redeploy contracts: `npx hardhat run scripts/deploy-cc.js --network localhost`
- Create new project with fresh address

---

## 📝 Testing Checklist

Use this checklist to ensure everything works:

- [ ] Owner can create project (MetaMask popup for address)
- [ ] Admin can approve project (NO MetaMask popup)
- [ ] Admin can save estimations (NO MetaMask popup)
- [ ] Restorer can submit bid (MetaMask popup for address)
- [ ] Admin can select restorer (NO MetaMask popup)
- [ ] Investor can fund project (2 MetaMask popups)
- [ ] Admin can assign restoration (NO MetaMask popup)
- [ ] Restorer can finish restoration (no blockchain, DB only)
- [ ] Admin can open auction (NO MetaMask popup)
- [ ] Buyer can place bid (2 MetaMask popups)
- [ ] Admin can set buyer and redistribute (NO MetaMask popup)
- [ ] All values are correctly converted to wei
- [ ] All database states match blockchain states
- [ ] Project status flows correctly through all stages

---

## 🎬 Demo Script (Recommended Flow)

1. **Introduction:** "I'll demonstrate our blockchain integration where admin operations are handled server-side"

2. **Show Owner Flow:** Create a project (brief - show MetaMask popup for address)

3. **Highlight Admin Flow:** 
   - "Watch - when I approve this project, there's no MetaMask popup!"
   - Approve project → No popup ✅
   - "Same for saving estimations" → No popup ✅
   - "Same for selecting restorer" → No popup ✅
   - "This is because all admin operations are handled by our backend"

4. **Show Other Flows:** 
   - Investor funding (show MetaMask popups - this is expected)
   - Buyer bidding (show MetaMask popups - this is expected)

5. **Conclusion:** "This ensures perfect synchronization between our database and blockchain, eliminates admin workflow friction, and maintains user control for investors and buyers."

---

**Good luck with your demo! 🚀**

