// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8.0;

import "./Ownable.sol";
import "./CCToken.sol";
import "./SafeMath.sol";

interface StableToken
{
    function balanceOf(address account) external view returns (uint256);
    function transfer(address recipient, uint256 amount) external returns (bool);
    function transferFrom(
        address sender,
        address recipient,
        uint256 amount
    ) external returns (bool);
}

/** 
 * @title CC Swap Token
 * @dev Implements Collector Coin Swap Token facility
 */
contract CCSwapToken is Ownable, ERC20 {
    using SafeMath for uint256;
    
    address public tokenContract;
    StableToken token;
    
    modifier nonZeroAddress()
    {
        require(msg.sender != address(0));
        _;
    }
    
    event Deposit(
        address indexed sender,
        uint256 indexed amount
    );
    
    event Redeem(
        address indexed sender,
        uint256 indexed amount
    );
    
    constructor(address _tokenContract)
        ERC20("Collector-Coin Token", "CCT")
    {
        tokenContract = _tokenContract;
        token = StableToken(tokenContract);
    }
    
    /**
     * @dev Deposits funds to receive Collector-Coin
     */
    function deposit(uint256 _amount)
        public
        payable
        nonZeroAddress
    {
        require(token.balanceOf(msg.sender) >= _amount, "insufficient balance");
        require(token.transferFrom(msg.sender, address(this), _amount), "transfer did not complete");
        _mint(msg.sender, _amount);
        emit Deposit(msg.sender, _amount);
    }
    
    /**
     * @dev Redeems Collector-Coin tokens for ether
     */
    function redeem(uint256 _amount)
        public
        payable
        nonZeroAddress
    {
        require(_amount <= balanceOf(msg.sender), "trying to redeem more than your balance");
        uint256 balanceBefore = balanceOf(msg.sender);
        _burn(msg.sender, _amount);
        require(balanceOf(msg.sender) == balanceBefore.sub(_amount), "balance accounting failed");
        //require(token.transferFrom(address(this), msg.sender, _amount), "transfer did not complete");
        require(token.transfer(msg.sender, _amount), "transfer did not complete");
        emit Redeem(msg.sender, _amount);
    }
    
    /**
     * @dev Returns balance of ether on contract
     */
    function getBalance()
        public
        view
        returns (uint256)
    {
        return address(this).balance;
    }
    
    function updateTokenContract(address _newAddress)
        public
        onlyOwner
        nonZeroAddress
        returns (bool)
    {
        tokenContract = _newAddress;
        token = StableToken(tokenContract);
        return true;
    }
    
    /**
     * @dev Returns deletes contract 
     */
    function kill()
        public
        onlyOwner
        nonZeroAddress
    {
        selfdestruct(payable(msg.sender));
    }
    
}