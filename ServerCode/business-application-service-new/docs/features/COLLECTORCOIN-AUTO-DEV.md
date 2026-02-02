# CollectorCoin 区块链项目 Auto-Dev 执行文档

> 基于 `docs/plan/IMPLEMENTATION-PLAN-v2.md` 生成
> **生成日期**: 2026-01-28
> **项目类型**: 毕业设计
> **整体完成度**: 100% ✅

---

## 1. 规划进度

**状态**: ✅ 规划完成
**总任务数**: 15 个任务
**支持并行**: 最多 4 个 Claude 实例

---

## 2. 现有代码统计

### 2.1 智能合约层 (95% 完成)

| 文件 | 行数 | 状态 |
|------|:----:|:----:|
| `contracts/contracts/CCMarketPlace.sol` | 191 | ✅ 完成 |

### 2.2 JBPM 工作流层 (90% 完成)

| 文件 | 行数 | 状态 |
|------|:----:|:----:|
| `workflows/NewListing.bpmn` | 158 | ✅ 完成 |

### 2.3 Java 后端服务层 (60% 完成)

| 文件 | 行数 | 状态 | 说明 |
|------|:----:|:----:|------|
| `BlockchainEventListenerService.java` | 163 | ✅ | 完整实现 |
| `InvestmentProxyService.java` | 152 | ✅ | 完整实现 |
| `WorkflowTaskService.java` | 178 | ⚠️ | 3个TODO方法 |
| `BlockchainServiceImpl.java` | 79 | ⚠️ | 4个方法仅日志 |
| `BlockchainProxyService.java` | 79 | ⚠️ | 缺4个代理方法 |
| `ProjectListing.java` | 74 | ✅ | NFT字段已添加 |
| `ProjectListingRepository.java` | 20 | ✅ | 查询方法已有 |

### 2.4 前端层 (10% 完成)

| 模块 | 状态 |
|------|:----:|
| React 项目 | ✅ 已初始化 |

---

## 3. 预估总览

| Wave | Task-ID | 任务名称 | 预估上下文 | 状态 | 依赖 |
|:----:|---------|----------|:----------:|:----:|------|
| 0 | P1-INIT | 前端项目初始化 | ~25k | ✅ 完成 | 无 |
| 0 | P2-CTRL | 后端 Controller 层 | ~45k | ✅ 完成 | 无 |
| 1 | P1-WALLET | MetaMask 钱包集成 | ~35k | ✅ 完成 | P1-INIT |
| 1 | P1-STORE | Redux 状态管理 | ~30k | ✅ 完成 | P1-INIT |
| 1 | P1-API | API 客户端服务 | ~25k | ✅ 完成 | P1-INIT |
| 1 | P2-SVC | 后端服务层补全 | ~35k | ✅ 完成 | 无 |
| 2 | P3-LAYOUT | 通用布局组件 | ~40k | ✅ 完成 | P1-INIT |
| 2 | P3-DASH | Dashboard 页面 | ~35k | ✅ 已完成（2026-01-28 16:12） | P3-LAYOUT |
| 2 | P3-LIST | Listings 列表页 | ~45k | ✅ 完成 | P3-LAYOUT, P1-API |
| 3 | P3-DETAIL | 项目详情页 | ~50k | ✅ 完成 | P3-LIST |
| 3 | P3-CREATE | 创建项目页 | ~45k | ✅ 完成 | P3-LAYOUT, P1-WALLET |
| 3 | P3-INVEST | 投资页面 | ~40k | ✅ 完成 | P3-DETAIL, P1-WALLET |
| 4 | P3-AUCTION | 拍卖页面 | ~40k | ✅ 完成 | P3-DETAIL, P1-WALLET |
| 4 | P3-ADMIN | 管理后台 | ~55k | ✅ 完成 | P3-LAYOUT, P2-CTRL |
| 5 | P4-E2E | 端到端集成测试 | ~60k | ✅ 已完成 | 所有前置任务 |

---

## 4. 任务依赖图

