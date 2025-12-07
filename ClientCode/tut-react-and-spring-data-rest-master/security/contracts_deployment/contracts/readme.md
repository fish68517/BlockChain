# CC Smart Contracts
====
## Info

This folder contains smart contracts that are relevant to the CollectorCoin project. The main ones are CCProject, CCProjectFactory and CCToken.
CCProject contract represents a car listing, and CCToken - CollectorCoin as a cryptocurrency.
CCProjectFactory mains acts as an interface for the admins to interact with various CCProjects.

## CC Steps:

1. Admin → Approves the CCProject, Creates a CCProject Contract instance
2. Admin →  Adds an value estimation (set the status of the project to alive)
3. Restorer → Assigned Restorer got CCToken transferred from the CCProject contract
4. Funder/Invester → Fund the CCProject
5. Buyer → Transfer his token to CCProject instance. Failing to pay will transfer tokens to original funders (what about restorer?)
6. CCProject now has tokens from buyer. Redistribute tokens to funders (by how much? need an example) Admin marks the CCProject Contract to be not alive.

## Useful Resources:

1. Create a custom token by ERC20 policy: [https://www.quicknode.com/guides/smart-contract-development/how-to-create-and-deploy-an-erc20-token](https://www.quicknode.com/guides/smart-contract-development/how-to-create-and-deploy-an-erc20-token)
2. Tutorials on solidity: [https://www.youtube.com/watch?v=EhPeHeoKF88](https://www.youtube.com/watch?v=EhPeHeoKF88)
3. Tutorials on building Web3 with React, Metamask: [https://www.youtube.com/watch?v=Wn_Kb3MR_cU](https://www.youtube.com/watch?v=Wn_Kb3MR_cU)
4. ethers.js documentation [https://docs.ethers.org/v5/](https://docs.ethers.org/v5/)
5. Etherium Remix [https://remix-project.org/](https://remix-project.org/)

# Notes 

1. The addresses provided in ('ClientCode/tut-react-and-spring-data-rest-master/security/src/main/js/utils/web3Utils.js') are the address of the smart contract deployed in the Sepolia Testnet by us with onlyOwner access to most of the functions. We recommend you to redeploy the contracts the testnet you find convenient to work with
2. Additional information regarding the next steps can be found in the Final Report
