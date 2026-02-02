// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title CCToken - Collector Coin Token
 * @dev ERC20 token for investment and trading on CollectorCoin platform
 */
contract CCToken is ERC20, Ownable {

    event TokensMinted(address indexed to, uint256 amount);
    event TokensBurned(address indexed from, uint256 amount);

    constructor() ERC20("Collector Coin Token", "CCT") Ownable(msg.sender) {
        // Mint initial supply to owner (1,000,000 CCT)
        _mint(msg.sender, 1000000 * 10 ** decimals());
    }

    /**
     * @dev Mint new tokens (only owner)
     */
    function mint(address to, uint256 amount) external onlyOwner {
        require(to != address(0), "Cannot mint to zero address");
        _mint(to, amount);
        emit TokensMinted(to, amount);
    }

    /**
     * @dev Burn tokens (only owner)
     */
    function burn(address from, uint256 amount) external onlyOwner {
        require(from != address(0), "Cannot burn from zero address");
        _burn(from, amount);
        emit TokensBurned(from, amount);
    }

    /**
     * @dev Transfer tokens from user to contract (for investments)
     * @notice User must approve this contract first
     */
    function transferForInvestment(
        address investor,
        address projectWallet,
        uint256 amount
    ) external onlyOwner returns (bool) {
        require(investor != address(0), "Invalid investor");
        require(projectWallet != address(0), "Invalid project wallet");
        require(amount > 0, "Amount must be greater than 0");

        _transfer(investor, projectWallet, amount);
        return true;
    }
}
