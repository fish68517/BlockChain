# CollectorCoin 平台操作指南

## 目录
1. [环境准备](#环境准备)
2. [启动服务](#启动服务)
3. [用户注册](#用户注册)
4. [用户登录](#用户登录)
5. [角色权限](#角色权限)
6. [创建NFT Listing](#创建nft-listing)
7. [MetaMask钱包导入](#metamask钱包导入)
8. [常见问题](#常见问题)

---

## 环境准备

### 系统要求
- Node.js v16+
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- MetaMask 浏览器插件

### 数据库配置
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/collectorcoin
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 启动服务

### 1. 启动 Hardhat 本地区块链
```bash
cd contracts
npx hardhat node
```
> 保持终端运行，默认地址: http://localhost:8545

### 2. 部署智能合约
```bash
cd contracts
npx hardhat run scripts/deploy.js --network localhost
```
> 记录输出的合约地址，更新到 `application.properties` 中的 `blockchain.contract.address`

### 3. 启动后端服务
```bash
cd server
mvn spring-boot:run
```
> 后端API: http://localhost:8080

### 4. 启动前端服务
```bash
cd frontend
npm run dev
```
> 前端页面: http://localhost:3000

---

## 用户注册

### 步骤
1. 访问 http://localhost:3000/register
2. 填写以下信息：
   - **用户名**: 唯一标识
   - **邮箱**: 可选
   - **密码**: 登录密码
3. 点击 **Register** 按钮

### 注册成功后
系统会自动：
- 生成一个新的以太坊钱包
- 从主钱包转账 **1 ETH** 到你的钱包（可在配置文件修改）
- 密码使用BCrypt加密存储
- 默认角色为 **USER**

### ⚠️ 重要
**请保存好你的私钥！** 你可以用它导入MetaMask来使用钱包。

### 配置初始ETH数量
```properties
# application.properties
user.initial.eth=1.0
```

---

## 用户登录

### 步骤
1. 访问 http://localhost:3000/login
2. 输入用户名和密码
3. 点击 **Login** 按钮

### 登录后
- 页面右上角显示用户名和角色标签
- 侧边栏显示钱包地址和ETH余额
- 可点击 **Logout** 退出登录

### JWT认证
系统使用JWT（JSON Web Token）进行身份认证：
- Token有效期：24小时
- Token存储在浏览器localStorage
- 所有API请求自动携带Token

---

## 角色权限

### 角色类型
| 角色 | 说明 | 可访问页面 |
|------|------|------------|
| **ADMIN** | 管理员 | 所有页面 |
| **OWNER** | 藏品所有者 | Dashboard, Listings, Create Listing, My Items |
| **RESTORER** | 修复师 | Dashboard, Listings, Restoration Tasks |
| **INVESTOR** | 投资者 | Dashboard, Listings, My Investments |
| **BUYER** | 买家 | Dashboard, Listings, My Purchases |
| **USER** | 普通用户 | Dashboard, Listings |

### 管理员账户
```
用户名: Tulip
密码: mmoo20031003
角色: ADMIN
```

### 设置用户角色（数据库）
```sql
UPDATE users SET role = 'OWNER' WHERE username = 'xxx';
```

---

## 创建NFT Listing

### 前提条件
- 已登录系统
- MetaMask 已连接到 Localhost:8545 网络

### 步骤
1. 点击页面上的 **Create New Listing** 按钮
2. 填写 NFT 信息：
   - **Name**: NFT 名称
   - **Description**: NFT 描述
   - **Price**: 价格 (ETH)
   - **Upload Image**: 上传图片
3. 点击 **Submit** 提交

### 处理流程
1. 图片上传到 IPFS（当前为模拟模式）
2. 创建 Listing 记录
3. 触发工作流（当前为简化模式）
4. NFT 等待铸造

---

## MetaMask钱包导入

### 添加 Localhost 网络
1. 打开 MetaMask
2. 点击网络下拉菜单 → **添加网络**
3. 填写信息：
   - **网络名称**: Localhost 8545
   - **RPC URL**: http://localhost:8545
   - **链 ID**: 31337
   - **货币符号**: ETH
4. 点击保存

### 导入注册生成的钱包
1. 在 MetaMask 中点击头像 → **导入账户**
2. 选择 **私钥**
3. 粘贴注册时显示的私钥
4. 点击 **导入**

### 导入管理员钱包（可选）
管理员钱包（Hardhat Account #0）：
```
地址: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
私钥: 0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80
```

---

## 常见问题

### Q: 注册失败显示 "钱包地址已存在"
**A**: 每个钱包地址只能绑定一个用户。系统现在自动生成钱包，不会有这个问题。

### Q: 登录失败
**A**: 检查用户名和密码是否正确。如果忘记密码，需要重新注册。

### Q: MetaMask 无法连接
**A**: 
1. 确保 Hardhat 节点正在运行
2. 确保 MetaMask 网络切换到 Localhost 8545
3. 刷新页面重试

### Q: ETH 余额为 0
**A**: 
- 检查 Hardhat 节点是否运行
- 管理员钱包需要有足够的 ETH
- 查看后端日志确认转账是否成功

### Q: 创建 Listing 失败
**A**: 
1. 确保已连接 MetaMask
2. 检查网络是否正确
3. 查看后端日志获取详细错误信息

---

## 技术架构

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   React 前端    │────▶│  Spring Boot    │────▶│   MySQL 数据库  │
│  (localhost:3000)│     │ (localhost:8080)│     │                 │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  Hardhat Node   │
                        │ (localhost:8545)│
                        │  智能合约       │
                        └─────────────────┘
```

---

## 联系方式

如有问题，请联系开发团队。
