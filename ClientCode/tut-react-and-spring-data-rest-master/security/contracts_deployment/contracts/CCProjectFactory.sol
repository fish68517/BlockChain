// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8.0;

import "./Ownable.sol";
import "./CCProject.sol";

/** 
 * @title Collector Coin Project Factory
 * @dev Deploys a new CC Project
 */
contract CCProjectFactory is Ownable {
    ICCToken token;
    CCProject[] public projectAddresses;

    event ProjectCreated(CCProject project);
    event ProjectDetailsUpdated(address indexed projectAddress);
    event ProjectApproved(address indexed projectAddress);
    event ProjectValueEstimated(address indexed projectAddress);
    event ProjectRestorerSet(address indexed projectAddress, address indexed restorerAddress, uint256 amount);
    event ProjectRestorationAssigned(address indexed projectAddress);
    event ProjectAuctionOpen(address indexed projectAddress);
    event ProjectBuyerSet(address indexed projectAddress, address indexed buyerAddress);
    event ProjectProfitRedistributed(address indexed projectAddress);
    
    constructor(address _ccTokenAddress)
    {
        token = ICCToken(_ccTokenAddress);
    }
    
    function createNewProject(
        string memory _vin,
        string memory _make,
        string memory _model,
        uint256 _ccpg,
        uint256 _fundingGoal,
        address _ownerAddress
    ) external returns (address) {
        CCProject newProject = new CCProject(
            _vin,
            _make,
            _model,
            _ccpg,
            _fundingGoal,
            _ownerAddress,
            address(token)
        );
        projectAddresses.push(newProject);
        emit ProjectCreated(newProject);

        return address(newProject);
    }
    
    function getProjects()
        external
        view
        returns (CCProject[] memory)
    {
        return projectAddresses;
    }
    
    function getProjectById(uint256 _id)
        external
        view
        returns (CCProject)
    {
        return projectAddresses[_id];
    }
    
    /**
     * Admin functions
    */
    
    function editProjectDetails(
        address _projectAddress,
        string memory _vin,
        string memory _make,
        string memory _model,
        uint256 _ccpg,
        uint256 _fundingGoal
    ) external returns (bool) {
        CCProject(_projectAddress).updateBasicInfo(
            _vin, 
            _make, 
            _model, 
            _ccpg, 
            _fundingGoal
        );
        emit ProjectDetailsUpdated(_projectAddress);
        return true;
    }

    function approveProject(
        address _projectAddress
    ) external onlyOwner returns (bool) {
        CCProject(_projectAddress).approveProject();
        emit ProjectApproved(_projectAddress);
        return true;
    }

    function saveEstimations(
        address _projectAddress,
        uint256 _estValue, 
        uint256 _estRepairCost
    ) external onlyOwner returns (bool) {
        CCProject(_projectAddress).saveEstimations(
            _estValue,
            _estRepairCost
        );
        emit ProjectValueEstimated(_projectAddress);
        return true;
    }

    function setRestorer(
        address _projectAddress,
        address _restorer, 
        uint256 _fundingGoal
    ) public onlyOwner returns (bool) {
        CCProject(_projectAddress).setRestorer(
            _restorer, 
            _fundingGoal
        );
        emit ProjectRestorerSet(_projectAddress, _restorer, _fundingGoal);
        return true;
    }

    function assignForRestoration(
        address _projectAddress
    ) external onlyOwner returns (bool) {
        CCProject(_projectAddress).assignForRestoration();
        emit ProjectRestorationAssigned(_projectAddress);
        return true;
    }

    function setAuctionOpen(
        address _projectAddress
    ) public onlyOwner returns (bool) {
        CCProject(_projectAddress).setAuctionOpen();
        emit ProjectAuctionOpen(_projectAddress);
        return true;
    }

    function setBuyer(
        address _projectAddress,
        address _buyer
    ) external onlyOwner returns (bool) {
        CCProject(_projectAddress).setBuyer(_buyer);
        emit ProjectBuyerSet(_projectAddress, _buyer);
        return true;
    }

    function redistribute(
        address _projectAddress
    ) external onlyOwner returns (bool) {
        CCProject(_projectAddress).redistribute(msg.sender, 10);
        emit ProjectProfitRedistributed(_projectAddress);
        return true;
    }
}