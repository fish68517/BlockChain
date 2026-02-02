CollectorCoin Server Application.

This project contains the JBPM model, as well as models and controllers to interact with it. Stack: Java & Maven.

Currently configured to run on `http://localhost:8090`.

The project directory contains the following files:

1. business-application-model. This folder contains Java model definitions.
2. business-application-service. This is the entrypoint of the application. It contains the main logic of interacting with the JBPM model through JBPM API and client application through REST controllers. Refer to [this readme](business-application-service/readme.md) for more details.
3. business-central-kjar. This directory contains the .bpmn file with the JBPM model to be deployed with the application is run. The [module readme](business-central-kjar/readme.md) contains more details.





Administrator@WIN-MAB9DRR1MTC MINGW64 /d/CollectionCoin_22222/CollectorCoin/ServerCode/local-hardhat (admin_wallet)
$ npx hardhat run scripts/deploy-cc.js --network localhost
WARNING: You are currently using Node.js v22.20.0, which is not supported by Hardhat. This can lead to unexpected behavior. See https://hardhat.org/nodejs-versions

Downloading compiler 0.8.28
Compiled 10 Solidity files successfully (evm target: paris).
Deploying with: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
CCToken deployed to: 0x5FbDB2315678afecb367f032d93F642f64180aa3
CCProjectFactory deployed to: 0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512
Transferring to admin: 0x2546BcD3c84621e976D8185a91A922aE77ECEc30
Ownership transferred successfully!

---



Administrator@WIN-MAB9DRR1MTC MINGW64 /d/CollectionCoin_22222/CollectorCoin/ServerCode/local-hardhat (admin_wallet)
$ npx hardhat run scripts/transfer-factory-ownership.js --network localhost
WARNING: You are currently using Node.js v22.20.0, which is not supported by Hardhat. This can lead to unexpected behavior. See https://hardhat.org/nodejs-versions

Current deployer address: 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
Factory address: 0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512
Admin address (target owner): 0x2546BcD3c84621e976D8185a91A922aE77ECEc30

Current factory owner: 0x2546BcD3c84621e976D8185a91A922aE77ECEc30
Factory is already owned by admin address!
