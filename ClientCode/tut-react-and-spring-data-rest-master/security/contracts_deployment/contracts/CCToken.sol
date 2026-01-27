// SPDX-License-Identifier: GPL-3.0

// pragma solidity ^0.8.0;
pragma solidity ^0.8.20;

import "./Ownable.sol";
import "./ERC20.sol";
import "./SafeMath.sol";

/**
 * @title CC Swap Token
 * @dev Implements Collector Coin Swap Token facility
 */
contract CCToken is ERC20, Ownable {
    using SafeMath for uint256;

    event MintTokens(address indexed recipient, uint256 indexed amount);
    event BurnTokens(address indexed recipient, uint256 indexed amount);

    modifier nonZeroAddress()
    {
        require(msg.sender != address(0));
        _;
    }

    constructor()
        ERC20("Collector Coin Token", "CCT")
    {}

    function mint(address _recipient, uint256 _amount)
        public
        onlyOwner
        nonZeroAddress
        returns (bool)
    {
        _mint(_recipient, _amount);
        emit MintTokens(msg.sender, _amount);
        return true;
    }

    function burn(address _bank, uint256 _amount)
        public
        onlyOwner
        nonZeroAddress
        returns (bool)
    {
        _burn(_bank, _amount);
        emit BurnTokens(msg.sender, _amount);
        return true;
    }

    /**
     * @dev Returns deletes contract
    
    function kill()
        public
        onlyOwner
        nonZeroAddress
    {
        selfdestruct(payable(msg.sender));
    }
    */
}