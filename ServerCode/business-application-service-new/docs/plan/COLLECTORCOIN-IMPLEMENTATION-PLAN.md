# CollectorCoin 区块链项目实施规划文档

> **项目类型**: 毕业设计
> **文档版本**: v1.0
> **创建日期**: 2026-01-28
> **状态**: 待评审

---

## 1. 背景与目标

### 1.1 业务背景

CollectorCoin 是一个基于区块链技术的收藏品投资平台，将实体收藏品（如经典汽车）的修复项目代币化为动态 NFT（dNFT），实现去中心化的投资、修复和拍卖全流程管理。

### 1.2 项目目标

| 目标项 | 描述 | 验收标准 |
|--------|------|----------|
| NFT 代币化 | 实现收藏品项目的 dNFT 代币化 | 成功铸造 dNFT 并返回 tokenId |
| 生命周期管理 | 构建完整的投资-修复-拍卖生命周期 | 端到端流程可演示跑通 |
| 工作流自动化 | 通过 JBPM 实现业务流程自动化 | Human Task 可被 TaskService 完成 |
| 交易透明性 | 通过智能合约保证交易透明不可篡改 | 所有事件正确触发并可被监听 |

### 1.3 上线标准

- [ ] 端到端演示流程完整跑通（约 15-20 分钟）
- [ ] 所有智能合约函数通过单元测试
- [ ] JBPM 工作流从 New Listing 到 Auction Review 完整流转
- [ ] 事件监听延迟 < 30 秒
- [ ] NFT 元数据符合 OpenSea 标准

---

## 2. 现状

### 2.1 项目结构（规划）

```
collectorcoin/
├── server/                              # Spring Boot 后端
│   └── src/main/java/com/collectorcoin/
│       ├── config/
│       │   └── Web3jConfig.java         # Web3j 配置类
│       ├── service/
│       │   ├── BlockchainService.java   # 区块链服务接口（需扩展）
│       │   ├── BlockchainServiceImpl.java # 区块链服务实现（需扩展）
│       │   ├── BlockchainEventListenerService.java # 【新建】事件监听
│       │   ├── BlockchainProxyService.java # 【新建】代理服务
│       │   ├── InvestmentProxyService.java # 【新建】投资代理
│       │   ├── NFTMetadataService.java  # 【新建】元数据服务
│       │   └── IPFSService.java         # 【新建】IPFS 服务
│       ├── model/
│       │   ├── ProjectListing.java      # 项目实体（需扩展）
│       │   ├── NFTMetadata.java         # 【新建】NFT 元数据模型
│       │   └── Attribute.java           # 【新建】属性模型
│       └── repository/
│           └── ProjectListingRepository.java # 项目仓库
├── contracts/                           # Solidity 智能合约
│   ├── CCToken.sol                      # 代币合约
│   ├── Factory.sol                      # 工厂合约
│   ├── ProjectToken.sol                 # 项目代币合约
│   └── CCMarketPlace.sol                # 市场合约（需重大扩展）
├── workflows/                           # JBPM 工作流
│   └── NewListing.bpmn                  # 新项目工作流（需修改）
└── resources/
    └── application.properties           # 配置文件（需扩展）
```

### 2.2 现有核心文件（待创建/扩展）

| 文件路径 | 状态 | 说明 |
|----------|------|------|
| `server/.../BlockchainService.java` | 需扩展 | 添加 7 个 NFT 相关方法 |
| `server/.../BlockchainServiceImpl.java` | 需扩展 | 实现新增接口方法 |
| `server/.../ProjectListing.java` | 需扩展 | 添加 4 个 NFT 字段 |
| `contracts/CCMarketPlace.sol` | 需重大扩展 | 添加 dNFT 全生命周期管理 |
| `workflows/NewListing.bpmn` | 需修改 | 添加 6 个 Human Task 节点 |
| `resources/application.properties` | 需扩展 | 添加区块链/IPFS 配置 |

---

## 3. 缺口

### 3.1 智能合约层缺口

- [ ] 缺少 `mintDNFT()` 函数 - 铸造动态 NFT
- [ ] 缺少 `launchDNFT()` 函数 - 上架 NFT 到市场
- [ ] 缺少 `addInvestor()` 函数 - 记录投资者信息
- [ ] 缺少 `updateRestorer()` 函数 - 更新修复者
- [ ] 缺少 `markRestorationComplete()` 函数 - 标记修复完成
- [ ] 缺少 `setItemForSale()` 函数 - 设置拍卖价格并解除暂停
- [ ] 缺少 `transferToWinner()` 函数 - 转移 NFT 给买家
- [ ] 缺少 8 个链上事件定义

### 3.2 JBPM 工作流层缺口

- [ ] 缺少 `WaitForNFTMinting` Human Task 节点
- [ ] 缺少 `WaitForInvestmentConfirm` Human Task 节点
- [ ] 缺少 `WaitForRestorationAssign` Human Task 节点
- [ ] 缺少 `WaitForRestorationComplete` Human Task 节点
- [ ] 缺少 `WaitForItemPost` Human Task 节点
- [ ] 缺少 `WaitForNFTTransfer` Human Task 节点
- [ ] Bid Review 节点位置需调整至 Funding 之前

### 3.3 Java 后端服务层缺口

