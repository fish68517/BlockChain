# CollectorCoin 演示脚本

> **版本**: 1.0
> **日期**: 2026-01-28
> **演示时长**: 约 15 分钟

---

## 演示概述

本演示展示 CollectorCoin 平台的完整 dNFT 生命周期流程。

---

## 前置准备

### 环境要求

- Java 17+
- Node.js 18+
- MySQL 8.0+
- MetaMask 钱包（Sepolia 测试网）

### 启动服务

```bash
# 1. 启动 MySQL
docker-compose up -d mysql

# 2. 启动后端服务
cd server && mvn spring-boot:run

# 3. 启动前端服务（新终端）
cd frontend && pnpm dev

# 4. 启动 IPFS（可选）
docker-compose up -d ipfs
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| 后端 API | http://localhost:8080/api |
| IPFS Gateway | http://localhost:8081 |

---

## 演示流程

### 第一部分：项目创建（3分钟）

**步骤 1.1：创建新项目**

```
演示要点：
- 填写项目标题：Vintage 1965 Ford Mustang
- 填写估值：$50,000
- 填写修复预算：$15,000
- 上传项目图片
```

**步骤 1.2：项目审核**

```
演示要点：
- 管理员审核项目详情
- 确认估值合理性
- 批准项目进入下一阶段
```

### 第二部分：NFT 铸造（3分钟）

**步骤 2.1：上传元数据到 IPFS**

```
演示要点：
- 系统自动生成 NFT 元数据
- 元数据上传到 IPFS
- 获取 IPFS CID
```

**步骤 2.2：铸造 dNFT**

```
演示要点：
- 调用智能合约 mintDNFT()
- 展示 MetaMask 交易确认
- 查看 Etherscan 交易记录
```

**步骤 2.3：启动 NFT**

```
演示要点：
- 调用 launchDNFT()
- NFT 进入市场可投资状态
```

### 第三部分：投资流程（3分钟）

**步骤 3.1：投资者参与**

```
演示要点：
- 投资者连接钱包
- 选择投资金额：5 ETH
- 确认投资交易
```

### 第四部分：修复流程（3分钟）

**步骤 4.1：分配修复师**

```
演示要点：
- 管理员选择修复师
- 调用 updateRestorer()
```

**步骤 4.2：完成修复**

```
演示要点：
- 修复师提交完成报告
- 调用 markRestorationComplete()
```

### 第五部分：拍卖与转移（3分钟）

**步骤 5.1：发布拍卖**

```
演示要点：
- 设置拍卖价格：75,000 USD
- 调用 setItemForSale()
```

**步骤 5.2：转移给获胜者**

```
演示要点：
- 拍卖结束，确定获胜者
- 调用 transferToWinner()
- 展示 NFT 所有权转移
```

---

## 演示数据

### 测试账户

| 角色 | 地址 |
|------|------|
| Admin | 0xAdmin... |
| Investor | 0xInvestor... |
| Restorer | 0xRestorer... |
| Winner | 0xWinner... |

### 合约地址

| 合约 | 地址 |
|------|------|
| CCMarketPlace | 见 .env 配置 |

---

## 常见问题

**Q: 交易失败怎么办？**
A: 检查 Gas 费用是否充足，确认网络连接正常。

**Q: IPFS 上传失败？**
A: 检查 IPFS 节点是否运行，或使用 Mock 模式。
