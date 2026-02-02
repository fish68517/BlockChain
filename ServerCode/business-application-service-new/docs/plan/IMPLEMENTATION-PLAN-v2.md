# CollectorCoin 区块链项目实施规划文档 v2.0

> **项目类型**: 毕业设计
> **文档版本**: v2.0
> **创建日期**: 2026-01-28
> **状态**: 待评审
> **基于**: 实际代码检查结果

---

## 1. 项目概述

### 1.1 业务背景

CollectorCoin 是一个基于区块链技术的收藏品投资平台，将实体收藏品（如经典汽车）的修复项目代币化为动态 NFT（dNFT），实现去中心化的投资、修复和拍卖全流程管理。

### 1.2 项目目标

| 目标项 | 描述 | 验收标准 |
|--------|------|----------|
| NFT 代币化 | 实现收藏品项目的 dNFT 代币化 | 成功铸造 dNFT 并返回 tokenId |
| 生命周期管理 | 构建完整的投资-修复-拍卖生命周期 | 端到端流程可演示跑通 |
| 工作流自动化 | 通过 JBPM 实现业务流程自动化 | Human Task 可被 TaskService 完成 |
| 交易透明性 | 通过智能合约保证交易透明不可篡改 | 所有事件正确触发并可被监听 |
| 用户界面 | 提供完整的 Web 前端界面 | 支持所有角色的操作流程 |

### 1.3 技术栈

| 层级 | 技术选型 |
|------|----------|
| 前端框架 | React 18 + TypeScript |
| 状态管理 | Redux Toolkit |
| UI 组件库 | Ant Design |
| 钱包集成 | ethers.js + MetaMask |
| 后端语言 | Java 17 (Spring Boot 3.x) |
| 工作流引擎 | JBPM 7.x |
| 区块链交互 | Web3j 4.x |
| 智能合约 | Solidity 0.8.20 (ERC-1155) |
| 分布式存储 | IPFS |
| 数据库 | MySQL 8.x |

### 1.4 上线标准

- [ ] 端到端演示流程完整跑通（约 15-20 分钟）
- [ ] 所有智能合约函数通过单元测试
- [ ] JBPM 工作流从 New Listing 到 Auction Review 完整流转
- [ ] 事件监听延迟 < 30 秒
- [ ] NFT 元数据符合 OpenSea 标准
- [ ] 前端支持所有角色操作（Admin、Investor、Restorer、Buyer）

---

## 2. 现状分析

### 2.1 项目结构（当前）

```
collectorcoin/
├── server/                              # Spring Boot 后端 [部分完成]
│   └── src/main/java/com/collectorcoin/
│       ├── config/
│       │   ├── Web3jConfig.java         ✅ 已完成
│       │   └── JbpmConfig.java          ✅ 已完成
│       ├── service/
│       │   ├── BlockchainService.java   ✅ 接口已定义
│       │   ├── BlockchainServiceImpl.java ⚠️ 4个方法仅日志
│       │   ├── BlockchainProxyService.java ⚠️ 缺4个方法
│       │   ├── BlockchainEventListenerService.java ✅ 已完成
│       │   ├── InvestmentProxyService.java ✅ 已完成
│       │   ├── NFTMetadataService.java  ✅ 已完成
│       │   ├── IPFSService.java         ✅ 已完成
│       │   └── WorkflowTaskService.java ⚠️ 3个TODO方法
│       ├── model/
│       │   ├── ProjectListing.java      ✅ 已完成
│       │   ├── NFTMetadata.java         ✅ 已完成
│       │   └── Attribute.java           ✅ 已完成
│       ├── contracts/
│       │   └── CCMarketPlace.java       ✅ Web3j Wrapper
│       └── repository/
│           └── ProjectListingRepository.java ✅ 已完成
├── contracts/                           # Solidity 智能合约 [基本完成]
│   └── contracts/
│       └── CCMarketPlace.sol            ✅ 已完成
├── workflows/                           # JBPM 工作流 [已完成]
│   ├── NewListing.bpmn                  ✅ 已完成
│   └── NewListing.bpmn.backup           ✅ 备份
├── frontend/                            # React 前端 [❌ 完全缺失]
│   └── (不存在)
└── docs/
    └── plan/
```