- [ ] 缺少 `BlockchainEventListenerService` - 链上事件监听服务
- [ ] 缺少 `BlockchainProxyService` - Admin 钱包代理服务
- [ ] 缺少 `InvestmentProxyService` - 投资代理服务
- [ ] 缺少 `NFTMetadataService` - NFT 元数据管理服务
- [ ] 缺少 `IPFSService` - IPFS 交互服务
- [ ] `BlockchainService` 接口缺少 7 个 NFT 相关方法
- [ ] `ProjectListing` 实体缺少 4 个 NFT 字段

### 3.4 配置层缺口

- [ ] 缺少区块链节点 URL 配置
- [ ] 缺少智能合约地址配置
- [ ] 缺少 Admin 钱包私钥配置
- [ ] 缺少 IPFS API/Gateway 配置
- [ ] 缺少 NFT 元数据基础配置

---

## 4. 设计与对接方案

### 4.1 智能合约设计

#### 4.1.1 CCMarketPlace.sol 扩展

```solidity
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/security/Pausable.sol";

contract CCMarketPlace is ERC1155, Ownable, Pausable {

    // ============ State Variables ============
    uint256 private _tokenIdCounter;

    struct DNFTData {
        address projectAddress;
        uint256 valueEstimation;
        uint256 repairEstimation;
        string metadataURI;
        uint8 status;           // 0=Created, 1=Launched, 2=Funded, 3=Restoring, 4=Completed, 5=ForSale, 6=Sold
        address restorer;
        uint256 auctionPrice;
        bool transferable;
        address[] investors;
        mapping(address => uint256) investorAmounts;
    }

    mapping(uint256 => DNFTData) private _dnftData;

    // ============ Events ============
    event DNFTCreated(uint256 indexed tokenId, address indexed projectAddress, address admin);
    event DNFTLaunched(uint256 indexed tokenId);
    event InvestorAdded(uint256 indexed tokenId, address investor, uint256 amount);
    event RestorerUpdated(uint256 indexed tokenId, address restorer);
    event RestorationCompleted(uint256 indexed tokenId);
    event ItemPosted(uint256 indexed tokenId, uint256 price);
    event NFTTransferred(uint256 indexed tokenId, address winner);
    event MetadataUpdated(uint256 indexed tokenId, string metadataURI);

    // ============ Core Functions ============

    /**
     * @dev Mint a new dNFT for a project
     * @param projectAddress The address of the project
     * @param valueEst Value estimation in wei
     * @param repairEst Repair cost estimation in wei
     * @param metadataURI IPFS URI for metadata
     * @return tokenId The ID of the newly minted token
     */
    function mintDNFT(
        address projectAddress,
        uint256 valueEst,
        uint256 repairEst,
        string memory metadataURI
    ) external onlyOwner returns (uint256) {
        uint256 tokenId = _tokenIdCounter++;

        DNFTData storage data = _dnftData[tokenId];
        data.projectAddress = projectAddress;
        data.valueEstimation = valueEst;
        data.repairEstimation = repairEst;
        data.metadataURI = metadataURI;
        data.status = 0; // Created
        data.transferable = false;

        _mint(msg.sender, tokenId, 1, "");

        emit DNFTCreated(tokenId, projectAddress, msg.sender);
        return tokenId;
    }

    /**
     * @dev Launch dNFT in marketplace
     */
    function launchDNFT(uint256 tokenId) external onlyOwner {
        require(_dnftData[tokenId].status == 0, "Invalid status");
        _dnftData[tokenId].status = 1; // Launched
        emit DNFTLaunched(tokenId);
    }

    /**
     * @dev Add investor to dNFT
     */
    function addInvestor(
        uint256 tokenId,
        address investor,
        uint256 amount
    ) external onlyOwner {
        DNFTData storage data = _dnftData[tokenId];
        require(data.status == 1 || data.status == 2, "Not in funding phase");

        if (data.investorAmounts[investor] == 0) {
            data.investors.push(investor);
        }
        data.investorAmounts[investor] += amount;
        data.status = 2; // Funded

        emit InvestorAdded(tokenId, investor, amount);
    }

    /**
     * @dev Update restorer address
     */
    function updateRestorer(uint256 tokenId, address restorer) external onlyOwner {
        require(_dnftData[tokenId].status == 2, "Not funded yet");
        _dnftData[tokenId].restorer = restorer;
        _dnftData[tokenId].status = 3; // Restoring
        emit RestorerUpdated(tokenId, restorer);
    }

    /**
     * @dev Mark restoration as complete
     */
    function markRestorationComplete(uint256 tokenId) external onlyOwner {
        require(_dnftData[tokenId].status == 3, "Not in restoration");
        _dnftData[tokenId].status = 4; // Completed
        emit RestorationCompleted(tokenId);
    }

    /**
     * @dev Set item for sale with price and enable transfer
     */
    function setItemForSale(uint256 tokenId, uint256 price) external onlyOwner {
        require(_dnftData[tokenId].status == 4, "Restoration not complete");
        _dnftData[tokenId].auctionPrice = price;
        _dnftData[tokenId].transferable = true;
        _dnftData[tokenId].status = 5; // ForSale
        emit ItemPosted(tokenId, price);
    }

    /**
     * @dev Transfer NFT to auction winner
     */
    function transferToWinner(uint256 tokenId, address winner) external onlyOwner {
        require(_dnftData[tokenId].status == 5, "Not for sale");
        require(_dnftData[tokenId].transferable, "Transfer not allowed");

        address currentOwner = owner();
        _safeTransferFrom(currentOwner, winner, tokenId, 1, "");
        _dnftData[tokenId].status = 6; // Sold
        _dnftData[tokenId].transferable = false;

        emit NFTTransferred(tokenId, winner);
    }

    /**
     * @dev Update metadata URI
     */
    function updateMetadataURI(uint256 tokenId, string memory newURI) external onlyOwner {
        _dnftData[tokenId].metadataURI = newURI;
        emit MetadataUpdated(tokenId, newURI);
    }

    // ============ View Functions ============

    function getDNFTData(uint256 tokenId) external view returns (
        address projectAddress,
        uint256 valueEstimation,
        uint256 repairEstimation,
        string memory metadataURI,
        uint8 status,
        address restorer,
        uint256 auctionPrice,
        bool transferable
    ) {
        DNFTData storage data = _dnftData[tokenId];
        return (
            data.projectAddress,
            data.valueEstimation,
            data.repairEstimation,
            data.metadataURI,
            data.status,
            data.restorer,
            data.auctionPrice,
            data.transferable
        );
    }

    function getInvestors(uint256 tokenId) external view returns (address[] memory) {
        return _dnftData[tokenId].investors;
    }

    function getInvestorAmount(uint256 tokenId, address investor) external view returns (uint256) {
        return _dnftData[tokenId].investorAmounts[investor];
    }
}
```

