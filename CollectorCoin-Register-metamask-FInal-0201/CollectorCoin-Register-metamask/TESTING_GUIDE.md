# Testing Guide for Blockchain Endpoints

## Prerequisites
- Server running on `http://localhost:8090`
- Hardhat node running on `http://127.0.0.1:8545`
- A project address from Step 1 (create-project)

---

## Step 1: Create Project ✅ (Already Tested)

**Endpoint:** `POST /api/blockchain/admin/create-project`

**Request Body:**
```json
{
  "vin": "1HGBH41JXMN109186",
  "make": "Honda",
  "model": "Civic",
  "ccpg": "1000000000000000000",
  "fundingGoal": "5000000000000000000",
  "ownerAddress": "0x70997970C51812dc3A010C7d01b50e0d17dc79C8"
}
```

**Response:** Project address (e.g., `0x5FbDB2315678afecb367f032d93F642f64180aa3`)

**Save this project address for next steps!**

---

## Step 2: Approve Project

**Endpoint:** `POST /api/blockchain/admin/approve-project`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/approve-project`  

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/approve-project`
3. **Params** tab:
   - Key: `projectAddress`
   - Value: `[YOUR_PROJECT_ADDRESS_FROM_STEP_1]`
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/approve-project?projectAddress=YOUR_PROJECT_ADDRESS"
```

**Expected Response:** `200 OK` (empty body)

---

## Step 3: Save Estimations

**Endpoint:** `POST /api/blockchain/admin/save-estimations`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/save-estimations`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/save-estimations`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
   - `valueEst`: `10000000000000000000` (10 tokens in wei)
   - `repairEst`: `5000000000000000000` (5 tokens in wei)
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/save-estimations?projectAddress=YOUR_PROJECT_ADDRESS&valueEst=10000000000000000000&repairEst=5000000000000000000"
```

**Expected Response:** `200 OK` (empty body)

**Note:** Values are in wei (18 decimals). Multiply token amount by 10^18.

---

## Step 4: Set Restorer

**Endpoint:** `POST /api/blockchain/admin/set-restorer`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/set-restorer`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/set-restorer`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
   - `restorerAddress`: `0x8626f6940E2eb28930eFb4CeF49B2d1F2C9C1199` (use a Hardhat account address)
   - `fundingGoal`: `5000000000000000000` (5 tokens in wei)
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/set-restorer?projectAddress=YOUR_PROJECT_ADDRESS&restorerAddress=0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC&fundingGoal=5000000000000000000"
```

**Expected Response:** `200 OK` (empty body)

**Note:** Use a valid Ethereum address for `restorerAddress` (can be any Hardhat account).

---

## Step 4.5: Fund the Project (REQUIRED before Step 5)

**⚠️ IMPORTANT:** Before you can call `assign-restoration`, the project must be fully funded!

The `assignForRestoration()` function requires:
- `fundingLive = false` (funding must be complete)
- `fundingGoal == fundsDeposited` (full funding must be reached)

After `setRestorer()` is called, `fundingLive` becomes `true`. You need to fund the project to reach the funding goal, which will set `fundingLive = false`.

### Option A: Use Hardhat Script (Recommended for Testing)

```bash
cd ServerCode/local-hardhat
PROJECT_ADDRESS=0xcafac3dd18ac6c6e92c921884f9e4176737c052c FUNDING_GOAL=5000000000000000000 npx hardhat run scripts/fund-project.js --network localhost
```

Replace:
- `YOUR_PROJECT_ADDRESS` with your project address
- `FUNDING_GOAL` with the funding goal you set in `set-restorer` (should match)

### Option B: Use Backend Endpoint (if you have one)

If you have an endpoint to call `acceptFunds` on the project contract, use that to fund the project.

### Option C: Manual Funding via Smart Contract

You would need to:
1. Get tokens (mint from CCToken contract if needed)
2. Approve the project to spend your tokens
3. Call `acceptFunds(amount)` on the project contract

---

## Step 5: Assign Restoration

**Endpoint:** `POST /api/blockchain/admin/assign-restoration`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/assign-restoration`