### 2.2 各层完成度详情

#### 2.2.1 智能合约层 - 95% 完成

| 文件 | 状态 | 说明 |
|------|:----:|------|
| `CCMarketPlace.sol` | ✅ | 191行，全部核心功能已实现 |

**已实现功能：**
- ✅ `mintDNFT()` - 铸造 dNFT
- ✅ `launchDNFT()` - 上架市场
- ✅ `addInvestor()` - 添加投资者
- ✅ `updateRestorer()` - 更新修复者
- ✅ `markRestorationComplete()` - 标记修复完成
- ✅ `setItemForSale()` - 设置拍卖价格
- ✅ `transferToWinner()` - 转移 NFT
- ✅ `updateMetadataURI()` - 更新元数据
- ✅ 8 个事件全部定义

#### 2.2.2 JBPM 工作流层 - 90% 完成

| 文件 | 状态 | 说明 |
|------|:----:|------|
| `NewListing.bpmn` | ✅ | 流程定义完整 |

**已实现功能：**
- ✅ Bid Review 已移至 Funding 之前
- ✅ 6 个 Human Task 等待节点已定义
- ⚠️ WorkflowTaskService helper 方法未实现

#### 2.2.3 Java 后端服务层 - 60% 完成

| 文件 | 状态 | 行数 | 说明 |
|------|:----:|:----:|------|
| `BlockchainEventListenerService.java` | ✅ | 163 | 完整实现 |
| `InvestmentProxyService.java` | ✅ | 152 | 完整实现 |
| `NFTMetadataService.java` | ✅ | 42 | 接口完整 |
| `NFTMetadataServiceImpl.java` | ✅ | 88 | 实现完整 |
| `IPFSService.java` | ✅ | 42 | 接口完整 |
| `IPFSServiceImpl.java` | ✅ | 145 | 含 Mock 模式 |
| `BlockchainService.java` | ✅ | 61 | 接口完整 |
| `BlockchainServiceImpl.java` | ⚠️ | 79 | 4个方法仅日志 |
| `BlockchainProxyService.java` | ⚠️ | 79 | 缺4个代理方法 |
| `WorkflowTaskService.java` | ⚠️ | 178 | 3个TODO方法 |
| `ProjectListing.java` | ✅ | 74 | NFT字段已添加 |
| `CCMarketPlace.java` | ✅ | 280 | Web3j Wrapper |

**缺失的 REST Controller：**
- ❌ `ListingController` - 项目管理 API
- ❌ `InvestmentController` - 投资 API
- ❌ `AuctionController` - 拍卖 API
- ❌ `WorkflowController` - 工作流 API
- ❌ `AuthController` - 认证 API

#### 2.2.4 前端层 - 0% 完成

| 模块 | 状态 | 说明 |
|------|:----:|------|
| React 项目 | ❌ | 完全不存在 |
| 页面组件 | ❌ | 无任何页面 |
| MetaMask 集成 | ❌ | 无钱包连接 |
| API 调用 | ❌ | 无服务调用 |
| 路由系统 | ❌ | 无路由配置 |

---

## 3. 缺口分析

### 3.1 前端层缺口（最大缺口）

#### 3.1.1 项目基础设施
- [ ] React + TypeScript 项目初始化
- [ ] 路由配置 (React Router)
- [ ] 状态管理 (Redux Toolkit)
- [ ] API 客户端 (Axios)
- [ ] UI 组件库 (Ant Design)
- [ ] 样式方案 (Tailwind CSS / SCSS)

