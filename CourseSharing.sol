// SPDX-License-Identifier: MIT
// 指定开源许可证

// 定义Solidity编译器版本。^0.8.0表示使用0.8.0及以上但小于0.9.0的版本
pragma solidity ^0.8.0;

/**
 * @title CourseSharing
 * @dev 这是我们的课程资源共享平台的核心智能合约
 */
contract CourseSharing {

    // --- 数据结构 ---

    // 定义一个结构体来存储每个课程资源的信息
    struct Resource {
        uint id;                // 资源的唯一ID
        string name;            // 资源名称
        string ipfsHash;        // 指向IPFS上加密文件的哈希地址
        address payable owner;  // 资源所有者的地址（'payable'表示该地址可以接收ETH）
        uint price;             // 访问资源的价格（单位：wei，以太坊最小单位）
    }

    // --- 状态变量 ---

    // 使用 mapping 存储所有资源。可以理解为一个数据库表，用ID作为主键
    mapping(uint => Resource) public resources;
    // 记录已创建的资源总数，也用作新资源的ID
    uint public resourceCount = 0;

    // 嵌套mapping，用于记录权限：哪个用户(address)是否有权访问哪个资源(uint)
    // 结构：permissions[资源ID][用户地址] => true/false
    mapping(uint => mapping(address => bool)) public accessPermissions;

    // --- 事件 ---
    // 当状态发生重要变化时，触发事件可以方便外部应用（如网页前端）监听和响应

    event ResourceAdded(uint id, string name, address owner);
    event AccessGranted(uint indexed resourceId, address indexed user);

    // --- 函数 ---

    /**
     * @dev 允许用户上传一个新资源的信息到区块链上
     * @param _name 资源名称
     * @param _ipfsHash 资源的IPFS哈希
     * @param _price 资源访问价格 (单位: wei)
     */
    function addResource(string memory _name, string memory _ipfsHash, uint _price) public {
        // 1. 资源总数加一
        resourceCount++;
        // 2. 创建一个新的 Resource 实例并存储
        resources[resourceCount] = Resource(resourceCount, _name, _ipfsHash, payable(msg.sender), _price);
        // 3. 默认情况下，上传者对自己上传的资源拥有访问权限
        accessPermissions[resourceCount][msg.sender] = true;
        // 4. 触发“资源已添加”事件
        emit ResourceAdded(resourceCount, _name, msg.sender);
    }

    /**
     * @dev 允许用户支付费用来购买某个资源的访问权限
     * @param _resourceId 想要访问的资源ID
     */
    function grantAccess(uint _resourceId) public payable {
        // 使用 require 进行条件检查，如果条件不满足，交易将失败回滚

        // 检查1: 资源ID必须是有效的
        require(_resourceId > 0 && _resourceId <= resourceCount, "Resource not found");
        
        // 从存储中读取资源信息
        Resource storage resource = resources[_resourceId];

        // 检查2: 用户支付的ETH (msg.value) 必须等于资源设定的价格
        require(msg.value == resource.price, "Please pay the correct price");

        // 检查3: 确保用户不是重复购买
        require(!accessPermissions[_resourceId][msg.sender], "You already have access");

        // 核心步骤1: 将用户支付的ETH转给资源的所有者（实现公平分配）
        (bool success, ) = resource.owner.call{value: msg.value}("");
        require(success, "Failed to send Ether");

        // 核心步骤2: 授予调用此函数的用户访问权限
        accessPermissions[_resourceId][msg.sender] = true;

        // 触发“已授予权限”事件
        emit AccessGranted(_resourceId, msg.sender);
    }

    /**
     * @dev 检查一个用户是否拥有访问特定资源的权限
     * 这个是 view 函数，只读不写，调用它不花费任何Gas费 
     */
    function hasAccess(uint _resourceId, address _user) public view returns (bool) {
        // 检查资源ID是否有效
        if (_resourceId == 0 || _resourceId > resourceCount) {
            return false;
        }
        return accessPermissions[_resourceId][_user];
    }
}