```
Wave-0 (基础设施，可并行)
┌─────────────┐     ┌─────────────┐
│  P1-INIT    │     │  P2-CTRL    │
│ 前端初始化   │     │ Controller  │
└──────┬──────┘     └──────┬──────┘
       │                   │
Wave-1 │                   │
       ▼                   ▼
┌──────┴──────┐     ┌──────┴──────┐
│  P1-WALLET  │     │  P2-SVC     │
│  P1-STORE   │     │ 服务层补全   │
│  P1-API     │     └─────────────┘
└──────┬──────┘
       │
Wave-2 │
       ▼
┌─────────────┐
│  P3-LAYOUT  │
│ 通用布局组件 │
└──────┬──────┘
       │
       ├──────────────┬──────────────┐
       ▼              ▼              ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  P3-DASH    │ │  P3-LIST    │ │  P3-CREATE  │
│  Dashboard  │ │  列表页     │ │  创建页     │
└─────────────┘ └──────┬──────┘ └─────────────┘
                       │
Wave-3                 ▼
                ┌─────────────┐
                │  P3-DETAIL  │
                │  详情页     │
                └──────┬──────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│  P3-INVEST  │ │  P3-AUCTION │ │  P3-ADMIN   │
│  投资页     │ │  拍卖页     │ │  管理后台   │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │
Wave-5 └───────────────┼───────────────┘
                       ▼
                ┌─────────────┐
                │   P4-E2E    │
                │ 端到端测试   │
                └─────────────┘
```

---

## 5. 任务详情

### Wave-0: 基础设施层（可并行）

---

#### Task: P1-INIT
**任务名称**: 前端项目初始化
**预估上下文**: ~25k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:12）
**执行实例**: Claude-Terminal-8472
**开始时间**: 2026-01-28 14:45
**完成时间**: 2026-01-28 15:12
**依赖**: 无

**预估依据**:
| 类型 | 内容 | tokens |
|------|------|--------|
| 配置文件 | package.json, vite.config.ts, tsconfig.json | 3k |
| 对话轮次 | 预估 8 轮 | 16k |
| 缓冲 | +25% | 5k |
| **小计** | | **~25k** |

**范围**:
- [x] 使用 Vite 初始化 React + TypeScript 项目
- [x] 配置 Ant Design 组件库
- [x] 配置 Tailwind CSS
- [x] 配置 React Router v6
- [x] 创建基础目录结构
- [x] 配置环境变量 (.env.example)

**新建文件清单**:
```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tsconfig.node.json
├── tailwind.config.js
├── postcss.config.js
├── index.html
├── .env.example
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── vite-env.d.ts
    └── index.css
```

**验收标准**:
- `pnpm dev` 启动成功
- Ant Design 组件可正常渲染
- Tailwind CSS 样式生效

---

#### Task: P2-CTRL
**任务名称**: 后端 Controller 层
**预估上下文**: ~45k tokens ✅
**状态**: ✅ 已完成（2026-01-28 14:56）
**执行实例**: Claude-Terminal-3847
**开始时间**: 2026-01-28 14:45
**完成时间**: 2026-01-28 14:56
**依赖**: 无

**预估依据**:
| 类型 | 文件 | 行数 | tokens |
|------|------|------|--------|
| 读取 | ProjectListing.java | 74 | 0.9k |
| 读取 | BlockchainService.java | 61 | 0.7k |
| 读取 | InvestmentProxyService.java | 152 | 1.8k |
| 新建 | ListingController.java | ~200 | 2.4k |
| 新建 | InvestmentController.java | ~100 | 1.2k |
| 新建 | AuctionController.java | ~100 | 1.2k |
| 新建 | WorkflowController.java | ~80 | 1.0k |
| 新建 | DTO 类 (5个) | ~150 | 1.8k |
| 新建 | CorsConfig.java | ~30 | 0.4k |
| 对话 | 预估 15 轮 | - | 30k |
| 缓冲 | +25% | - | 10k |
| **小计** | | | **~45k** |

**范围**:
- [x] 创建 ListingController (CRUD + 业务操作)
- [x] 创建 InvestmentController (投资 API)
- [x] 创建 AuctionController (拍卖 API)
- [x] 创建 WorkflowController (工作流任务 API)
- [x] 创建 DTO 类 (ListingDTO, CreateListingRequest 等)
- [x] 配置 CORS 跨域