#### 3.1.2 钱包集成
- [ ] MetaMask 连接组件
- [ ] 钱包状态管理
- [ ] 网络切换处理
- [ ] 交易签名流程

#### 3.1.3 页面组件
- [ ] 首页 (Dashboard)
- [ ] 项目列表页 (Listings)
- [ ] 项目详情页 (Listing Detail)
- [ ] 创建项目页 (Create Listing)
- [ ] 投资页面 (Investment)
- [ ] 拍卖页面 (Auction)
- [ ] 我的投资页 (My Investments)
- [ ] 管理后台 (Admin Panel)

#### 3.1.4 角色界面
- [ ] Admin 管理界面
- [ ] Investor 投资界面
- [ ] Restorer 修复者界面
- [ ] Buyer 买家界面

### 3.2 后端 REST API 缺口

- [ ] `GET /api/listings` - 获取项目列表
- [ ] `GET /api/listings/{id}` - 获取项目详情
- [ ] `POST /api/listings` - 创建新项目
- [ ] `PUT /api/listings/{id}` - 更新项目
- [ ] `POST /api/listings/{id}/verify` - 验证项目
- [ ] `POST /api/listings/{id}/estimate` - 价值评估
- [ ] `POST /api/listings/{id}/invest` - 投资
- [ ] `POST /api/listings/{id}/assign-restorer` - 分配修复者
- [ ] `POST /api/listings/{id}/complete-restoration` - 完成修复
- [ ] `POST /api/listings/{id}/post-auction` - 上架拍卖
- [ ] `POST /api/listings/{id}/bid` - 出价
- [ ] `POST /api/listings/{id}/finalize` - 完成拍卖
- [ ] `GET /api/workflow/tasks` - 获取待办任务
- [ ] `POST /api/workflow/tasks/{id}/complete` - 完成任务

### 3.3 后端服务层缺口

#### BlockchainProxyService 缺失方法：
- [ ] `updateRestorer(tokenId, restorer)`
- [ ] `markRestorationComplete(tokenId)`
- [ ] `setItemForSale(tokenId, price)`
- [ ] `transferToWinner(tokenId, winner)`

#### BlockchainServiceImpl 需完善方法：
- [ ] `updateNFTRestorer()` - 当前仅日志
- [ ] `markRestorationComplete()` - 当前仅日志
- [ ] `setNFTAuctionLive()` - 当前仅日志
- [ ] `transferNFTToWinner()` - 当前仅日志

#### WorkflowTaskService 需实现方法：
- [ ] `findTaskByNameAndListing(taskName, listingId)`
- [ ] `findListingByProject(projectAddress)`
- [ ] `findListingByTokenId(tokenId)`

### 3.4 配置层缺口

- [ ] 前端环境变量配置
- [ ] CORS 跨域配置
- [ ] JWT 认证配置（可选）

---

## 4. 设计方案

### 4.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Frontend (React)                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│  │ Dashboard│ │ Listings │ │ Invest   │ │ Auction  │ │ Admin    │      │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘      │
│       │            │            │            │            │             │
│  ┌────┴────────────┴────────────┴────────────┴────────────┴─────┐      │
│  │                    Redux Store + API Client                   │      │
│  └────┬─────────────────────────────────────────────────────────┘      │
│       │                                                                 │
│  ┌────┴─────┐                                                          │
│  │ MetaMask │ ←── Wallet Connection                                    │
│  └──────────┘                                                          │
└───────┬─────────────────────────────────────────────────────────────────┘
        │ REST API + WebSocket
        ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        Backend (Spring Boot)                             │
