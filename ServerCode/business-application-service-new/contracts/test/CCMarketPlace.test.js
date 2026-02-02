const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("CCMarketPlace", function () {
  let marketplace;
  let owner;
  let investor1;
  let investor2;
  let restorer;
  let winner;

  beforeEach(async function () {
    [owner, investor1, investor2, restorer, winner] = await ethers.getSigners();
    const CCMarketPlace = await ethers.getContractFactory("CCMarketPlace");
    marketplace = await CCMarketPlace.deploy();
    await marketplace.waitForDeployment();
  });

  describe("mintDNFT", function () {
    it("should mint dNFT successfully", async function () {
      const tx = await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );

      await expect(tx)
        .to.emit(marketplace, "DNFTCreated")
        .withArgs(0, investor1.address, owner.address);
    });

    it("should reject non-owner mint", async function () {
      await expect(
        marketplace.connect(investor1).mintDNFT(
          investor1.address,
          ethers.parseEther("50000"),
          ethers.parseEther("10000"),
          "ipfs://QmTest"
        )
      ).to.be.reverted;
    });
  });

  describe("launchDNFT", function () {
    it("should launch dNFT", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );

      const tx = await marketplace.launchDNFT(0);
      await expect(tx).to.emit(marketplace, "DNFTLaunched").withArgs(0);
    });
  });

  describe("addInvestor", function () {
    it("should add investor", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);

      const tx = await marketplace.addInvestor(
        0,
        investor1.address,
        ethers.parseEther("1000")
      );

      await expect(tx)
        .to.emit(marketplace, "InvestorAdded")
        .withArgs(0, investor1.address, ethers.parseEther("1000"));
    });
  });

  describe("Full lifecycle", function () {
    it("should complete full NFT lifecycle", async function () {
      // 1. Mint
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );

      // 2. Launch
      await marketplace.launchDNFT(0);

      // 3. Add investor
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));

      // 4. Update restorer
      await marketplace.updateRestorer(0, restorer.address);

      // 5. Mark complete
      await marketplace.markRestorationComplete(0);

      // 6. Set for sale
      await marketplace.setItemForSale(0, ethers.parseEther("100000"));

      // 7. Transfer to winner
      await marketplace.transferToWinner(0, winner.address);

      // Verify final state
      const data = await marketplace.getDNFTData(0);
      expect(data.status).to.equal(6);
    });
  });
});
