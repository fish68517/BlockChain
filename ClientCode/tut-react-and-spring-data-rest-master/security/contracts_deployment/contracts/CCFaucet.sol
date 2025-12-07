// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8.0;

import "./Ownable.sol";
import "./CCToken.sol";
import "./CCProject.sol";

/** 
 * @title Collector Coin Project Factory
 * @dev Deploys a new CC Project
 */
contract CCProjectFactory is Ownable {
    CCToken token;
    CCProject[] public projectAddresses;
    event ProjectCreated(CCProject project);
    
    constructor(address _ccTokenAddress)
    {
        token = CCToken(_ccTokenAddress);
    }
    
    function createNewProject(
        string memory _vin,
        string memory _make,
        string memory _model,
        bool _vinMatched,
        uint256 _msrp,
        uint256 _fundingGoal
    )
        external
    {
        CCProject newProject = new CCProject(
            _vin,
            _make,
            _model,
            _vinMatched,
            _msrp,
            _fundingGoal,
            address(token)
        );
        projectAddresses.push(newProject);
        emit ProjectCreated(newProject);
    }
    
    function getProjects()
        external
        view
        returns (CCProject[] memory)
    {
        return projectAddresses;
    }
    
    function getProject(uint256 _id)
        external
        view
        returns (CCProject)
    {
        return projectAddresses[_id];
    }
    
    /**
     * Admin functions
     */
    function deactivate(address _projectAddress)
        external
        onlyOwner
        returns (bool)
    {
        CCProject(_projectAddress).deactivate();
        return true;
    }
    
    function withdrawFunding(address _projectAddress)
        external
        onlyOwner
        returns (bool)
    {
        CCProject(_projectAddress).withdrawFunding();
        return true;
    }
    
    function transferFunding(address _recipient, uint256 _amount)
        external
        onlyOwner
        returns (bool)
    {
        token.transfer(_recipient, _amount);
        return true;
    }
    
    function approveDeposits(address _projectAddress, uint256 _amount)
        external
        onlyOwner
        returns (bool)
    {
        token.approve(_projectAddress, _amount);
        return true;
    }
    
    function depositProceeds(address _projectAddress, uint256 _amount)
        external
        onlyOwner
        returns (bool)
    {
        CCProject(_projectAddress).depositProceeds(_amount);
        return true;
    }
    
    function udpateCCToken(address _newAddress)
        public
        onlyOwner
        returns (bool)
    {
        token = CCToken(_newAddress);
        return true;
    }
}