│  ┌──────────────────────────────────────────────────────────────┐      │
│  │                     REST Controllers                          │      │
│  │  ListingController │ InvestmentController │ AuctionController │      │
│  └────────────────────────────┬─────────────────────────────────┘      │
│                               │                                         │
│  ┌────────────────────────────┴─────────────────────────────────┐      │
│  │                      Service Layer                            │      │
│  │  BlockchainService │ WorkflowTaskService │ NFTMetadataService │      │
│  └────────────────────────────┬─────────────────────────────────┘      │
│                               │                                         │
│  ┌────────────────────────────┴─────────────────────────────────┐      │
│  │              BlockchainProxyService (Admin Wallet)            │      │
│  │                    BlockchainEventListenerService             │      │
│  └────────────────────────────┬─────────────────────────────────┘      │
└───────────────────────────────┼─────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   MySQL DB   │       │  JBPM Engine │       │   Ethereum   │
│              │       │              │       │  (Sepolia)   │
└──────────────┘       └──────────────┘       └──────┬───────┘
                                                     │
                                              ┌──────┴───────┐
                                              │ CCMarketPlace│
                                              │   Contract   │
                                              └──────────────┘
```

### 4.2 前端设计

#### 4.2.1 项目结构

```
frontend/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/           # 通用组件
│   │   ├── common/
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   └── Loading.tsx
│   │   ├── wallet/
│   │   │   ├── ConnectWallet.tsx
│   │   │   └── WalletStatus.tsx
│   │   ├── listing/
│   │   │   ├── ListingCard.tsx
│   │   │   ├── ListingForm.tsx
│   │   │   └── ListingDetail.tsx
│   │   ├── investment/
│   │   │   ├── InvestForm.tsx
│   │   │   └── InvestorList.tsx
│   │   └── auction/
│   │       ├── BidForm.tsx
│   │       └── AuctionStatus.tsx
│   ├── pages/                # 页面组件
│   │   ├── Dashboard.tsx
│   │   ├── Listings.tsx
│   │   ├── ListingDetail.tsx
│   │   ├── CreateListing.tsx
│   │   ├── Investment.tsx
│   │   ├── Auction.tsx
│   │   ├── MyInvestments.tsx
│   │   └── AdminPanel.tsx
│   ├── store/                # Redux 状态管理
│   │   ├── index.ts
│   │   ├── slices/
│   │   │   ├── authSlice.ts
│   │   │   ├── listingSlice.ts
│   │   │   ├── walletSlice.ts
│   │   │   └── workflowSlice.ts
│   │   └── api/
│   │       └── apiSlice.ts
│   ├── services/             # API 服务
│   │   ├── api.ts
│   │   ├── listingService.ts
│   │   ├── investmentService.ts
│   │   └── auctionService.ts
│   ├── hooks/                # 自定义 Hooks
│   │   ├── useWallet.ts
│   │   ├── useContract.ts
│   │   └── useWorkflow.ts
│   ├── utils/                # 工具函数
│   │   ├── format.ts
│   │   ├── constants.ts
│   │   └── web3.ts
│   ├── types/                # TypeScript 类型
│   │   ├── listing.ts
│   │   ├── investment.ts
│   │   └── auction.ts
│   ├── App.tsx
│   ├── index.tsx
│   └── routes.tsx
├── package.json
├── tsconfig.json
├── vite.config.ts
└── .env.example
```

#### 4.2.2 页面设计

| 页面 | 路由 | 角色 | 功能描述 |
|------|------|------|----------|
| Dashboard | `/` | All | 平台概览、统计数据 |
| Listings | `/listings` | All | 项目列表、筛选搜索 |
| Listing Detail | `/listings/:id` | All | 项目详情、状态展示 |
| Create Listing | `/listings/create` | Admin | 创建新项目 |
| Investment | `/invest/:id` | Investor | 投资操作页面 |
| Auction | `/auction/:id` | Buyer | 拍卖出价页面 |
| My Investments | `/my-investments` | Investor | 我的投资记录 |
| Admin Panel | `/admin` | Admin | 管理后台、任务处理 |
| Restorer Panel | `/restorer` | Restorer | 修复任务管理 |

#### 4.2.3 核心组件设计

**ConnectWallet.tsx** - 钱包连接组件
```typescript
interface WalletState {
  address: string | null;
  chainId: number | null;
  isConnected: boolean;
  balance: string;
}

