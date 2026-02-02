// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";

/**
 * @title CCMarketPlace
 * @dev Dynamic NFT marketplace for CollectorCoin platform
 */
contract CCMarketPlace is ERC1155, Ownable, ReentrancyGuard {

    // ============ State Variables ============
    uint256 private _tokenIdCounter;

    struct DNFTData {
        address projectAddress;
        uint256 valueEstimation;
        uint256 repairEstimation;
        string metadataURI;
        uint8 status;
        address restorer;
        uint256 auctionPrice;
        bool transferable;
    }

    mapping(uint256 => DNFTData) private _dnftData;
    mapping(uint256 => address[]) private _investors;
    mapping(uint256 => mapping(address => uint256)) private _investorAmounts;

    // ============ Events ============
    event DNFTCreated(uint256 indexed tokenId, address indexed projectAddress, address admin);
    event DNFTLaunched(uint256 indexed tokenId);
    event InvestorAdded(uint256 indexed tokenId, address investor, uint256 amount);
    event RestorerUpdated(uint256 indexed tokenId, address restorer);
    event RestorationCompleted(uint256 indexed tokenId);
    event ItemPosted(uint256 indexed tokenId, uint256 price);
    event NFTTransferred(uint256 indexed tokenId, address winner);
    event MetadataUpdated(uint256 indexed tokenId, string metadataURI);

    // ============ Constructor ============
    constructor() ERC1155("") Ownable(msg.sender) {}

    // ============ Core Functions ============

    /**
     * @dev Mint a new dNFT for a project
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
        data.status = 0;
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
        _dnftData[tokenId].status = 1;
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
        require(data.status == 1 || data.status == 2, "Not in funding");

        if (_investorAmounts[tokenId][investor] == 0) {
            _investors[tokenId].push(investor);
        }
        _investorAmounts[tokenId][investor] += amount;
        data.status = 2;

        emit InvestorAdded(tokenId, investor, amount);
    }

    /**
     * @dev Update restorer address
     */
    function updateRestorer(uint256 tokenId, address restorer) external onlyOwner {
        require(_dnftData[tokenId].status == 2, "Not funded");
        _dnftData[tokenId].restorer = restorer;
        _dnftData[tokenId].status = 3;
        emit RestorerUpdated(tokenId, restorer);
    }

    /**
     * @dev Mark restoration as complete
     */
    function markRestorationComplete(uint256 tokenId) external onlyOwner {
        require(_dnftData[tokenId].status == 3, "Not restoring");
        _dnftData[tokenId].status = 4;
        emit RestorationCompleted(tokenId);
    }

    /**
     * @dev Set item for sale
     */
    function setItemForSale(uint256 tokenId, uint256 price) external onlyOwner {
        require(_dnftData[tokenId].status == 4, "Not complete");
        _dnftData[tokenId].auctionPrice = price;
        _dnftData[tokenId].transferable = true;
        _dnftData[tokenId].status = 5;
        emit ItemPosted(tokenId, price);
    }

    /**
     * @dev Transfer NFT to winner and redistribute funds
     */
    function transferToWinner(uint256 tokenId, address winner) external onlyOwner nonReentrant {
        require(_dnftData[tokenId].status == 5, "Not for sale");
        require(_dnftData[tokenId].transferable, "Not transferable");

        address currentOwner = owner();
        _safeTransferFrom(currentOwner, winner, tokenId, 1, "");
        _dnftData[tokenId].status = 6;
        _dnftData[tokenId].transferable = false;

        emit NFTTransferred(tokenId, winner);
    }

    /**
     * @dev Redistribute funds to investors based on their investment ratio
     * @param tokenId The token ID
     * @param totalAmount Total amount to redistribute
     */
    function redistributeFunds(uint256 tokenId, uint256 totalAmount) external onlyOwner nonReentrant {
        address[] memory investors = _investors[tokenId];
        require(investors.length > 0, "No investors");

        uint256 totalInvested = 0;
        for (uint256 i = 0; i < investors.length; i++) {
            totalInvested += _investorAmounts[tokenId][investors[i]];
        }

        for (uint256 i = 0; i < investors.length; i++) {
            uint256 investorAmount = _investorAmounts[tokenId][investors[i]];
            uint256 share = (totalAmount * investorAmount) / totalInvested;
            
            // Record the distribution (actual transfer would be done off-chain or via separate token)
            emit FundsRedistributed(tokenId, investors[i], share);
        }
    }

    event FundsRedistributed(uint256 indexed tokenId, address investor, uint256 amount);

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
        return _investors[tokenId];
    }

    function getInvestorAmount(uint256 tokenId, address investor) external view returns (uint256) {
        return _investorAmounts[tokenId][investor];
    }

    function uri(uint256 tokenId) public view override returns (string memory) {
        return _dnftData[tokenId].metadataURI;
    }
}