**⚠️ Prerequisites:** Project must be fully funded (see Step 4.5)

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/assign-restoration`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/assign-restoration?projectAddress=YOUR_PROJECT_ADDRESS"
```

**Expected Response:** `200 OK` (empty body)

**If you get "funding is still in progress" error:**
- Make sure you completed Step 4.5 (fund the project)
- Verify funding goal was reached

---

## Step 6: Open Auction

**Endpoint:** `POST /api/blockchain/admin/open-auction`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/open-auction`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/open-auction`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/open-auction?projectAddress=YOUR_PROJECT_ADDRESS"
```

**Expected Response:** `200 OK` (empty body)

---

## Step 7: Set Buyer

**Endpoint:** `POST /api/blockchain/admin/set-buyer`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/set-buyer`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/set-buyer`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
   - `buyerAddress`: `0xdD2FD4581271e230360230F9337D5c0430Bf44C0` (use a Hardhat account address)
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/set-buyer?projectAddress=YOUR_PROJECT_ADDRESS&buyerAddress=0x90F79bf6EB2c4f870365E785982E1f101E93b906"
```

**Expected Response:** `200 OK` (empty body)

**Note:** Use a valid Ethereum address for `buyerAddress` (can be any Hardhat account).

---

## Step 8: Redistribute

**Endpoint:** `POST /api/blockchain/admin/redistribute`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/redistribute`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/redistribute`
3. **Params** tab:
   - `projectAddress`: `[YOUR_PROJECT_ADDRESS]`
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/redistribute?projectAddress=YOUR_PROJECT_ADDRESS"
```

**Expected Response:** `200 OK` (empty body)

---

## Step 9: Edit Project Details

**Endpoint:** `POST /api/blockchain/admin/edit-project`

**Method:** POST  
**URL:** `http://localhost:8090/api/blockchain/admin/edit-project`

### Postman:
1. Method: **POST**
2. URL: `http://localhost:8090/api/blockchain/admin/edit-project`
3. **Body** tab:
   - Select **raw** and **JSON**
   - Enter:
   ```json
   {
     "projectAddress": "YOUR_PROJECT_ADDRESS",
     "vin": "1HGBH41JXMN109186",
     "make": "Toyota",
     "model": "Camry",
     "ccpg": "2000000000000000000",
     "fundingGoal": "6000000000000000000"
   }
   ```
4. Click **Send**

### cURL:
```bash
curl -X POST "http://localhost:8090/api/blockchain/admin/edit-project" \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "YOUR_PROJECT_ADDRESS",
    "vin": "1HGBH41JXMN109186",
    "make": "Toyota",
    "model": "Camry",
    "ccpg": "2000000000000000000",
    "fundingGoal": "6000000000000000000"
  }'
```

**Expected Response:** `200 OK` (empty body)

**Note:** This is the only endpoint that uses JSON body (like create-project).

---

## Step 10: Get Token Balance (Query Endpoint)

**Endpoint:** `GET /api/blockchain/balance/{address}`

**Method:** GET  
**URL:** `http://localhost:8090/api/blockchain/balance/{address}`

### Postman:
1. Method: **GET**
2. URL: `http://localhost:8090/api/blockchain/balance/0x70997970C51812dc3A010C7d01b50e0d17dc79C8`
   - Replace the address with any Ethereum address
3. Click **Send**

### cURL:
```bash
curl -X GET "http://localhost:8090/api/blockchain/balance/0x70997970C51812dc3A010C7d01b50e0d17dc79C8"
```

**Expected Response:** `200 OK` with balance in wei (e.g., `1000000000000000000`)

**Note:** This is a read-only query, doesn't modify blockchain state.

---

## Complete Workflow Test Sequence

Test endpoints in this order (each depends on previous steps):