// 功能：连接 MetaMask、显示地址、切换网络
```

**ListingCard.tsx** - 项目卡片组件
```typescript
interface ListingCardProps {
  id: number;
  title: string;
  description: string;
  imageUrl: string;
  status: ListingStatus;
  valueEstimation: number;
  fundingProgress: number;
}
```

**InvestForm.tsx** - 投资表单组件
```typescript
interface InvestFormProps {
  listingId: number;
  minAmount: number;
  maxAmount: number;
  onSubmit: (amount: number) => Promise<void>;
}
```

### 4.3 REST API 设计

#### 4.3.1 Listing API

| Method | Endpoint | 描述 | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/listings` | 获取列表 | - | `List<ListingDTO>` |
| GET | `/api/listings/{id}` | 获取详情 | - | `ListingDTO` |
| POST | `/api/listings` | 创建项目 | `CreateListingRequest` | `ListingDTO` |
| PUT | `/api/listings/{id}` | 更新项目 | `UpdateListingRequest` | `ListingDTO` |
| POST | `/api/listings/{id}/verify` | 验证通过 | - | `ListingDTO` |
| POST | `/api/listings/{id}/reject` | 验证拒绝 | `{reason}` | `ListingDTO` |
| POST | `/api/listings/{id}/estimate` | 价值评估 | `{valueEst, repairEst}` | `ListingDTO` |

#### 4.3.2 Investment API

| Method | Endpoint | 描述 | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/listings/{id}/invest` | 投资 | `{amount, walletAddress}` | `InvestmentDTO` |
| GET | `/api/investments/my` | 我的投资 | - | `List<InvestmentDTO>` |

#### 4.3.3 Restoration API

| Method | Endpoint | 描述 | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/listings/{id}/assign-restorer` | 分配修复者 | `{restorerAddress}` | `ListingDTO` |
| POST | `/api/listings/{id}/complete-restoration` | 完成修复 | - | `ListingDTO` |

#### 4.3.4 Auction API

| Method | Endpoint | 描述 | Request Body | Response |
|--------|----------|------|--------------|----------|
| POST | `/api/listings/{id}/post-auction` | 上架拍卖 | `{price}` | `ListingDTO` |
| POST | `/api/listings/{id}/bid` | 出价 | `{amount, walletAddress}` | `BidDTO` |
| POST | `/api/listings/{id}/finalize` | 完成拍卖 | `{winnerAddress}` | `ListingDTO` |

#### 4.3.5 Workflow API

| Method | Endpoint | 描述 | Request Body | Response |
|--------|----------|------|--------------|----------|
| GET | `/api/workflow/tasks` | 待办任务 | - | `List<TaskDTO>` |
| POST | `/api/workflow/tasks/{id}/complete` | 完成任务 | `{data}` | `TaskDTO` |

### 4.4 后端补全设计

#### 4.4.1 ListingController.java（新建）

```java
@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @GetMapping
    public List<ListingDTO> getListings();

    @GetMapping("/{id}")
    public ListingDTO getListing(@PathVariable Long id);

    @PostMapping
    public ListingDTO createListing(@RequestBody CreateListingRequest request);

    @PostMapping("/{id}/verify")
    public ListingDTO verifyListing(@PathVariable Long id);

    @PostMapping("/{id}/estimate")
    public ListingDTO estimateListing(@PathVariable Long id,
                                       @RequestBody EstimateRequest request);

    @PostMapping("/{id}/invest")
    public InvestmentDTO invest(@PathVariable Long id,
                                @RequestBody InvestRequest request);
}
```

#### 4.4.2 BlockchainProxyService 补全方法