### 4.2 Java 后端服务设计

#### 4.2.1 BlockchainService 接口扩展

**文件路径**: `server/src/main/java/com/collectorcoin/service/BlockchainService.java`

```java
public interface BlockchainService {

    // ============ Existing Methods ============
    // ... (保留现有方法)

    // ============ New NFT Methods ============

    /**
     * Mint a new dNFT for a project
     * @param projectAddress Project contract address
     * @param valueEst Value estimation in wei
     * @param repairEst Repair cost estimation in wei
     * @param metadataURI IPFS metadata URI
     * @return tokenId The minted token ID
     */
    Long mintDNFT(String projectAddress, BigInteger valueEst,
                  BigInteger repairEst, String metadataURI);

    /**
     * Launch dNFT in marketplace
     * @param tokenId The token ID to launch
     */
    void launchDNFT(Long tokenId);

    /**
     * Add investor information to NFT
     * @param tokenId The token ID
     * @param investorAddress Investor wallet address
     * @param amount Investment amount in wei
     */
    void addNFTInvestor(Long tokenId, String investorAddress, BigInteger amount);

    /**
     * Update NFT status
     * @param tokenId The token ID
     * @param status New status code (0-6)
     */
    void updateNFTStatus(Long tokenId, int status);

    /**
     * Update restorer address
     * @param tokenId The token ID
     * @param restorerAddress Restorer wallet address
     */
    void updateNFTRestorer(Long tokenId, String restorerAddress);

    /**
     * Set NFT for auction with price
     * @param tokenId The token ID
     * @param auctionPrice Auction price in wei
     */
    void setNFTAuctionLive(Long tokenId, BigInteger auctionPrice);

    /**
     * Update NFT metadata URI
     * @param tokenId The token ID
     * @param newURI New IPFS metadata URI
     */
    void updateNFTMetadataURI(Long tokenId, String newURI);

    /**
     * Transfer NFT to auction winner
     * @param tokenId The token ID
     * @param winnerAddress Winner wallet address
     */
    void transferNFTToWinner(Long tokenId, String winnerAddress);
}
```

#### 4.2.2 BlockchainProxyService（Admin 钱包代理）

**文件路径**: `server/src/main/java/com/collectorcoin/service/BlockchainProxyService.java`

```java
@Service
public class BlockchainProxyService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainProxyService.class);

    @Value("${blockchain.admin.private-key}")
    private String adminPrivateKey;

    @Value("${blockchain.node.url}")
    private String nodeUrl;

    @Value("${blockchain.contract.marketplace.address}")
    private String marketplaceAddress;

    private Web3j web3j;
    private Credentials adminCredentials;

    @PostConstruct
    public void init() {
        web3j = Web3j.build(new HttpService(nodeUrl));
        adminCredentials = Credentials.create(adminPrivateKey);
        logger.info("BlockchainProxyService initialized with admin address: {}",
            adminCredentials.getAddress());
    }

    /**
     * Execute contract function as admin (admin pays gas)
     */
    public TransactionReceipt executeAsAdmin(Function function) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger nonce = web3j.ethGetTransactionCount(
            adminCredentials.getAddress(), DefaultBlockParameterName.LATEST)
            .send().getTransactionCount();

        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(500000);

        RawTransaction rawTx = RawTransaction.createTransaction(
            nonce, gasPrice, gasLimit, marketplaceAddress, encodedFunction);

        byte[] signedTx = TransactionEncoder.signMessage(rawTx, adminCredentials);
        String hexValue = Numeric.toHexString(signedTx);

        EthSendTransaction ethSendTx = web3j.ethSendRawTransaction(hexValue).send();

        if (ethSendTx.hasError()) {
            throw new RuntimeException("Transaction failed: " + ethSendTx.getError().getMessage());
        }

        String txHash = ethSendTx.getTransactionHash();
        logger.info("Transaction sent: {}", txHash);

        // Wait for receipt
        TransactionReceipt receipt = waitForReceipt(txHash);
        logger.info("Transaction confirmed in block: {}", receipt.getBlockNumber());

        return receipt;
    }

    private TransactionReceipt waitForReceipt(String txHash) throws Exception {
        int attempts = 0;
        int maxAttempts = 40; // ~2 minutes with 3s interval

        while (attempts < maxAttempts) {
            EthGetTransactionReceipt receiptResponse =
                web3j.ethGetTransactionReceipt(txHash).send();

            if (receiptResponse.getTransactionReceipt().isPresent()) {
                return receiptResponse.getTransactionReceipt().get();
            }

            Thread.sleep(3000);
            attempts++;
        }

        throw new RuntimeException("Transaction receipt not found after " + maxAttempts + " attempts");
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public String getMarketplaceAddress() {
        return marketplaceAddress;
    }
}
```

