# CollectorCoin 完整使用指南

> **版本**: 2.0
> **更新日期**: 2026-01-30
> **演示时长**: 约 25-35 分钟
> **测试状态**: ✅ 已验证完整流程（含 Restorer Bidding）

---

## 重要配置说明

**演示模式配置** (`server/src/main/resources/application.properties`)：

```properties
# 推荐：使用 Demo 模式（不实际调用链上合约）
blockchain.enabled=false

# 或者：启用真实区块链交互（需要正确配置合约）
# blockchain.enabled=true
```

> ⚠️ **注意**：如果 `blockchain.enabled=true`，请确保智能合约已正确部署且 tokenId 正确同步，否则结束拍卖时可能出现 `ERC1155InsufficientBalance` 错误。

---

## 目录

1. [环境准备](#1-环境准备)
2. [启动所有服务](#2-启动所有服务)
3. [MetaMask 钱包配置](#3-metamask-钱包配置)
4. [用户注册与登录](#4-用户注册与登录)
5. [完整业务流程演示](#5-完整业务流程演示)
   - 5.1 创建 Listing + Owner 估值（Owner 操作）
   - 5.2 验证项目（Admin 操作）
   - 5.3 价值评估 + 铸造 NFT（Admin 操作）
   - 5.4 上架市场（Admin 操作）
   - 5.5 修复师竞价（Restorer 操作）
   - 5.6 选择修复师（Admin 操作）
   - 5.7 投资（Investor 操作）
   - 5.8 完成修复（Restorer 操作）
   - 5.9 开始拍卖（Admin 操作）
   - 5.10 拍卖出价（Buyer 操作）
   - 5.11 结束拍卖 + 转移 NFT（Admin 操作）
6. [查看结果](#6-查看结果)
7. [CCToken 说明](#7-cctoken-说明)

---

## 1. 环境准备

### 1.1 系统要求

| 软件     | 版本   | 说明           |
| -------- | ------ | -------------- |
| Java     | 17+    | 后端运行环境   |
| Node.js  | 18+    | 前端和智能合约 |
| MySQL    | 8.0+   | 数据库         |
| pnpm     | 最新版 | 前端包管理器   |
| MetaMask | 最新版 | 浏览器钱包插件 |

### 1.2 创建数据库

```sql
-- 在 MySQL 中执行
CREATE DATABASE IF NOT EXISTS collectorcoin
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

---

## 2. 启动所有服务

### 2.1 启动 Hardhat 本地区块链

打开**第一个终端**：

```bash
cd contracts
npx hardhat node
```

> ✅ 看到 "Started HTTP and WebSocket JSON-RPC server at http://127.0.0.1:8545/" 表示成功

### 2.2 部署智能合约

打开**第二个终端**：

```bash
cd contracts
npx hardhat run scripts/deploy.js --network localhost

如果上述运行失败请用下面的指令
# 1. 部署合约
npx hardhat run scripts/deploy-cc.js --network localhost
# (记下这里输出的新的 Factory 地址！)

# 2. 移交权限 (必做，否则后端没法审批)
npx hardhat run scripts/transfer-factory-ownership.js --network localhost
```

> ✅ 记录输出的合约地址，例如：`0x5FbDB2315678afecb367f032d93F642f64180aa3`

### 2.3 更新合约地址配置

编辑 `server/src/main/resources/application.properties`：

```properties
blockchain.contract.marketplace.address=你的合约地址
```

### 2.4 启动后端服务

打开**第三个终端**：

```bash
cd server
mvn spring-boot:run

mvn clean package -DskipTests
./launch.sh 

npm run watch
```

> ✅ 看到 "Started CollectorCoinApplication" 表示成功
>
> 后端地址：http://localhost:8080

### 2.5 启动前端服务

打开**第四个终端**：

```bash
cd frontend
pnpm dev
```

> ✅ 看到 "VITE ready" 表示成功
>
> 前端地址：http://localhost:3000

---

## 3. MetaMask 钱包配置

### 3.1 添加本地网络

1. 打开 MetaMask 插件
2. 点击网络下拉菜单 → **添加网络** → **手动添加网络**
3. 填写以下信息：

| 字段     | 值                    |
| -------- | --------------------- |
| 网络名称 | Localhost 8545        |
| RPC URL  | http://localhost:8545 |
| 链 ID    | 31337                 |
| 货币符号 | ETH                   |

4. 点击**保存**

### 3.2 导入测试账户

Hardhat 提供了 20 个预充值账户，每个有 10000 ETH。

**Admin 账户（Account #0）**：

```
地址: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
私钥: 0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80
```

**投资者账户（Account #1）**：

```
地址: 0x70997970C51812dc3A010C7d01b50e0d17dc79C8
私钥: 0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d
```

**修复师账户（Account #2）**：

```
地址: 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC
私钥: 0x5de4111afa1a4b94908f83103eb1f1706367c2e68ca870fc3fb9a804cdab365a
```

**买家账户（Account #3）**：

```
地址: 0x90F79bf6EB2c4f870365E785982E1f101E93b906
私钥: 0x7c852118294e51e653712a81e05800f419141751be58f605c371e15141b007a6
```

**导入方法**：

1. MetaMask → 点击头像 → **导入账户**
2. 选择**私钥**
3. 粘贴私钥 → **导入**

---

## 4. 用户注册与登录

### 4.1 创建 Admin 账户（首次使用）

> ⚠️ **首次使用时，数据库中没有预置 Admin 用户，需要手动创建！**

**方法一：注册后通过数据库升级角色**

1. 先注册一个普通用户
2. 使用 MySQL 将其升级为 ADMIN：

```sql
-- 连接数据库
mysql -u root -p123456 collectorcoin

-- 将用户升级为 ADMIN
UPDATE users SET role = 'ADMIN' WHERE username = '你的用户名';

-- 验证
SELECT id, username, role FROM users;
```

**方法二：使用已存在的测试账户**

如果数据库中已有用户，可以直接使用：

- 用户名：`testadmin`（或你注册的用户名）
- 密码：注册时设置的密码

### 4.2 注册新用户

1. 访问 http://localhost:3000/register
2. 填写：
   - 用户名（唯一）
   - 密码
3. 点击 **Register**

> ✅ 注册成功后，系统会自动为用户生成一个以太坊钱包

### 4.3 登录系统

1. 访问 http://localhost:3000/login
2. 输入用户名和密码
3. 点击 **Login**

> ✅ 登录后左侧边栏会显示用户信息和钱包余额

---

## 5. 完整业务流程演示

### 流程总览

```
Owner提交项目 → 验证 → 估值(铸造NFT) → 上架 → 修复师竞价 → 选择修复师 → 投资 → [达标自动分配] → 修复完成 → 拍卖 → 转移NFT
      ↓           ↓          ↓           ↓          ↓            ↓          ↓           ↓              ↓         ↓        ↓
   PENDING → VERIFIED → ESTIMATED → LAUNCHED → (竞价中) →    FUNDING   → FUNDING → RESTORING →   RESTORED → AUCTION → SOLD
```

**关键变化**：

- Owner 提交时需填写自己的估值（供 Admin 参考）
- 修复师需要竞价，Admin 选择最低价
- 投资达到 funding target 后**自动分配**修复师
- 使用 CCToken (CCT) 进行投资和交易，ETH 仅用于 gas fee

---

### 5.1 创建 Listing + Owner 估值（Owner 操作）

**操作者**：Owner 或 Admin

**步骤**：

1. 登录系统
2. 点击左侧菜单 **Create Listing** 或首页的 **Create New Listing** 按钮
3. 填写项目信息：

| 字段                                  | 示例值                                      | 说明                 |
| ------------------------------------- | ------------------------------------------- | -------------------- |
| Title                                 | 1965 Ford Mustang Fastback                  | 项目标题             |
| Description                           | Classic muscle car, needs full restoration. | 项目描述             |
| VIN (可选)                            | 5F09A123456                                 | 车辆识别号           |
| Image                                 | 上传一张汽车图片                            | 项目图片             |
| **Estimated Final Value (CCT)** | 5.0                                         | Owner 估计的最终价值 |
| **Estimated Repair Cost (CCT)** | 1.5                                         | Owner 估计的修复成本 |

4. 点击 **Create Listing**

> ✅ 成功后跳转到 Listings 页面，状态为 **PENDING**
>
> ⚠️ Owner 的估值仅供 Admin 参考，Admin 会在估值阶段给出最终估值

---

### 5.2 验证项目（Admin 操作）

**操作者**：Admin

**步骤**：

1. 点击左侧菜单 **Admin Panel**
2. 找到刚创建的 Listing，状态为 PENDING
3. 点击 **Verify** 按钮

> ✅ 成功后状态变为 **VERIFIED**

---

### 5.3 价值评估 + 铸造 NFT（Admin 操作）

**操作者**：Admin

**步骤**：

1. 在 Admin Panel 中，找到状态为 VERIFIED 的 Listing
2. 点击 **Estimate** 按钮
3. 参考 Owner 提交的估值，填写最终估值：

| 字段              | 示例值 | 说明                            |
| ----------------- | ------ | ------------------------------- |
| Value (CCT)       | 5.0    | 项目最终估值（参考 Owner 估值） |
| Repair Cost (CCT) | 1.5    | 修复成本（参考 Owner 估值）     |

4. 点击 **OK**

> ✅ 系统自动执行：
>
> - 生成 NFT 元数据
> - 上传到 IPFS（Mock 模式）
> - 调用智能合约 `mintDNFT()` 铸造 NFT
> - 状态变为 **ESTIMATED**
> - NFT 标签显示 **Minted**

**后端日志会显示**：

```
[DEMO MODE] Mock dNFT minted with tokenId: 1001
```

---

### 5.4 上架市场（Admin 操作）

**操作者**：Admin

**步骤**：

1. 在 Admin Panel 中，找到状态为 ESTIMATED 的 Listing
2. 点击 **Launch** 按钮

> ✅ 系统调用 `launchDNFT()` 将 NFT 上架市场
>
> - 状态变为 **LAUNCHED**
> - NFT 标签显示 **Launched**

**现在修复师可以开始竞价了！**

---

### 5.5 修复师竞价（Restorer 操作）

**操作者**：Restorer（修复师角色用户）

**步骤**：

1. 登录修复师账户
2. 点击左侧菜单 **Listings**
3. 找到状态为 LAUNCHED 的项目
4. 点击 **Bid** 按钮（或进入详情页）
5. 填写竞价信息：

| 字段             | 示例值 | 说明                     |
| ---------------- | ------ | ------------------------ |
| Bid Amount (CCT) | 1.2    | 修复师愿意接受的修复费用 |

6. 点击 **Submit Bid**

> ✅ 竞价被记录，多个修复师可以竞价
>
> 💡 **竞价策略**：出价越低越有可能被选中

---

### 5.6 选择修复师（Admin 操作）

**操作者**：Admin

**步骤**：

1. 点击左侧菜单 **Admin Panel**
2. 找到状态为 LAUNCHED 且有竞价的 Listing
3. 查看所有修复师的竞价（按价格升序排列）
4. 点击最低价竞价旁的 **Select** 按钮

> ✅ 系统执行：
>
> - 选中的竞价状态变为 SELECTED
> - 其他竞价状态变为 REJECTED
> - Listing 的 fundingTarget 设为选中的竞价金额
> - 状态变为 **FUNDING**
>
> ⚠️ **注意**：此时修复师还未正式分配，需要等投资达标后自动分配

---

### 5.7 投资（Investor 操作）

**操作者**：Investor（或任何登录用户）

**步骤**：

1. 点击左侧菜单 **Listings**
2. 找到状态为 FUNDING 的项目
3. 点击项目卡片进入详情页
4. 或直接点击 **Invest** 按钮
5. 进入投资页面后：

| 字段                    | 示例值 |
| ----------------------- | ------ |
| Investment Amount (CCT) | 0.5    |

6. 点击 **Invest**

> ✅ 系统调用 `addInvestor()` 记录投资
>
> 💡 **自动分配机制**：当总投资金额达到 fundingTarget 时：
>
> - 状态自动变为 **FUNDED**
> - 系统自动调用 `updateRestorer()` 分配修复师
> - 状态变为 **RESTORING**

**可以多次投资，所有投资者都会被记录在 NFT 元数据中**

---

### 5.8 完成修复（Restorer 操作）

**操作者**：Admin 或 Restorer

**步骤**：

1. 点击左侧菜单 **Restorer Panel**
2. 找到状态为 RESTORING 的项目
3. 点击 **Complete** 按钮

> ✅ 系统调用 `markRestorationComplete()` 标记修复完成
>
> - 状态变为 **RESTORED**

---

### 5.9 开始拍卖（Admin 操作）

**操作者**：Admin

**步骤**：

1. 点击左侧菜单 **Admin Panel**
2. 找到状态为 RESTORED 的 Listing
3. 点击 **Start Auction** 按钮
4. 输入拍卖价格：

| 字段                | 示例值 |
| ------------------- | ------ |
| Auction Price (CCT) | 8.0    |

5. 点击 **OK**

> ✅ 系统调用 `setItemForSale()` 设置拍卖价格并解锁 NFT 转移
>
> - 状态变为 **AUCTION**

---

### 5.10 拍卖出价（Buyer 操作）

**操作者**：任何登录用户

**步骤**：

1. 点击左侧菜单 **Listings**
2. 找到状态为 AUCTION 的项目
3. 点击进入详情页，或点击 **Bid** 按钮
4. 进入拍卖页面：

| 字段             | 示例值 |
| ---------------- | ------ |
| Bid Amount (CCT) | 8.5    |

5. 点击 **Place Bid**

> ✅ 出价被记录，可以多人出价

---

### 5.11 结束拍卖 + 转移 NFT（Admin 操作）

**操作者**：Admin

**步骤**：

1. 点击左侧菜单 **Admin Panel**
2. 找到状态为 AUCTION 的 Listing
3. 点击 **Finalize** 按钮
4. 输入获胜者钱包地址：

```
0x90F79bf6EB2c4f870365E785982E1f101E93b906
```

5. 点击 **OK**

> ✅ 系统执行：
>
> - 调用 `redistributeFunds()` 分配收益给投资者
> - 调用 `transferToWinner()` 将 NFT 转移给获胜者
> - 状态变为 **SOLD**

---

## 6. 查看结果

### 6.1 查看 Listing 状态

- 访问 **Listings** 页面，查看所有项目状态
- 状态流转：PENDING → VERIFIED → ESTIMATED → LAUNCHED → FUNDING → RESTORING → RESTORED → AUCTION → SOLD

### 6.2 查看后端日志

后端控制台会显示所有区块链操作日志：

```
[DEMO MODE] Mock dNFT minted with tokenId: 1001
[DEMO MODE] Mock dNFT launched: 1001
[DEMO MODE] Mock investor 0x70997970C51812dc3A010C7d01b50e0d17dc79C8 added to token 1001
[DEMO MODE] Mock restorer 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC set for token 1001
[DEMO MODE] Mock restoration complete for token 1001
[DEMO MODE] Mock auction live for token 1001 at price 8000000000000000000
[DEMO MODE] Mock token 1001 transferred to 0x90F79bf6EB2c4f870365E785982E1f101E93b906
```

### 6.3 查看我的资产

| 页面           | 说明             |
| -------------- | ---------------- |
| My Items       | 我创建的 Listing |
| My Investments | 我的投资记录     |
| My Purchases   | 我购买的 NFT     |

---

## 常见问题

### Q: 页面显示空白或无法加载？

**A**: 检查后端服务是否正常运行，查看浏览器控制台是否有错误。

### Q: 登录失败显示 "Invalid username or password"？

**A**:

1. 确认用户名和密码正确
2. 如果用户不存在，需要先注册
3. **注意**：内置 Admin 账户（Tulip）可能密码编码不正确，建议注册新用户后通过数据库升级为 ADMIN

### Q: 按钮点击无反应？

**A**: 检查当前用户角色是否有权限执行该操作。普通 USER 角色无法访问 Admin Panel。

### Q: 状态没有更新？

**A**: 刷新页面，或检查后端日志是否有错误。

### Q: 结束拍卖时出现 500 错误或 "ERC1155InsufficientBalance"？

**A**: 这是因为 `blockchain.enabled=true` 但链上 NFT 没有正确铸造。解决方案：

1. 编辑 `server/src/main/resources/application.properties`
2. 设置 `blockchain.enabled=false`
3. 重启后端服务
4. 重新创建一个 listing 测试

### Q: MetaMask 无法连接？

**A**:

1. 确保 Hardhat 节点正在运行
2. 确保 MetaMask 网络切换到 Localhost 8545
3. 刷新页面重试

### Q: 如何查看后端日志？

**A**: 后端日志会显示在运行 `mvn spring-boot:run` 的终端窗口中，包含所有 API 调用和区块链操作的详细信息。

---

## 角色权限速查表

| 操作            | ADMIN | OWNER | INVESTOR | RESTORER | BUYER |
| --------------- | ----- | ----- | -------- | -------- | ----- |
| 创建 Listing    | ✅    | ✅    | ❌       | ❌       | ❌    |
| 验证项目        | ✅    | ❌    | ❌       | ❌       | ❌    |
| 估值 + 铸造 NFT | ✅    | ❌    | ❌       | ❌       | ❌    |
| 上架市场        | ✅    | ❌    | ❌       | ❌       | ❌    |
| 修复师竞价      | ❌    | ❌    | ❌       | ✅       | ❌    |
| 选择修复师      | ✅    | ❌    | ❌       | ❌       | ❌    |
| 投资            | ✅    | ✅    | ✅       | ✅       | ✅    |
| 完成修复        | ✅    | ❌    | ❌       | ✅       | ❌    |
| 开始拍卖        | ✅    | ❌    | ❌       | ❌       | ❌    |
| 出价            | ✅    | ✅    | ✅       | ✅       | ✅    |
| 结束拍卖        | ✅    | ❌    | ❌       | ❌       | ❌    |

---

## 测试账户汇总

> ⚠️ **推荐**：注册新用户后通过数据库升级为 ADMIN，而不是使用内置账户

| 角色   | 创建方式          | 说明                                                    |
| ------ | ----------------- | ------------------------------------------------------- |
| Admin  | 注册 + 数据库升级 | `UPDATE users SET role='ADMIN' WHERE username='xxx';` |
| 投资者 | 使用 Hardhat 地址 | 0x70997970C51812dc3A010C7d01b50e0d17dc79C8              |
| 修复师 | 使用 Hardhat 地址 | 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC              |
| 买家   | 使用 Hardhat 地址 | 0x90F79bf6EB2c4f870365E785982E1f101E93b906              |

---

## 快速测试脚本

如果你想快速验证系统是否正常，可以使用 PowerShell 运行以下命令：

```powershell
# 登录
$result = Invoke-RestMethod -Uri "http://localhost:8080/api/users/login" -Method Post -Body '{"username":"你的用户名","password":"你的密码"}' -ContentType "application/json"
$TOKEN = $result.token
Write-Host "登录成功，角色: $($result.role)"

# 查看所有 listings
$headers = @{Authorization="Bearer $TOKEN"}
Invoke-RestMethod -Uri "http://localhost:8080/api/listings" -Headers $headers
```

---

**祝演示顺利！** 🎓

---

## 更新日志

- **v2.0 (2026-01-30)**:

  - 新增 CCToken (ERC-20) 作为平台货币
  - 新增 Owner 估值功能（ownerValueEstimation, ownerRepairEstimation）
  - 新增修复师竞价系统（Restorer Bidding）
  - 新增投资达标自动分配修复师功能
  - 更新业务流程图和步骤说明
  - 更新角色权限速查表
- **v1.1 (2026-01-30)**:

  - 添加 Demo 模式配置说明
  - 修复 Admin 账户创建说明
  - 添加常见问题解决方案
  - 添加快速测试脚本

---

## 7. CCToken 说明

### 7.1 什么是 CCToken？

CCToken (CCT) 是 CollectorCoin 平台的原生 ERC-20 代币，用于：

- 投资项目
- 修复师竞价
- 拍卖出价
- 收益分配

**ETH 仅用于支付 gas fee（交易手续费）**

### 7.2 CCToken 合约信息

| 属性       | 值                                  |
| ---------- | ----------------------------------- |
| 名称       | Collector Coin Token                |
| 符号       | CCT                                 |
| 精度       | 18 decimals                         |
| 初始供应量 | 1,000,000 CCT                       |
| 合约文件   | `contracts/contracts/CCToken.sol` |

### 7.3 CCToken 功能

```solidity
// 铸造新代币（仅 Owner）
function mint(address to, uint256 amount) external onlyOwner

// 销毁代币（仅 Owner）
function burn(address from, uint256 amount) external onlyOwner

// 投资转账（仅 Owner，用于代理投资）
function transferForInvestment(address investor, address projectWallet, uint256 amount) external onlyOwner
```

### 7.4 部署 CCToken

CCToken 会在部署脚本中自动部署：

```bash
cd contracts
npx hardhat run scripts/deploy.js --network localhost
```

部署后会输出 CCToken 合约地址，需要更新到后端配置中。