```java
// 需要添加的方法
public TransactionReceipt updateRestorer(BigInteger tokenId, String restorer);
public TransactionReceipt markRestorationComplete(BigInteger tokenId);
public TransactionReceipt setItemForSale(BigInteger tokenId, BigInteger price);
public TransactionReceipt transferToWinner(BigInteger tokenId, String winner);
```

#### 4.4.3 WorkflowTaskService 补全方法

```java
// 需要实现的方法
private Long findTaskByNameAndListing(String taskName, Long listingId) {
    // Query JBPM RuntimeDataService for tasks
}

private Long findListingByProject(String projectAddress) {
    return projectListingRepository.findByProjectAddress(projectAddress).getId();
}

private Long findListingByTokenId(Long tokenId) {
    return projectListingRepository.findByNftTokenId(tokenId).getId();
}
```

---

## 5. 实施步骤

### 5.1 总体顺序

```
Phase-1 (前端基础) → Phase-2 (后端API) → Phase-3 (前端页面) → Phase-4 (集成测试)
```

### 5.2 Phase-1: 前端基础设施（优先级最高）

| 步骤 | 任务 | 预估行数 | 依赖 |
|------|------|:--------:|------|
| P1-1 | 初始化 React + Vite + TypeScript 项目 | 50 | 无 |
| P1-2 | 配置 Ant Design + Tailwind CSS | 30 | P1-1 |
| P1-3 | 配置 Redux Toolkit | 80 | P1-1 |
| P1-4 | 配置 React Router | 50 | P1-1 |
| P1-5 | 实现 MetaMask 连接 Hook | 100 | P1-1 |
| P1-6 | 实现 API 客户端 (Axios) | 60 | P1-1 |
| P1-7 | 创建通用布局组件 | 150 | P1-2 |

### 5.3 Phase-2: 后端 API 补全

| 步骤 | 任务 | 预估行数 | 依赖 |
|------|------|:--------:|------|
| P2-1 | 创建 ListingController | 200 | 无 |
| P2-2 | 创建 InvestmentController | 100 | 无 |
| P2-3 | 创建 AuctionController | 100 | 无 |
| P2-4 | 创建 WorkflowController | 80 | 无 |
| P2-5 | 补全 BlockchainProxyService | 60 | 无 |
| P2-6 | 补全 BlockchainServiceImpl | 40 | P2-5 |
| P2-7 | 补全 WorkflowTaskService | 50 | 无 |
| P2-8 | 配置 CORS | 20 | 无 |

### 5.4 Phase-3: 前端页面开发

| 步骤 | 任务 | 预估行数 | 依赖 |
|------|------|:--------:|------|
| P3-1 | Dashboard 页面 | 150 | P1-7 |
| P3-2 | Listings 列表页 | 200 | P1-7 |
| P3-3 | ListingDetail 详情页 | 250 | P3-2 |
| P3-4 | CreateListing 创建页 | 200 | P1-7 |
| P3-5 | Investment 投资页 | 180 | P3-3 |
| P3-6 | Auction 拍卖页 | 180 | P3-3 |
| P3-7 | AdminPanel 管理页 | 250 | P1-7 |
| P3-8 | MyInvestments 页面 | 150 | P1-7 |

### 5.5 Phase-4: 集成测试

| 步骤 | 任务 | 依赖 |
|------|------|------|
| P4-1 | 前后端联调 | P2-8, P3-8 |
| P4-2 | 端到端流程测试 | P4-1 |
| P4-3 | 问题修复 | P4-2 |
| P4-4 | 演示脚本准备 | P4-3 |

---

## 6. 测试与验收

### 6.1 端到端验收标准