**新建文件清单**:
```
server/src/main/java/com/collectorcoin/
├── controller/
│   ├── ListingController.java
│   ├── InvestmentController.java
│   ├── AuctionController.java
│   └── WorkflowController.java
├── dto/
│   ├── ListingDTO.java
│   ├── CreateListingRequest.java
│   ├── UpdateListingRequest.java
│   ├── InvestmentDTO.java
│   ├── EstimateRequest.java
│   └── InvestRequest.java
└── config/
    └── CorsConfig.java
```

**API 端点清单**:
| Method | Endpoint | 描述 |
|--------|----------|------|
| GET | `/api/listings` | 获取项目列表 |
| GET | `/api/listings/{id}` | 获取项目详情 |
| POST | `/api/listings` | 创建新项目 |
| PUT | `/api/listings/{id}` | 更新项目 |
| POST | `/api/listings/{id}/verify` | 验证项目 |
| POST | `/api/listings/{id}/estimate` | 价值评估 |
| POST | `/api/listings/{id}/invest` | 投资 |
| POST | `/api/listings/{id}/assign-restorer` | 分配修复者 |
| POST | `/api/listings/{id}/complete-restoration` | 完成修复 |
| POST | `/api/listings/{id}/post-auction` | 上架拍卖 |
| POST | `/api/listings/{id}/finalize` | 完成拍卖 |
| GET | `/api/workflow/tasks` | 获取待办任务 |
| POST | `/api/workflow/tasks/{id}/complete` | 完成任务 |

**验收标准**:
- 所有 API 端点可通过 Postman/curl 调用
- CORS 配置允许前端跨域访问
- DTO 与实体正确映射

---

### Wave-1: 核心功能层

---

#### Task: P1-WALLET
**任务名称**: MetaMask 钱包集成
**预估上下文**: ~35k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:22）
**执行实例**: Claude-Terminal-8472
**开始时间**: 2026-01-28 15:15
**完成时间**: 2026-01-28 15:22
**依赖**: P1-INIT

**预估依据**:
| 类型 | 内容 | tokens |
|------|------|--------|
| 新建 | useWallet.ts Hook | 1.5k |
| 新建 | ConnectWallet.tsx | 1.2k |
| 新建 | WalletStatus.tsx | 0.8k |
| 新建 | walletSlice.ts | 1.0k |
| 新建 | web3.ts 工具 | 0.8k |
| 对话 | 预估 12 轮 | 24k |
| 缓冲 | +25% | 7k |
| **小计** | | **~35k** |

**范围**:
- [x] 实现 useWallet Hook (连接/断开/状态)
- [x] 实现 ConnectWallet 组件
- [x] 实现 WalletStatus 组件
- [x] 创建 walletSlice (Redux 状态)
- [x] 网络切换处理 (Sepolia)
- [x] 交易签名流程封装

**新建文件清单**:
```
frontend/src/
├── hooks/
│   └── useWallet.ts
├── components/wallet/
│   ├── ConnectWallet.tsx
│   └── WalletStatus.tsx
├── store/slices/
│   └── walletSlice.ts
└── utils/
    └── web3.ts
```

**验收标准**:
- MetaMask 连接/断开正常
- 显示钱包地址和余额
- 网络切换提示正确

---

#### Task: P1-STORE
**任务名称**: Redux 状态管理
**预估上下文**: ~30k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:34）
**执行实例**: Claude-Terminal-5927
**开始时间**: 2026-01-28 15:28
**完成时间**: 2026-01-28 15:34
**依赖**: P1-INIT

**预估依据**:
| 类型 | 内容 | tokens |
|------|------|--------|
| 新建 | store/index.ts | 0.6k |
| 新建 | listingSlice.ts | 1.5k |
| 新建 | authSlice.ts | 1.0k |
| 新建 | workflowSlice.ts | 1.0k |
| 对话 | 预估 10 轮 | 20k |
| 缓冲 | +25% | 6k |
| **小计** | | **~30k** |

