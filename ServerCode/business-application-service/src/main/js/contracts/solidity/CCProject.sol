// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8.0;

import "./SafeMath.sol";
import "./Ownable.sol";

interface CCToken {
    function balanceOf(address account) external view returns (uint256);
    function approve(address spender, uint256 amount) external returns (bool);
    function transfer(address recipient, uint256 amount) external returns (bool);
    function transferFrom(
        address sender,
        address recipient,
        uint256 amount
    ) external returns (bool);
}

/** 
 * @title CC Project
 * @dev Implements A new Collector Coin Project
 */
contract CCProject is Ownable{
    using SafeMath for uint;
    
    CCToken token;
    bool public fundingLive;
    bool public isApproved;
    bool public openAuction;
    bool public buyerSelected;

    // Project Details
    string public vinNumber;
    string public make;
    string public model;
    uint256 public ccpg;
    uint256 public fundingGoal;
    uint256 public fundsDeposited;
    address public ownerAddress;
    address public factoryAddress;
    
    uint256 public selectedSellingPrice;
    address public selectedBuyer;
    uint256 public selectedRestorationPrice;
    address public selectedRestorer;

    uint256 public estimatedValue;
    uint256 public estimatedRepairCost;

    mapping(address => uint256) public investments;
    mapping(address => uint256) public auctionBids;
    mapping(uint256 => address) public investors;
    mapping(uint256 => address) public bidders;

    uint256 public investor_count;
    uint256 public bidder_count;

    event ProjectListingCreated(address indexed _address, address indexed ownerAddress);
    event ProjectListingEdited(address indexed _address, address indexed ownerAddress);
    event ProjectListingApproved(address indexed _address);
    event EstimationsSaved(uint256 indexed _estValue, uint256 indexed _estRCost);
    event RestorerSelected(address indexed account, uint256 indexed amount);
    event InvestorAdded(address indexed account, uint256 indexed amount);
    event FundingAllocated(address indexed account, uint256 indexed amount);
    event AuctionIsOpen(address indexed listing);
    event BuyBidReceived(address indexed account, uint256 indexed amount);
    event BuyerSelected(address indexed account, uint256 indexed amount);
    event RedistributedTo(address indexed account, uint256 indexed amount);
    event BuyBidRefunded(address indexed account, uint256 indexed amount);
    event PurschaseComplete(address indexed listing);

    constructor(
        string memory _vin,
        string memory _make,
        string memory _model,
        uint256 _ccpg,
        uint256 _fundingGoal,
        address _ownerAddress,
        address _ccTokenAddress
    ) {
        vinNumber = _vin;
        make = _make;
        model = _model;
        ccpg = _ccpg;
        fundingGoal = _fundingGoal;
        ownerAddress = _ownerAddress;
        token = CCToken(_ccTokenAddress);

        fundingLive = false;
        factoryAddress = msg.sender;
        isApproved = false;
        openAuction = false;
        buyerSelected = false;
        fundsDeposited = 0;

        investor_count = 0;
        bidder_count = 0;

        emit ProjectListingCreated(address(this), ownerAddress);
    }

    function updateBasicInfo(
        string memory _vin,
        string memory _make,
        string memory _model,
        uint256 _ccpg,
        uint256 _fundingGoal
    ) public onlyOwner {
        vinNumber = _vin;
        make = _make;
        model = _model;
        ccpg = _ccpg;
        fundingGoal = _fundingGoal;
        
        fundingLive = false;
        isApproved = false;
        openAuction = false;
        buyerSelected = false;
        fundsDeposited = 0;
        investor_count = 0;
        bidder_count = 0;

        emit ProjectListingEdited(address(this), ownerAddress);
    }

    function approveProject() public onlyOwner {
        isApproved = true;
        emit ProjectListingApproved(address(this));
    }

    function saveEstimations(
        uint256 _estValue, 
        uint256 _estRepairCost
    ) public onlyOwner  {
        estimatedRepairCost = _estRepairCost;
        estimatedValue = _estValue;
        emit EstimationsSaved(estimatedValue, estimatedRepairCost);
    }

    function setFundingGoal(
        uint256 _fundingGoal
    ) internal {
        fundingGoal = _fundingGoal;
    }

    function setRestorer(
        address _restorer, 
        uint256 _fundingGoal
    ) public onlyOwner {
        require(_restorer != address(0), "Invalid restorer address");
        selectedRestorer = _restorer;
        setFundingGoal(_fundingGoal);
        selectedRestorationPrice = _fundingGoal;
        fundingLive = true;
        emit RestorerSelected(selectedRestorer, fundingGoal);
    }

    function acceptFunds(
        uint256 amount
    ) external returns (bool) {
        require(msg.sender != address(0), "Invalid sender address");
        require(fundsDeposited < fundingGoal, "Funding goal had been reached");
        require(amount <= fundingGoal.sub(fundsDeposited), "Fund exceeds required goal");
        require(amount > 0, "Choose a non-zero amount to invest");
        require(fundingLive, "Funding is not live");

        require(token.transferFrom(msg.sender, address(this), amount), "Unable to transfer funds to the project");

        investors[investor_count] = msg.sender;
        investor_count ++;
        investments[msg.sender] = investments[msg.sender].add(amount);
        fundsDeposited = fundsDeposited.add(amount);

        if(fundsDeposited == fundingGoal) {
            fundingLive = false;
        }

        emit InvestorAdded(msg.sender, amount);
        return true;
    }

    function assignForRestoration() public onlyOwner {
        require(!fundingLive, "funding is still in progress");
        require(fundingGoal == fundsDeposited, "funding goal has not been reached");
        require(token.transfer(selectedRestorer, fundingGoal), "Unable to transfer funds to restorer");
        emit FundingAllocated(selectedRestorer, fundingGoal);
    }

    function setAuctionOpen() public onlyOwner {
        openAuction = true;
        emit AuctionIsOpen(address(this));
    }

    function addNewAuctionBid(
        uint256 amount
    ) external returns (bool) {
        require(openAuction, "Auction is not open");
        require(msg.sender != address(0), "Invalid sender address");

        require(token.transferFrom(msg.sender, address(this), amount), "Transfer of tokens while bidding failed");

        auctionBids[msg.sender] = amount;
        bidders[bidder_count] = msg.sender;
        bidder_count ++;

        emit BuyBidReceived(msg.sender, amount);
        return true;
    }

    function setBuyer(address _buyer) public onlyOwner {
        require(_buyer != address(0), "Invalid buyer address");
        selectedBuyer = _buyer;
        selectedSellingPrice = auctionBids[selectedBuyer];
        buyerSelected = true;
        openAuction = false;
        emit BuyerSelected(selectedBuyer, selectedSellingPrice);

        refundBids();
    }


    function refundBids() internal {
        for(uint256 i = 0; i < bidder_count; i ++) {
            if(bidders[i] != selectedBuyer) {
                require(token.transfer(bidders[i], auctionBids[bidders[i]]), "Error when transferring refunds to bidders");
                emit BuyBidRefunded(bidders[i], auctionBids[bidders[i]]);
            }
        }
    }

    function redistribute(
        address adminAddress,
        uint256 commission
    ) public onlyOwner {
        require(buyerSelected, "Winner of auction is not yet decided");
        uint256 commAmount = selectedSellingPrice.mul(commission).div(10 ** 2);
        require(token.transfer(adminAddress, commAmount), "Redistribution to admin failed");
        emit RedistributedTo(adminAddress, commAmount);

        uint256 total = ccpg.add(fundingGoal);
        uint256 rem = selectedSellingPrice.sub(commAmount);

        uint256 ownerShare = ccpg.mul(10 ** 2).div(total);
        uint256 ownerAmount = rem.mul(ownerShare).div(10 ** 2);
        require(token.transfer(ownerAddress, ownerAmount), "Redistribution to owner failed");
        emit RedistributedTo(ownerAddress, ownerAmount);

        for(uint256 i = 0; i < investor_count; i ++) {
            uint256 invShare = investments[investors[i]].mul(10 ** 2).div(total);
            uint256 invAmount = rem.mul(invShare).div(10 ** 2);
            require(token.transfer(investors[i], invAmount), "Redistribution to funder failed");
            emit RedistributedTo(investors[i], invAmount);
        }

        emit PurschaseComplete(address(this));
    }
}