#### 4.2.3 BlockchainEventListenerService（事件监听）

**文件路径**: `server/src/main/java/com/collectorcoin/service/BlockchainEventListenerService.java`

```java
@Service
public class BlockchainEventListenerService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainEventListenerService.class);

    @Autowired
    private Web3j web3j;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectListingRepository projectListingRepository;

    @Value("${blockchain.contract.marketplace.address}")
    private String marketplaceAddress;

    private Disposable subscription;

    @PostConstruct
    public void startListening() {
        logger.info("Starting blockchain event listener...");

        EthFilter filter = new EthFilter(
            DefaultBlockParameterName.LATEST,
            DefaultBlockParameterName.LATEST,
            marketplaceAddress
        );

        subscription = web3j.ethLogFlowable(filter)
            .subscribe(
                this::handleLog,
                error -> {
                    logger.error("Event listener error: {}", error.getMessage());
                    reconnect();
                }
            );

        logger.info("Blockchain event listener started");
    }

    private void handleLog(Log log) {
        String eventSignature = log.getTopics().get(0);

        // Route to appropriate handler based on event signature
        if (eventSignature.equals(DNFT_CREATED_SIGNATURE)) {
            handleDNFTCreated(log);
        } else if (eventSignature.equals(INVESTOR_ADDED_SIGNATURE)) {
            handleInvestorAdded(log);
        } else if (eventSignature.equals(RESTORER_UPDATED_SIGNATURE)) {
            handleRestorerUpdated(log);
        } else if (eventSignature.equals(RESTORATION_COMPLETED_SIGNATURE)) {
            handleRestorationCompleted(log);
        } else if (eventSignature.equals(ITEM_POSTED_SIGNATURE)) {
            handleItemPosted(log);
        } else if (eventSignature.equals(NFT_TRANSFERRED_SIGNATURE)) {
            handleNFTTransferred(log);
        }
    }

    private void handleDNFTCreated(Log log) {
        Long tokenId = extractTokenId(log);
        String projectAddress = extractProjectAddress(log);

        ProjectListing listing = projectListingRepository.findByProjectAddress(projectAddress);
        if (listing != null) {
            listing.setNftTokenId(tokenId);
            listing.setNftMinted(true);
            projectListingRepository.save(listing);

            completeWaitTask(listing.getProcessId(), "WaitForNFTMinting",
                Map.of("tokenId", tokenId));
        }
    }

    /**
     * Complete a waiting Human Task
     */
    private void completeWaitTask(Long processInstanceId, String taskName, Map<String, Object> results) {
        try {
            List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(
                processInstanceId,
                Arrays.asList(Status.Ready, Status.Reserved),
                "en-UK"
            );

            TaskSummary task = tasks.stream()
                .filter(t -> t.getName().equals(taskName))
                .findFirst()
                .orElse(null);

            if (task != null) {
                taskService.claim(task.getId(), "system");
                taskService.start(task.getId(), "system");
                taskService.complete(task.getId(), "system", results);
                logger.info("Completed task {} for process {}", taskName, processInstanceId);
            } else {
                logger.warn("Task {} not found for process {}", taskName, processInstanceId);
            }
        } catch (Exception e) {
            logger.error("Failed to complete task {}: {}", taskName, e.getMessage());
        }
    }

    private void reconnect() {
        logger.info("Attempting to reconnect event listener...");
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        try {
            Thread.sleep(5000);
            startListening();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void stopListening() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            logger.info("Blockchain event listener stopped");
        }
    }
}
```

#### 4.2.4 NFTMetadataService

**文件路径**: `server/src/main/java/com/collectorcoin/service/NFTMetadataService.java`

