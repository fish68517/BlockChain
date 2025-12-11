# Blockchain Backend Integration - Quick Reference

## Setup Instructions

### 1. Configure Properties
Add to `application.properties`:
```properties
blockchain.rpc-url=http://localhost:8545
blockchain.private-key=YOUR_PRIVATE_KEY (optional)
```

### 2. Build Project
```bash
mvn clean install
```

### 3. Run Application
```bash
mvn spring-boot:run
```

---

## API Endpoints Quick Reference

### Getting Started
```bash
# Check contract addresses
curl http://localhost:8080/api/blockchain/contract-addresses

# Get token balance
curl http://localhost:8080/api/blockchain/token-balance/0xYourAddress
```

### Project Workflow Example

**1. Create a Project**
```bash
curl -X POST http://localhost:8080/api/blockchain/create-project \
  -H "Content-Type: application/json" \
  -d '{
    "walletAddress": "0x...",
    "vin": "VIN123",
    "make": "Toyota",
    "model": "Camry",
    "ccpg": "1000",
    "fundingGoal": "5000"
  }'
```

**2. Approve Project**
```bash
curl -X POST http://localhost:8080/api/blockchain/approve-project \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x..."
  }'
```

**3. Approve Funding**
```bash
curl -X POST http://localhost:8080/api/blockchain/approve-funding \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "amount": "100"
  }'
```

**4. Invest in Project**
```bash
curl -X POST http://localhost:8080/api/blockchain/invest \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "amount": "100"
  }'
```

**5. Save Estimations**
```bash
curl -X POST http://localhost:8080/api/blockchain/save-estimations \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "valueEstimate": "50000",
    "repairEstimate": "10000"
  }'
```

**6. Set Restorer**
```bash
curl -X POST http://localhost:8080/api/blockchain/set-restorer \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "restorerAddress": "0x...",
    "fundingGoal": "10000"
  }'
```

**7. Open Auction**
```bash
curl -X POST http://localhost:8080/api/blockchain/open-auction \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x..."
  }'
```

**8. Place Bid**
```bash
curl -X POST http://localhost:8080/api/blockchain/bid \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "amount": "6000"
  }'
```

**9. Set Buyer (Select winning bidder)**
```bash
curl -X POST http://localhost:8080/api/blockchain/set-buyer \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x...",
    "buyerAddress": "0x..."
  }'
```

**10. Redistribute Funds**
```bash
curl -X POST http://localhost:8080/api/blockchain/redistribute \
  -H "Content-Type: application/json" \
  -d '{
    "projectAddress": "0x..."
  }'
```

---

## Common Request/Response Patterns

### Success Response
```json
{
  "message": "Operation completed",
  "transactionHash": "0x123abc..."
}
```

### Token Balance Response
```json
{
  "walletAddress": "0x...",
  "balance": "1234.567"
}
```

### Project Creation Response
```json
{
  "message": "Project created",
  "projectAddress": "0x..."
}
```

### Error Response
```json
{
  "error": "Descriptive error message"
}
```

---

## Contract Addresses

Located in: `src/main/resources/contracts/`

- **CCToken**: `0x60c5eb023F7778031F542ece23dff7165a515CC0`
- **CCProjectFactory**: `0xc2B45f7DfCf4Fb869133cC35BEdaA9c63D95Fda3`

---

## Java Integration Example

### Using Web3Utils in a Service

```java
@Service
public class BlockchainService {
    
    @Autowired
    private Web3Utils web3Utils;
    
    public String createProject(ProjectDTO projectDTO) throws Exception {
        return web3Utils.createCCProjectContract(
            projectDTO.getWalletAddress(),
            projectDTO.getVin(),
            projectDTO.getMake(),
            projectDTO.getModel(),
            projectDTO.getCcpg(),
            projectDTO.getFundingGoal()
        );
    }
    
    public String investInProject(String projectAddress, String amount) throws Exception {
        return web3Utils.investInCCProjectListing(projectAddress, amount);
    }
}
```

---

## Debugging Tips

### Check Logs
```bash
# If running with Maven
mvn spring-boot:run | grep blockchain

# If running as JAR
java -jar app.jar | grep blockchain
```

### Verify Connection
```bash
# Test RPC endpoint directly
curl http://localhost:8545 \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":1}'
```

### Validate Contract ABIs
- Check files in `src/main/resources/contracts/`
- Ensure JSON is valid
- Verify contract addresses match deployed contracts

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Check RPC URL in application.properties |
| Invalid contract address | Verify address format (0x...) and deployment |
| Transaction failed | Check account balance and nonce |
| JSON parsing error | Validate request JSON format |
| Web3Utils not found | Run `mvn clean install` to fetch dependencies |

---

## Environment Variables (Alternative)

```bash
export BLOCKCHAIN_RPC_URL=http://localhost:8545
export BLOCKCHAIN_PRIVATE_KEY=your_private_key
```

Then in application.properties:
```properties
blockchain.rpc-url=${BLOCKCHAIN_RPC_URL:http://localhost:8545}
blockchain.private-key=${BLOCKCHAIN_PRIVATE_KEY:}
```

---

## Additional Resources

- [Web3j Documentation](https://docs.web3j.io/)
- [Ethereum JSON-RPC API](https://ethereum.org/en/developers/docs/apis/json-rpc/)
- [Solidity Documentation](https://docs.soliditylang.org/)
- [Ganache for Local Testing](https://www.trufflesuite.com/ganache)

---

## Version Info

- Spring Boot: 2.2.10
- Web3j: 4.9.7
- Java: 1.8+
- Ethereum: Latest