1. ✅ **Create Project** (already tested)
2. **Approve Project** (Step 2)
3. **Save Estimations** (Step 3)
4. **Set Restorer** (Step 4)
5. **Assign Restoration** (Step 5)
6. **Open Auction** (Step 6)
7. **Set Buyer** (Step 7)
8. **Redistribute** (Step 8)
9. **Edit Project** (Step 9) - Can be done at any time
10. **Get Balance** (Step 10) - Can be done at any time

---

## Hardhat Test Accounts (for addresses)

When you need addresses for testing, use these Hardhat default accounts:

```
Account #0: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
Account #1: 0x70997970C51812dc3A010C7d01b50e0d17dc79C8
Account #2: 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC
Account #3: 0x90F79bf6EB2c4f870365E785982E1f101E93b906
Account #4: 0x15d34AAf54267DB7D7c367839AAf71A00a2C6A65
```

---

## Common Issues & Solutions

### Issue 1: Transaction Reverted
**Error:** `Transaction reverted`
**Solution:** 
- Check if previous steps were completed
- Verify contract addresses in `application.properties` match deployed contracts
- Ensure Hardhat node is running

### Issue 2: Invalid Address Format
**Error:** `Invalid address`
**Solution:** 
- Ensure addresses start with `0x` and are 42 characters long
- Use valid Ethereum addresses (Hardhat accounts)

### Issue 3: Number Format Error
**Error:** `Invalid number format`
**Solution:** 
- Use string numbers in wei (18 decimals)
- Example: `"1000000000000000000"` for 1 token

### Issue 4: Connection Refused
**Error:** `Connection refused`
**Solution:** 
- Ensure server is running on port 8090
- Ensure Hardhat node is running on port 8545
- Check `blockchain.network.url` in `application.properties`

---

## Quick Test Script

Save this as `test-all-endpoints.sh`:

```bash
#!/bin/bash

# Set your project address here (from create-project)
PROJECT_ADDRESS="YOUR_PROJECT_ADDRESS_HERE"
BASE_URL="http://localhost:8090/api/blockchain"

echo "Testing Blockchain Endpoints..."
echo "================================"

echo -e "\n1. Approve Project..."
curl -X POST "${BASE_URL}/admin/approve-project?projectAddress=${PROJECT_ADDRESS}"

echo -e "\n\n2. Save Estimations..."
curl -X POST "${BASE_URL}/admin/save-estimations?projectAddress=${PROJECT_ADDRESS}&valueEst=10000000000000000000&repairEst=5000000000000000000"

echo -e "\n\n3. Set Restorer..."
curl -X POST "${BASE_URL}/admin/set-restorer?projectAddress=${PROJECT_ADDRESS}&restorerAddress=0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC&fundingGoal=5000000000000000000"

echo -e "\n\n4. Assign Restoration..."
curl -X POST "${BASE_URL}/admin/assign-restoration?projectAddress=${PROJECT_ADDRESS}"

echo -e "\n\n5. Open Auction..."
curl -X POST "${BASE_URL}/admin/open-auction?projectAddress=${PROJECT_ADDRESS}"

echo -e "\n\n6. Set Buyer..."
curl -X POST "${BASE_URL}/admin/set-buyer?projectAddress=${PROJECT_ADDRESS}&buyerAddress=0x90F79bf6EB2c4f870365E785982E1f101E93b906"

echo -e "\n\n7. Redistribute..."
curl -X POST "${BASE_URL}/admin/redistribute?projectAddress=${PROJECT_ADDRESS}"

echo -e "\n\n8. Get Balance..."
curl -X GET "${BASE_URL}/balance/0x70997970C51812dc3A010C7d01b50e0d17dc79C8"

echo -e "\n\nDone!"
```

**Usage:**
```bash
chmod +x test-all-endpoints.sh
# Edit the PROJECT_ADDRESS variable first!
./test-all-endpoints.sh
```