```java
@Service
public class NFTMetadataService {

    @Autowired
    private IPFSService ipfsService;

    @Value("${nft.metadata.base.image.url}")
    private String baseImageUrl;

    @Value("${nft.metadata.external.base.url}")
    private String externalBaseUrl;

    public NFTMetadata createMetadata(ProjectListing listing) {
        NFTMetadata metadata = new NFTMetadata();
        metadata.setName("CollectorCoin dNFT #" + listing.getId());
        metadata.setDescription(listing.getDescription());
        metadata.setImage(baseImageUrl);
        metadata.setExternalUrl(externalBaseUrl + listing.getId());

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("VIN", listing.getVin()));
        attributes.add(new Attribute("Status", "CREATED"));
        attributes.add(Attribute.withNumber("Value Estimation", listing.getValueEstimation()));
        attributes.add(new Attribute("Investor Count", "0"));

        metadata.setAttributes(attributes);
        return metadata;
    }

    public String uploadToIPFS(NFTMetadata metadata) throws Exception {
        String json = new ObjectMapper().writeValueAsString(metadata);
        return ipfsService.upload(json);
    }
}
```

#### 4.2.5 IPFSService

**文件路径**: `server/src/main/java/com/collectorcoin/service/IPFSService.java`

```java
@Service
public class IPFSService {

    private static final Logger logger = LoggerFactory.getLogger(IPFSService.class);

    @Value("${ipfs.api.url}")
    private String ipfsApiUrl;

    @Value("${ipfs.gateway.url}")
    private String ipfsGatewayUrl;

    @Value("${ipfs.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate = new RestTemplate();

    public String upload(String content) throws Exception {
        if (!enabled) {
            logger.warn("IPFS disabled, returning mock URI");
            return "ipfs://mock-hash";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(content.getBytes()) {
            @Override
            public String getFilename() {
                return "metadata.json";
            }
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            ipfsApiUrl + "/add", request, Map.class);

        String hash = (String) response.getBody().get("Hash");
        logger.info("Uploaded to IPFS: {}", hash);
        return "ipfs://" + hash;
    }

    public String fetch(String ipfsUri) {
        String hash = ipfsUri.replace("ipfs://", "");
        return restTemplate.getForObject(ipfsGatewayUrl + hash, String.class);
    }
}
```

#### 4.2.6 数据模型

**NFTMetadata.java** - `server/src/main/java/com/collectorcoin/model/NFTMetadata.java`

```java
@Data
public class NFTMetadata {
    private String name;
    private String description;
    private String image;
    @JsonProperty("external_url")
    private String externalUrl;
    private List<Attribute> attributes;
    private List<String> investors;
    private List<String> history;
}
```

**Attribute.java** - `server/src/main/java/com/collectorcoin/model/Attribute.java`

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    @JsonProperty("trait_type")
    private String traitType;
    private String value;
    @JsonProperty("display_type")
    private String displayType;

    public Attribute(String traitType, String value) {
        this.traitType = traitType;
        this.value = value;
    }

    public static Attribute withNumber(String traitType, Object value) {
        Attribute attr = new Attribute();
        attr.setTraitType(traitType);
        attr.setValue(String.valueOf(value));
        attr.setDisplayType("number");
        return attr;
    }
}
```

#### 4.2.7 ProjectListing 实体扩展

**文件路径**: `server/src/main/java/com/collectorcoin/model/ProjectListing.java`

```java
@Entity
@Table(name = "project_listing")
public class ProjectListing {

    // ============ Existing Fields ============
    // ... (保留现有字段)

    // ============ New NFT Fields ============

    @Column(name = "nft_token_id")
    private Long nftTokenId;

    @Column(name = "nft_metadata_uri")
    private String nftMetadataUri;

    @Column(name = "nft_minted")
    private Boolean nftMinted = false;

    @Column(name = "nft_launched")
    private Boolean nftLaunched = false;

    // Getters and Setters
    public Long getNftTokenId() { return nftTokenId; }
    public void setNftTokenId(Long nftTokenId) { this.nftTokenId = nftTokenId; }

    public String getNftMetadataUri() { return nftMetadataUri; }
    public void setNftMetadataUri(String nftMetadataUri) { this.nftMetadataUri = nftMetadataUri; }

    public Boolean getNftMinted() { return nftMinted; }
    public void setNftMinted(Boolean nftMinted) { this.nftMinted = nftMinted; }

    public Boolean getNftLaunched() { return nftLaunched; }
    public void setNftLaunched(Boolean nftLaunched) { this.nftLaunched = nftLaunched; }
}
```

### 4.3 配置文件设计

**文件路径**: `server/src/main/resources/application.properties`

```properties
# ============ Blockchain Configuration ============
blockchain.node.url=https://sepolia.infura.io/v3/${INFURA_API_KEY}
blockchain.contract.marketplace.address=${MARKETPLACE_CONTRACT_ADDRESS}
blockchain.admin.private-key=${ADMIN_WALLET_PRIVATE_KEY}
blockchain.chain.id=11155111

# ============ IPFS Configuration ============
ipfs.api.url=http://localhost:5001/api/v0
ipfs.gateway.url=https://ipfs.io/ipfs/
ipfs.enabled=true

# ============ NFT Metadata Configuration ============
nft.metadata.base.image.url=ipfs://QmDefaultImageHash/default.jpg
nft.metadata.external.base.url=https://collectorcoin.com/listing/

