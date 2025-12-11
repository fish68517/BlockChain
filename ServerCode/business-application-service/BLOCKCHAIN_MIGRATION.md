# Blockchain Code Transfer Documentation

## Overview
The blockchain integration code has been successfully transferred from the frontend (JavaScript/Node.js) to the backend (Java/Spring Boot). This enables server-side blockchain operations for the CollectorCoin application.

## What Was Transferred

### 1. Web3 Utility Class (`Web3Utils.java`)
**Location:** `src/main/java/com/company/utils/Web3Utils.java`

Converted all JavaScript blockchain operations from `web3Utils.js` to Java. Includes:
- **Token Operations**: Get CC Token balance, approve funding
- **Project Management**: Create, update, approve, and manage projects
- **Auction Operations**: Open auctions, place bids, set buyers
- **Restoration Management**: Assign restorers, manage estimations
- **Fund Distribution**: Redistribute funds, handle investments

**Key Methods:**
```java
getCCTokenBalance(String walletAddress)
approveFunding(String projectAddress, String amount)
createCCProjectContract(String walletAddress, String vin, String make, String model, String ccpg, String fundingGoal)
approveCCProjectListing(String projectAddress)
saveEstCCProjectListing(String projectAddress, BigInteger valueEstimate, BigInteger repairEstimate)
setRestorerCCProjectListing(String projectAddress, String restorerAddress, String fundingGoal)
assignRestorationCCProjectListing(String projectAddress)
openAuctionCCProjectListing(String projectAddress)
setBuyerCCProjectListing(String projectAddress, String buyerAddress)
redistributeCCProjectListing(String projectAddress)
updateCCProjectListing(String projectAddress, String vin, String make, String model, String ccpg, String fundingGoal)
investInCCProjectListing(String projectAddress, String amount)
bidForCCProjectListing(String projectAddress, String amount)
```

### 2. Contract ABI Files
**Location:** `src/main/resources/contracts/`

Copied all smart contract ABI files:
- `CCToken.json` - ERC20 token contract ABI
- `CCProjectFactory.json` - Factory contract ABI for creating projects
- `CCProject.json` - Individual project contract ABI

These ABIs are used by web3j to interact with the smart contracts.

### 3. REST Controller (`BlockchainController.java`)
**Location:** `src/main/java/com/company/controllers/BlockchainController.java`

Provides REST endpoints for all blockchain operations:

**Base URL:** `/api/blockchain`

**Endpoints:**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/token-balance/{walletAddress}` | Get CC Token balance |
| POST | `/approve-funding` | Approve funding for project |
| POST | `/create-project` | Create new CC Project contract |
| POST | `/approve-project` | Approve project listing |
| POST | `/save-estimations` | Save project estimations |
| POST | `/set-restorer` | Assign restorer to project |
| POST | `/assign-restoration` | Assign project for restoration |
| POST | `/open-auction` | Open auction for project |
| POST | `/set-buyer` | Set buyer for project |
| POST | `/redistribute` | Redistribute project funds |
| PUT | `/update-project` | Update project details |
| POST | `/invest` | Invest in project |
| POST | `/bid` | Place auction bid |
| GET | `/contract-addresses` | Get deployed contract addresses |

### 4. Web3 Configuration (`Web3Config.java`)
**Location:** `src/main/java/com/company/config/Web3Config.java`

Spring configuration class that:
- Creates Web3Utils bean for dependency injection
- Supports configurable RPC URL and private key
- Manages blockchain connection settings

## Configuration

Add the following properties to `application.properties`:

```properties
# Blockchain Configuration
blockchain.rpc-url=http://localhost:8545
blockchain.private-key=<your-private-key>
```

Or use environment variables:
```bash
BLOCKCHAIN_RPC_URL=http://localhost:8545
BLOCKCHAIN_PRIVATE_KEY=<your-private-key>
```

## Dependencies Added

Updated `pom.xml` with web3j dependencies:
- `org.web3j:core:4.9.7` - Web3j core functionality
- `org.web3j:contracts:4.9.7` - Smart contract support
- `org.web3j:ens:4.9.7` - ENS (Ethereum Name Service) support
- `com.fasterxml.jackson.core:jackson-databind` - JSON processing

## Usage Examples

### Create a Project
```bash
POST /api/blockchain/create-project
{
  "walletAddress": "0x...",
  "vin": "VIN123456",
  "make": "Toyota",
  "model": "Camry",
  "ccpg": "1000",
  "fundingGoal": "5000"
}
```

### Approve Funding
```bash
POST /api/blockchain/approve-funding
{
  "projectAddress": "0x...",
  "amount": "100"
}
```

### Place a Bid
```bash
POST /api/blockchain/bid
{
  "projectAddress": "0x...",
  "amount": "500"
}
```

### Get Token Balance
```bash
GET /api/blockchain/token-balance/0x...
```

## Key Differences from Frontend

1. **Initialization**: Backend uses HttpService with RPC URL instead of window.ethereum (MetaMask)
2. **Transaction Management**: Uses web3j's RawTransactionManager for transaction handling
3. **Error Handling**: Server-side exceptions with detailed error messages
4. **Async Operations**: Java futures instead of JavaScript async/await
5. **Decimal Handling**: BigInteger for precise calculations instead of JavaScript number manipulation

## Migration Notes

- The frontend still maintains its original web3Utils.js for client-side operations
- Backend can now process blockchain operations without client-side MetaMask dependency
- Backend operations require a configured RPC endpoint and optional private key
- Consider implementing transaction signing on the backend or continue using MetaMask for client-side signing

## Security Considerations

1. **Never commit private keys** to version control
2. Use environment variables or secure vaults for sensitive data
3. Validate all input from API clients
4. Consider rate limiting on blockchain endpoints
5. Implement proper authentication/authorization for API endpoints

## Testing

To test the endpoints:

```bash
# Get contract addresses
curl http://localhost:8080/api/blockchain/contract-addresses

# Get token balance
curl http://localhost:8080/api/blockchain/token-balance/0x...

# Create project
curl -X POST http://localhost:8080/api/blockchain/create-project \
  -H "Content-Type: application/json" \
  -d '{...}'
```

## Future Improvements

1. Add transaction receipt polling to confirm transaction completion
2. Implement event listening for contract events
3. Add gas estimation before transactions
4. Implement transaction retry logic
5. Add caching for read-only operations
6. Consider implementing OpenZeppelin contracts SDK for type safety

## Support

For issues or questions about the blockchain integration, refer to:
- Web3j Documentation: https://docs.web3j.io/
- Ethereum JSON-RPC API: https://ethereum.org/en/developers/docs/apis/json-rpc/
- Smart Contract ABIs: Check the `contracts/` folder in resources