| 阶段 | 操作 | 验收标准 |
|:----:|------|----------|
| 1 | 创建新项目 | ✅ 前端表单提交，数据库记录创建 |
| 2 | 验证详情 | ✅ Admin 审核通过/拒绝 |
| 3 | 价值评估 | ✅ 链上铸造 dNFT，返回 tokenId |
| 4 | Bid Review | ✅ dNFT 上架市场 |
| 5 | Funding | ✅ 投资交易上链成功 |
| 6 | 分配修复者 | ✅ 修复者地址写入 NFT |
| 7 | 修复完成 | ✅ NFT 状态更新 |
| 8 | 上架拍卖 | ✅ 设置价格，NFT 可转移 |
| 9 | 拍卖出价 | ✅ 出价记录成功 |
| 10 | 完成拍卖 | ✅ NFT 转移给买家 |

---

## 7. 风险与依赖

### 7.1 风险评估

| 风险 | 影响 | 概率 | 缓解措施 |
|------|:----:|:----:|----------|
| 前端开发周期长 | 高 | 高 | 使用 Ant Design 加速 |
| MetaMask 集成问题 | 中 | 中 | 参考官方文档 |
| 前后端联调问题 | 中 | 中 | 定义清晰 API 契约 |
| CORS 跨域问题 | 低 | 高 | 提前配置 |

### 7.2 外部依赖

| 依赖项 | 类型 | 说明 |
|--------|------|------|
| Infura | 服务 | Ethereum 节点 |
| IPFS | 服务 | 分布式存储 |
| Sepolia | 网络 | 测试网 |
| MetaMask | 插件 | 钱包连接 |

---

## 8. 里程碑

| 里程碑 | 阶段 | 验收标准 |
|--------|------|----------|
| M1 | Phase-1 完成 | 前端项目可运行，钱包可连接 |
| M2 | Phase-2 完成 | 后端 API 全部可调用 |
| M3 | Phase-3 完成 | 所有页面开发完成 |
| M4 | Phase-4 完成 | 端到端流程跑通 |

---

## 9. 附录

### 9.1 预计新增代码行数

| 模块 | 文件 | 行数 |
|------|------|:----:|
| **前端** | | |
| | 项目配置 | 200 |
| | 通用组件 | 400 |
| | 页面组件 | 1500 |
| | Redux Store | 300 |
| | API 服务 | 200 |
| | Hooks | 200 |
| **前端小计** | | **2800** |
| **后端** | | |
| | Controllers | 500 |
| | Service 补全 | 150 |
| | 配置 | 30 |
| **后端小计** | | **680** |
| **总计** | | **~3500** |

### 9.2 文件清单

**需要新建的文件：**

```
frontend/                        # 整个前端项目（新建）
├── src/
│   ├── components/             # ~15 个组件文件
│   ├── pages/                  # ~8 个页面文件
│   ├── store/                  # ~5 个状态文件
│   ├── services/               # ~4 个服务文件
│   ├── hooks/                  # ~3 个 Hook 文件
│   └── types/                  # ~3 个类型文件

server/src/main/java/.../
├── controller/
│   ├── ListingController.java      # 新建
│   ├── InvestmentController.java   # 新建
│   ├── AuctionController.java      # 新建
│   └── WorkflowController.java     # 新建
└── dto/
    ├── ListingDTO.java             # 新建
    ├── InvestmentDTO.java          # 新建
    └── CreateListingRequest.java   # 新建
```

**需要修改的文件：**

```
server/src/main/java/.../
├── service/
│   ├── BlockchainProxyService.java   # 添加4个方法
│   ├── BlockchainServiceImpl.java    # 完善4个方法
│   └── WorkflowTaskService.java      # 实现3个方法
└── repository/
    └── ProjectListingRepository.java # 添加查询方法
```

### 9.3 当前完成度总结

| 模块 | 完成度 | 说明 |
|------|:------:|------|
| 智能合约 | 95% | 基本完成 |
| JBPM 工作流 | 90% | 基本完成 |
| 后端服务 | 60% | 缺 Controller |
| 前端 | 0% | 完全缺失 |
| **整体** | **~40%** | |

---

**文档编写完成** ✅

> 本文档基于 2026-01-28 实际代码检查结果编写