**范围**:
- [x] 配置 Redux Toolkit Store
- [x] 创建 listingSlice (项目列表状态)
- [x] 创建 authSlice (认证状态)
- [x] 创建 workflowSlice (工作流状态)
- [x] 配置 Redux DevTools

**新建文件清单**:
```
frontend/src/store/
├── index.ts
└── slices/
    ├── listingSlice.ts
    ├── authSlice.ts
    └── workflowSlice.ts
```

**验收标准**:
- Redux DevTools 可查看状态
- 状态更新正常触发组件重渲染

---

#### Task: P1-API
**任务名称**: API 客户端服务
**预估上下文**: ~25k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:57）
**执行实例**: Claude-Terminal-6391
**开始时间**: 2026-01-28 15:34
**完成时间**: 2026-01-28 15:57
**依赖**: P1-INIT

**预估依据**:
| 类型 | 内容 | tokens |
|------|------|--------|
| 新建 | api.ts (Axios 配置) | 0.8k |
| 新建 | listingService.ts | 1.2k |
| 新建 | investmentService.ts | 0.8k |
| 新建 | auctionService.ts | 0.8k |
| 新建 | types/*.ts | 1.5k |
| 对话 | 预估 8 轮 | 16k |
| 缓冲 | +25% | 5k |
| **小计** | | **~25k** |

**范围**:
- [x] 配置 Axios 实例 (baseURL, 拦截器)
- [x] 创建 listingService (项目 API)
- [x] 创建 investmentService (投资 API)
- [x] 创建 auctionService (拍卖 API)
- [x] 创建 workflowService (工作流 API)
- [x] 定义 TypeScript 类型（与后端 DTO 对齐）

**新建文件清单**:
```
frontend/src/
├── services/
│   ├── api.ts
│   ├── listingService.ts
│   ├── investmentService.ts
│   ├── auctionService.ts
│   └── workflowService.ts
└── types/
    ├── listing.ts
    ├── investment.ts
    └── auction.ts
```

**验收标准**:
- API 调用正常返回数据
- 错误处理统一拦截
- TypeScript 类型完整

---

#### Task: P2-SVC
**任务名称**: 后端服务层补全
**预估上下文**: ~35k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:03）
**执行实例**: Claude-Terminal-3847
**完成时间**: 2026-01-28 15:03
**依赖**: 无

**预估依据**:
| 类型 | 文件 | 行数 | tokens |
|------|------|------|--------|
| 读取 | BlockchainProxyService.java | 79 | 1.0k |
| 读取 | BlockchainServiceImpl.java | 79 | 1.0k |
| 读取 | WorkflowTaskService.java | 178 | 2.1k |
| 修改 | 补全 4 个代理方法 | ~60 | 0.7k |
| 修改 | 完善 4 个服务方法 | ~40 | 0.5k |
| 修改 | 实现 3 个 TODO 方法 | ~50 | 0.6k |
| 对话 | 预估 12 轮 | - | 24k |
| 缓冲 | +25% | - | 7k |
| **小计** | | | **~35k** |

**范围**:
- [x] BlockchainProxyService 补全方法:
  - `updateRestorer(tokenId, restorer)`
  - `markRestorationComplete(tokenId)`
  - `setItemForSale(tokenId, price)`
  - `transferToWinner(tokenId, winner)`
- [x] BlockchainServiceImpl 完善方法:
  - `updateNFTRestorer()` - 调用 proxyService
  - `markRestorationComplete()` - 调用 proxyService
  - `setNFTAuctionLive()` - 调用 proxyService
  - `transferNFTToWinner()` - 调用 proxyService
- [x] WorkflowTaskService 实现方法:
  - `findTaskByNameAndListing()` - 查询 JBPM
  - `findListingByProject()` - 查询 Repository
  - `findListingByTokenId()` - 查询 Repository

**修改文件清单**:
```
server/src/main/java/com/collectorcoin/service/
├── BlockchainProxyService.java   # 添加 4 个方法
├── BlockchainServiceImpl.java    # 完善 4 个方法
└── WorkflowTaskService.java      # 实现 3 个 TODO
```

**验收标准**:
- 所有方法实现完整，无 TODO
- 区块链交易可正常发送
- 工作流任务查询正常

---

### Wave-2: 页面基础层

---

#### Task: P3-LAYOUT
**任务名称**: 通用布局组件
**预估上下文**: ~40k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:29）
**执行实例**: Claude-Terminal-8472
**开始时间**: 2026-01-28 15:24
**完成时间**: 2026-01-28 15:29
**依赖**: P1-INIT

**范围**:
- [x] Header.tsx - 顶部导航栏
- [x] Footer.tsx - 页脚
- [x] Sidebar.tsx - 侧边栏菜单
- [x] Loading.tsx - 加载状态
- [x] MainLayout.tsx - 主布局容器
- [x] 配置路由 (routes.tsx)

**新建文件清单**:
```
frontend/src/
├── components/common/
│   ├── Header.tsx
│   ├── Footer.tsx
│   ├── Sidebar.tsx
│   ├── Loading.tsx
│   └── MainLayout.tsx
└── routes.tsx
```

**验收标准**:
- 布局响应式适配
- 路由切换正常

---

#### Task: P3-DASH
**任务名称**: Dashboard 页面
**预估上下文**: ~35k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:12）
**执行实例**: Claude-Terminal-7283
**开始时间**: 2026-01-28 16:09
**完成时间**: 2026-01-28 16:12
**依赖**: P3-LAYOUT

**范围**:
- [x] 平台统计卡片 (项目数、投资额等)
- [x] 最新项目列表
- [x] 快捷操作入口

**新建文件清单**:
```
frontend/src/pages/
└── Dashboard.tsx
```

**验收标准**:
- 统计数据正确显示
- 页面加载流畅

---

#### Task: P3-LIST
**任务名称**: Listings 列表页
**预估上下文**: ~45k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:06）
**执行实例**: Claude-Terminal-6391
**开始时间**: 2026-01-28 16:00
**完成时间**: 2026-01-28 16:06
**依赖**: P3-LAYOUT, P1-API

**范围**:
- [x] ListingCard.tsx - 项目卡片组件
- [x] Listings.tsx - 列表页面
- [x] 筛选/搜索功能
- [x] 分页功能

**新建文件清单**:
```
frontend/src/
├── components/listing/
│   └── ListingCard.tsx
└── pages/
    └── Listings.tsx
```

**验收标准**:
- 项目列表正确渲染
- 筛选/分页功能正常

---

### Wave-3: 核心页面层

---

#### Task: P3-DETAIL
**任务名称**: 项目详情页
**预估上下文**: ~50k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:12）
**执行实例**: Claude-Terminal-6391
**开始时间**: 2026-01-28 16:07
**完成时间**: 2026-01-28 16:12
**依赖**: P3-LIST

**范围**:
- [x] ListingDetail.tsx - 详情页面
- [x] 项目信息展示
- [x] NFT 状态展示
- [x] 投资进度展示
- [x] 操作按钮 (投资/拍卖入口)

**新建文件清单**:
```
frontend/src/
├── components/listing/
│   └── ListingDetail.tsx
└── pages/
    └── ListingDetailPage.tsx
```

**验收标准**:
- 详情数据正确展示
- NFT 状态实时更新

---

#### Task: P3-CREATE
**任务名称**: 创建项目页
**预估上下文**: ~45k tokens ✅
**状态**: ✅ 已完成（2026-01-28 15:56）
**执行实例**: Claude-Terminal-5927
**开始时间**: 2026-01-28 15:36
**完成时间**: 2026-01-28 15:56
**依赖**: P3-LAYOUT, P1-WALLET

**范围**:
- [x] ListingForm.tsx - 表单组件
- [x] CreateListing.tsx - 创建页面
- [x] 表单验证
- [ ] 图片上传 (IPFS) - 暂用URL输入替代

**新建文件清单**:
```
frontend/src/
├── components/listing/
│   └── ListingForm.tsx
└── pages/
    └── CreateListing.tsx
```

**验收标准**:
- 表单提交成功创建项目
- 验证规则正确

---

#### Task: P3-INVEST
**任务名称**: 投资页面
**预估上下文**: ~40k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:21）
**执行实例**: Claude-Terminal-6391
**开始时间**: 2026-01-28 16:15
**完成时间**: 2026-01-28 16:21
**依赖**: P3-DETAIL, P1-WALLET

**范围**:
- [x] InvestForm.tsx - 投资表单
- [x] InvestorList.tsx - 投资者列表
- [x] Investment.tsx - 投资页面
- [x] 钱包交易签名

**新建文件清单**:
```
frontend/src/
├── components/investment/
│   ├── InvestForm.tsx
│   └── InvestorList.tsx
└── pages/
    └── Investment.tsx
```

**验收标准**:
- 投资交易成功上链
- 投资者列表正确显示

---

### Wave-4: 高级功能层

---

#### Task: P3-AUCTION
**任务名称**: 拍卖页面
**预估上下文**: ~40k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:32）
**执行实例**: Claude-Terminal-6391
**开始时间**: 2026-01-28 16:23
**完成时间**: 2026-01-28 16:32
**依赖**: P3-DETAIL, P1-WALLET

**范围**:
- [x] BidForm.tsx - 出价表单
- [x] AuctionStatus.tsx - 拍卖状态
- [x] Auction.tsx - 拍卖页面

**新建文件清单**:
```
frontend/src/
├── components/auction/
│   ├── BidForm.tsx
│   └── AuctionStatus.tsx
└── pages/
    └── Auction.tsx
```

**验收标准**:
- 出价功能正常
- 拍卖状态实时更新

---

#### Task: P3-ADMIN
**任务名称**: 管理后台
**预估上下文**: ~55k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:04）
**执行实例**: Claude-Terminal-5927
**开始时间**: 2026-01-28 15:58
**完成时间**: 2026-01-28 16:04
**依赖**: P3-LAYOUT, P2-CTRL

**范围**:
- [x] AdminPanel.tsx - 管理主页
- [x] 项目审核功能
- [x] 工作流任务列表
- [x] 修复者分配功能

**新建文件清单**:
```
frontend/src/pages/
├── AdminPanel.tsx
└── RestorerPanel.tsx
```

**验收标准**:
- Admin 可审核项目
- 工作流任务可完成

---

### Wave-5: 集成测试层

---

#### Task: P4-E2E
**任务名称**: 端到端集成测试
**预估上下文**: ~60k tokens ✅
**状态**: ✅ 已完成（2026-01-28 16:59）
**执行实例**: Claude-Terminal-4821
**开始时间**: 2026-01-28 16:48
**完成时间**: 2026-01-28 16:59
**完成报告**: docs/features/COLLECTORCOIN-COMPLETION-REPORT.md
**依赖**: 所有前置任务

**范围**:
- [x] 前后端联调
- [x] 端到端流程测试
- [x] 问题修复（App.tsx 路由配置）
- [x] 演示脚本准备

**验收标准**:
按计划文档 6.1 节验收：

| 阶段 | 操作 | 验收标准 |
|:----:|------|----------|
| 1 | 创建新项目 | 前端表单提交，数据库记录创建 |
| 2 | 验证详情 | Admin 审核通过/拒绝 |
| 3 | 价值评估 | 链上铸造 dNFT，返回 tokenId |
| 4 | Bid Review | dNFT 上架市场 |
| 5 | Funding | 投资交易上链成功 |
| 6 | 分配修复者 | 修复者地址写入 NFT |
| 7 | 修复完成 | NFT 状态更新 |
| 8 | 上架拍卖 | 设置价格，NFT 可转移 |
| 9 | 拍卖出价 | 出价记录成功 |
| 10 | 完成拍卖 | NFT 转移给买家 |

---

## 6. 附录

### 6.1 预计新增代码行数

| 模块 | 文件类型 | 行数 |
|------|----------|:----:|
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
| | DTO 类 | 150 |
| | Service 补全 | 150 |
| | 配置 | 30 |
| **后端小计** | | **830** |
| **总计** | | **~3630** |

### 6.2 新建文件完整清单

**前端项目 (frontend/)**:
```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── index.html
├── .env.example
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── routes.tsx
    ├── index.css
    ├── components/
    │   ├── common/
    │   │   ├── Header.tsx
    │   │   ├── Footer.tsx
    │   │   ├── Sidebar.tsx
    │   │   ├── Loading.tsx
    │   │   └── MainLayout.tsx
    │   ├── wallet/
    │   │   ├── ConnectWallet.tsx
    │   │   └── WalletStatus.tsx
    │   ├── listing/
    │   │   ├── ListingCard.tsx
    │   │   ├── ListingForm.tsx
    │   │   └── ListingDetail.tsx
    │   ├── investment/
    │   │   ├── InvestForm.tsx
    │   │   └── InvestorList.tsx
    │   └── auction/
    │       ├── BidForm.tsx
    │       └── AuctionStatus.tsx
    ├── pages/
    │   ├── Dashboard.tsx
    │   ├── Listings.tsx
    │   ├── ListingDetailPage.tsx
    │   ├── CreateListing.tsx
    │   ├── Investment.tsx
    │   ├── Auction.tsx
    │   ├── AdminPanel.tsx
    │   └── RestorerPanel.tsx
    ├── store/
    │   ├── index.ts
    │   └── slices/
    │       ├── listingSlice.ts
    │       ├── authSlice.ts
    │       ├── walletSlice.ts
    │       └── workflowSlice.ts
    ├── services/
    │   ├── api.ts
    │   ├── listingService.ts
    │   ├── investmentService.ts
    │   └── auctionService.ts
    ├── hooks/
    │   └── useWallet.ts
    ├── utils/
    │   └── web3.ts
    └── types/
        ├── listing.ts
        ├── investment.ts
        └── auction.ts
```

**后端新建文件 (server/)**:
```
server/src/main/java/com/collectorcoin/
├── controller/
│   ├── ListingController.java
│   ├── InvestmentController.java
│   ├── AuctionController.java
│   └── WorkflowController.java
├── dto/
│   ├── ListingDTO.java
│   ├── CreateListingRequest.java
│   ├── UpdateListingRequest.java
│   ├── InvestmentDTO.java
│   ├── EstimateRequest.java
│   └── InvestRequest.java
└── config/
    └── CorsConfig.java
```

### 6.3 修改文件清单

```
server/src/main/java/com/collectorcoin/service/
├── BlockchainProxyService.java   # 添加 4 个方法
├── BlockchainServiceImpl.java    # 完善 4 个方法
└── WorkflowTaskService.java      # 实现 3 个 TODO
```

### 6.4 技术栈确认

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 前端框架 | React + TypeScript | 18.x |
| 构建工具 | Vite | 5.x |
| 状态管理 | Redux Toolkit | 2.x |
| UI 组件库 | Ant Design | 5.x |
| 样式方案 | Tailwind CSS | 3.x |
| 钱包集成 | ethers.js + MetaMask | 6.x |
| 后端语言 | Java (Spring Boot) | 17 / 3.x |
| 工作流引擎 | JBPM | 7.x |
| 区块链交互 | Web3j | 4.x |
| 智能合约 | Solidity (ERC-1155) | 0.8.20 |

### 6.5 执行说明

**启动执行**:
```bash
# 运行 /auto-dev 进入 B 模式开始执行
```

**并行执行建议**:
- Wave-0: P1-INIT 和 P2-CTRL 可并行（2 个 Claude）
- Wave-1: P1-WALLET, P1-STORE, P1-API, P2-SVC 可并行（4 个 Claude）
- Wave-2+: 根据依赖关系串行或部分并行

### 6.6 里程碑

| 里程碑 | 阶段 | 验收标准 |
|--------|------|----------|
| M1 | Wave-0 完成 | 前端项目可运行，后端 API 可调用 |
| M2 | Wave-1 完成 | 钱包可连接，状态管理正常 |
| M3 | Wave-2 完成 | 布局组件完成，Dashboard 可访问 |
| M4 | Wave-3 完成 | 核心页面开发完成 |
| M5 | Wave-4 完成 | 所有页面开发完成 |
| M6 | Wave-5 完成 | 端到端流程跑通 |

---

## 7. 文档信息

**生成时间**: 2026-01-28
**基于计划**: `docs/plan/IMPLEMENTATION-PLAN-v2.md`
**文档版本**: v1.0

---

**文档编写完成** ✅

---