# ============ Event Listener Configuration ============
blockchain.event.listener.retry.max=3
blockchain.event.listener.retry.delay=5000
```

**环境变量说明**:

| 变量名 | 必填 | 说明 | 读取位置 |
|--------|------|------|----------|
| `INFURA_API_KEY` | 是 | Infura API Key | `blockchain.node.url` |
| `MARKETPLACE_CONTRACT_ADDRESS` | 是 | 市场合约地址 | `blockchain.contract.marketplace.address` |
| `ADMIN_WALLET_PRIVATE_KEY` | 是 | Admin 钱包私钥 | `blockchain.admin.private-key` |

---

## 5. 实施步骤

### 5.1 实施顺序总览

```
Gate-0 (基础设施) → Wave-1 (智能合约) → Wave-2 (后端服务) → Wave-3 (工作流集成) → Wave-4 (端到端测试)
```

### 5.2 详细步骤清单

#### Gate-0: 基础设施准备（串行）

| 步骤 | 任务 | 依赖 | 可并行 |
|------|------|------|--------|
| G0-1 | 搭建 Spring Boot 项目骨架 | 无 | 否 |
| G0-2 | 配置 Web3j 依赖和 Bean | G0-1 | 否 |
| G0-3 | 配置 JBPM 依赖和运行时 | G0-1 | 否 |
| G0-4 | 配置 MySQL 数据库连接 | G0-1 | 否 |
| G0-5 | 部署本地 IPFS 节点或配置 Pinata | 无 | 是 |
| G0-6 | 创建 Sepolia 测试网 Admin 钱包 | 无 | 是 |

#### Wave-1: 智能合约开发（可部分并行）

| 步骤 | 任务 | 依赖 | 可并行 |
|------|------|------|--------|
| W1-1 | 编写 CCMarketPlace.sol 合约 | G0-6 | 否 |
| W1-2 | 编写合约单元测试 | W1-1 | 否 |
| W1-3 | 部署合约到 Sepolia 测试网 | W1-2 | 否 |
| W1-4 | 使用 Web3j 生成 Java Wrapper | W1-3 | 否 |
| W1-5 | 验证合约事件可被监听 | W1-3 | 否 |

#### Wave-2: 后端服务开发（可部分并行）

| 步骤 | 任务 | 依赖 | 可并行 |
|------|------|------|--------|
| W2-1 | 实现 BlockchainProxyService | W1-4 | 否 |
| W2-2 | 实现 IPFSService | G0-5 | 是 |
| W2-3 | 实现 NFTMetadataService | W2-2 | 否 |
| W2-4 | 扩展 BlockchainService 接口 | W2-1 | 否 |
| W2-5 | 实现 BlockchainServiceImpl | W2-4 | 否 |
| W2-6 | 实现 BlockchainEventListenerService | W2-5 | 否 |
| W2-7 | 实现 InvestmentProxyService | W2-5 | 是 |
| W2-8 | 扩展 ProjectListing 实体 | G0-4 | 是 |

#### Wave-3: JBPM 工作流集成（串行）

| 步骤 | 任务 | 依赖 | 可并行 |
|------|------|------|--------|
| W3-1 | 备份现有 NewListing.bpmn | 无 | 否 |
| W3-2 | 添加 6 个 Human Task 节点 | W3-1 | 否 |
| W3-3 | 调整 Bid Review 节点位置 | W3-2 | 否 |
| W3-4 | 配置 TaskService 集成 | W3-3 | 否 |
| W3-5 | 测试工作流节点流转 | W3-4 | 否 |

#### Wave-4: 端到端测试（串行）

| 步骤 | 任务 | 依赖 | 可并行 |
|------|------|------|--------|
| W4-1 | 编写集成测试用例 | W3-5 | 否 |
| W4-2 | 执行完整流程测试 | W4-1 | 否 |
| W4-3 | 修复发现的问题 | W4-2 | 否 |
| W4-4 | 准备演示脚本 | W4-3 | 否 |

---

## 6. 测试与验收

### 6.1 智能合约测试

**测试文件路径**: `contracts/test/CCMarketPlace.test.js`

**测试命令**:
```bash
npx hardhat test test/CCMarketPlace.test.js
```

**测试用例清单**:

| 测试用例 | 描述 |
|----------|------|
| `should mint dNFT successfully` | 验证铸造功能 |
| `should launch dNFT` | 验证上架功能 |
| `should add investor` | 验证添加投资者 |
| `should update restorer` | 验证更新修复者 |
| `should mark restoration complete` | 验证修复完成 |
| `should set item for sale` | 验证设置拍卖 |
| `should transfer to winner` | 验证转移NFT |
| `should emit correct events` | 验证事件触发 |
| `should reject unauthorized calls` | 验证权限控制 |

### 6.2 Java 后端测试

**测试文件路径**:
- `server/src/test/java/.../BlockchainServiceTest.java`
- `server/src/test/java/.../EventListenerServiceTest.java`
- `server/src/test/java/.../IPFSServiceTest.java`

**测试命令**:
```bash
mvn test -Dtest=BlockchainServiceTest
mvn test -Dtest=EventListenerServiceTest
mvn test -Dtest=IPFSServiceTest
```

### 6.3 JBPM 工作流测试

**测试文件路径**: `server/src/test/java/.../WorkflowIntegrationTest.java`

**测试命令**:
```bash
mvn test -Dtest=WorkflowIntegrationTest
```

### 6.4 端到端验收标准

| 阶段 | 验收标准 | 通过条件 |
|------|----------|----------|
| 1 | 创建新项目 | ✅ 数据库记录创建成功 |
| 2 | 详情验证 | ✅ 验证通过/返回编辑 |
| 3 | 价值评估 | ✅ 链上铸造 dNFT，返回 tokenId |
| 4 | Bid Review | ✅ dNFT 上架市场，状态可查 |
| 5 | Funding | ✅ 投资交易上链成功 |
| 6 | 投资者更新 | ✅ NFT 元数据包含投资者信息 |
| 7 | 分配修复者 | ✅ 修复者地址写入 NFT |
| 8 | 修复完成 | ✅ NFT 状态更新为"修复完成" |
| 9 | 上架物品 | ✅ 设置价格，NFT 可转移 |
| 10 | 拍卖 | ✅ 拍卖出价记录成功 |
| 11 | 拍卖审核 | ✅ 资金正确分配 |
| 12 | NFT 转移 | ✅ NFT 所有权转移给买家 |

---

## 7. 风险与依赖

### 7.1 风险评估

| 风险 | 影响程度 | 概率 | 缓解措施 |
|------|----------|------|----------|
| BPMN 修改破坏现有流程 | 极高 | 中 | 完整备份，测试环境验证，保留回滚方案 |
| 事件监听器失败 | 极高 | 中 | 心跳检测，自动重连，告警系统 |
| 异步状态不一致 | 极高 | 中 | 状态机管理，超时检测，手动干预接口 |
| Human Task 未找到 | 高 | 中 | 任务查询重试机制，延迟匹配，日志记录 |
| Gas 费过高 | 中 | 低 | 批量操作，使用测试网 |
| IPFS 上传失败 | 中 | 低 | 重试机制，备用网关 |
| 私钥泄露 | 极高 | 低 | 环境变量存储，禁止硬编码，访问控制 |

### 7.2 外部依赖

| 依赖项 | 类型 | 说明 |
|--------|------|------|
| Infura | 外部服务 | Ethereum 节点提供商 |
| IPFS | 外部服务 | 分布式存储 |
| Sepolia 测试网 | 外部服务 | 以太坊测试网络 |
| OpenZeppelin | 代码库 | 智能合约安全库 |
| Web3j | 代码库 | Java 区块链交互库 |
| JBPM | 代码库 | 工作流引擎 |

### 7.3 内部依赖（关键路径）

```
CCMarketPlace.sol → Web3j Wrapper → BlockchainProxyService → BlockchainServiceImpl → EventListenerService → JBPM Integration
```

---

## 8. 里程碑

| 里程碑 | 阶段 | 验收标准 |
|--------|------|----------|
| M1 | Gate-0 完成 | 项目骨架搭建完成，所有依赖配置正确 |
| M2 | Wave-1 完成 | 智能合约部署到测试网，所有函数可调用 |
| M3 | Wave-2 完成 | 后端服务全部实现，单元测试通过 |
| M4 | Wave-3 完成 | JBPM 工作流集成完成，Human Task 可被完成 |
| M5 | Wave-4 完成 | 端到端流程跑通，演示准备就绪 |

---

## 9. 附录

### 9.1 必读上下文文件列表

| 文件 | 说明 |
|------|------|
| `需求文档.md` | 完整需求规格 |
| `DOCUMENTATION-GUIDELINES.md` | 文档编写规范 |
| `Web3j 官方文档` | https://docs.web3j.io/ |
| `JBPM 用户指南` | https://docs.jbpm.org/ |
| `OpenSea 元数据标准` | https://docs.opensea.io/docs/metadata-standards |
| `Solidity 官方文档` | https://docs.soliditylang.org/ |

### 9.2 预计改动行数

| 文件 | 类型 | 预计行数 |
|------|------|----------|
| `CCMarketPlace.sol` | 新建/扩展 | ~250 行 |
| `BlockchainEventListenerService.java` | 新建 | ~200 行 |
| `BlockchainProxyService.java` | 新建 | ~150 行 |
| `BlockchainServiceImpl.java` | 扩展 | ~100 行 |
| `InvestmentProxyService.java` | 新建 | ~80 行 |
| `NFTMetadataService.java` | 新建 | ~60 行 |
| `IPFSService.java` | 新建 | ~80 行 |
| `NFTMetadata.java` | 新建 | ~30 行 |
| `Attribute.java` | 新建 | ~40 行 |
| `ProjectListing.java` | 扩展 | ~30 行 |
| `NewListing.bpmn` | 修改 | ~100 行 |
| `application.properties` | 扩展 | ~20 行 |
| **总计** | - | **~1140 行** |

### 9.3 单文件约束

所有文件均控制在 500 行以内，无需拆分。

---

## 10. 并行规划（Gate/Wave 结构）

### 10.1 Gate-0: 基础设施准备

| Task ID | 范围 | 依赖 | 必读文档 | 预估行数 |
|---------|------|------|----------|----------|
| G0-1 | Spring Boot 项目骨架 | 无 | Spring Boot 官方文档 | 50 |
| G0-2 | Web3j 配置 | G0-1 | Web3j 文档 | 30 |
| G0-3 | JBPM 配置 | G0-1 | JBPM 用户指南 | 40 |
| G0-4 | MySQL 配置 | G0-1 | - | 20 |
| G0-5 | IPFS 节点部署 | 无 | IPFS 文档 | - |
| G0-6 | Admin 钱包创建 | 无 | MetaMask 文档 | - |

**文件锁**: 无

### 10.2 Wave-1: 智能合约开发

| Task ID | 范围 | 依赖 | 必读文档 | 核心代码入口 | 预估行数 |
|---------|------|------|----------|--------------|----------|
| W1-1 | CCMarketPlace.sol 编写 | G0-6 | Solidity 文档, OpenZeppelin | `contracts/CCMarketPlace.sol` | 250 |
| W1-2 | 合约单元测试 | W1-1 | Hardhat 文档 | `contracts/test/CCMarketPlace.test.js` | 150 |
| W1-3 | 部署到 Sepolia | W1-2 | Hardhat 部署文档 | `contracts/scripts/deploy.js` | 30 |
| W1-4 | Web3j Wrapper 生成 | W1-3 | Web3j CLI 文档 | `server/.../contracts/CCMarketPlace.java` | 自动生成 |
| W1-5 | 事件监听验证 | W1-3 | Web3j 事件文档 | - | - |

**文件锁**: `contracts/CCMarketPlace.sol`（串行）

### 10.3 Wave-2: 后端服务开发

| Task ID | 范围 | 依赖 | 核心代码入口 | 预估行数 | 可并行 |
|---------|------|------|--------------|----------|--------|
| W2-1 | BlockchainProxyService | W1-4 | `service/BlockchainProxyService.java` | 150 | 否 |
| W2-2 | IPFSService | G0-5 | `service/IPFSService.java` | 80 | 是 |
| W2-3 | NFTMetadataService | W2-2 | `service/NFTMetadataService.java` | 60 | 否 |
| W2-4 | BlockchainService 接口扩展 | W2-1 | `service/BlockchainService.java` | 50 | 否 |
| W2-5 | BlockchainServiceImpl | W2-4 | `service/BlockchainServiceImpl.java` | 100 | 否 |
| W2-6 | EventListenerService | W2-5 | `service/BlockchainEventListenerService.java` | 200 | 否 |
| W2-7 | InvestmentProxyService | W2-5 | `service/InvestmentProxyService.java` | 80 | 是 |
| W2-8 | ProjectListing 扩展 | G0-4 | `model/ProjectListing.java` | 30 | 是 |

**文件锁**: `BlockchainService.java` → `BlockchainServiceImpl.java`（串行）

### 10.4 Wave-3: JBPM 工作流集成

| Task ID | 范围 | 依赖 | 核心代码入口 | 预估行数 | 可并行 |
|---------|------|------|--------------|----------|--------|
| W3-1 | 备份 NewListing.bpmn | 无 | `workflows/NewListing.bpmn` | - | 否 |
| W3-2 | 添加 Human Task 节点 | W3-1 | `workflows/NewListing.bpmn` | 80 | 否 |
| W3-3 | 调整 Bid Review 位置 | W3-2 | `workflows/NewListing.bpmn` | 20 | 否 |
| W3-4 | TaskService 集成 | W3-3, W2-6 | `service/BlockchainEventListenerService.java` | 50 | 否 |
| W3-5 | 工作流测试 | W3-4 | `test/WorkflowIntegrationTest.java` | 100 | 否 |

**文件锁**: `NewListing.bpmn`（高风险，必须串行）

### 10.5 Wave-4: 端到端测试

| Task ID | 范围 | 依赖 | 核心代码入口 | 预估行数 |
|---------|------|------|--------------|----------|
| W4-1 | 集成测试用例 | W3-5 | `test/E2EIntegrationTest.java` | 200 |
| W4-2 | 完整流程测试 | W4-1 | - | - |
| W4-3 | 问题修复 | W4-2 | 视情况 | - |
| W4-4 | 演示脚本准备 | W4-3 | `docs/DEMO-SCRIPT.md` | 50 |

**文件锁**: 无

---

## 11. 提交与评审清单

### 11.1 大纲完整性检查

- [x] 背景与目标：明确业务目的、上线标准
- [x] 现状：精确到文件/类/函数/组件/路由
- [x] 缺口：列点，无笼统表述
- [x] 设计与对接方案：API 契约、数据表、模块/组件、ENV/配置
- [x] 实施步骤：按可执行顺序，标注并行/串行
- [x] 测试与验收：测试文件路径、命令
- [x] 风险与依赖：外部服务/数据/权限
- [x] 里程碑：阶段与验收标准
- [x] 附录：必读文件、改动行数、单文件约束

### 11.2 技术规范检查

- [x] 智能合约事件定义完整（8 个事件）
- [x] BlockchainService 接口方法定义（7 个新方法）
- [x] ProjectListing 实体扩展字段（4 个新字段）
- [x] 配置文件变量定义（含必填说明）
- [x] Human Task 节点定义（6 个等待任务）

### 11.3 并行规划检查

- [x] Gate/Wave 结构完整
- [x] 每个 Task 包含：ID、范围、依赖、核心代码入口、预估行数
- [x] 关键路径与文件锁明确
- [x] 无"可选/视情况"模糊措辞

### 11.4 代码规范

- [x] 代码注释语言：英文
- [x] 变量/方法命名：英文，驼峰命名法
- [x] 日志输出：英文

---

**文档编写完成** ✅

> 本文档遵循 `DOCUMENTATION-GUIDELINES.md` 规范编写，可直接指导实施。

