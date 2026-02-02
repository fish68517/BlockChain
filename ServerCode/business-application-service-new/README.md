# CollectorCoin

A blockchain-based collectibles investment platform using dynamic NFTs (dNFT).

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2
- **Workflow**: JBPM 7.74
- **Blockchain**: Web3j, Solidity (ERC-1155)
- **Storage**: IPFS
- **Database**: MySQL

## Project Structure

```
collectorcoin/
├── server/          # Spring Boot backend
├── contracts/       # Solidity smart contracts
├── workflows/       # JBPM workflow definitions
└── docs/            # Documentation
```

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+

### Setup

1. Copy environment variables:
   ```bash
   cp .env.example .env
   ```

2. Install contract dependencies:
   ```bash
   cd contracts && npm install
   ```

3. Build server:
   ```bash
   cd server && mvn clean install
   ```

4. Run server:
   ```bash
   mvn spring-boot:run
   ```

## License

MIT
