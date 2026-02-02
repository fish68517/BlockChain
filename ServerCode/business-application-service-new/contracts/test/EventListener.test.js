const { expect } = require("chai");
const { ethers } = require("hardhat");

/**
 * Event Listener Tests
 * 验证所有智能合约事件可被正确捕获
 */
describe("Event Listener Tests", function () {
  let marketplace;
  let owner;
  let investor1;
  let restorer;
  let winner;

  beforeEach(async function () {
    [owner, investor1, restorer, winner] = await ethers.getSigners();
    const CCMarketPlace = await ethers.getContractFactory("CCMarketPlace");
    marketplace = await CCMarketPlace.deploy();
    await marketplace.waitForDeployment();
  });

  describe("DNFTCreated Event", function () {
    it("should emit DNFTCreated with correct parameters", async function () {
      const tx = await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTestMetadata"
      );

      const receipt = await tx.wait();
      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "DNFTCreated"
      );

      expect(event).to.not.be.undefined;
      expect(event.args[0]).to.equal(0n); // tokenId
      expect(event.args[1]).to.equal(investor1.address); // projectAddress
      expect(event.args[2]).to.equal(owner.address); // admin
    });

    it("should capture DNFTCreated via event filter", async function () {
      const filter = marketplace.filters.DNFTCreated();

      const tx = await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await tx.wait();

      const events = await marketplace.queryFilter(filter);
      expect(events.length).to.equal(1);
      expect(events[0].args.tokenId).to.equal(0n);
    });
  });

  describe("DNFTLaunched Event", function () {
    it("should emit DNFTLaunched with correct tokenId", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );

      const tx = await marketplace.launchDNFT(0);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "DNFTLaunched"
      );

      expect(event).to.not.be.undefined;
      expect(event.args[0]).to.equal(0n);
    });
  });

  describe("InvestorAdded Event", function () {
    it("should emit InvestorAdded with investor details", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);

      const investAmount = ethers.parseEther("5000");
      const tx = await marketplace.addInvestor(0, investor1.address, investAmount);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "InvestorAdded"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
      expect(event.args.investor).to.equal(investor1.address);
      expect(event.args.amount).to.equal(investAmount);
    });
  });

  describe("RestorerUpdated Event", function () {
    it("should emit RestorerUpdated with restorer address", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));

      const tx = await marketplace.updateRestorer(0, restorer.address);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "RestorerUpdated"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
      expect(event.args.restorer).to.equal(restorer.address);
    });
  });

  describe("RestorationCompleted Event", function () {
    it("should emit RestorationCompleted", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));
      await marketplace.updateRestorer(0, restorer.address);

      const tx = await marketplace.markRestorationComplete(0);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "RestorationCompleted"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
    });
  });

  describe("ItemPosted Event", function () {
    it("should emit ItemPosted with price", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));
      await marketplace.updateRestorer(0, restorer.address);
      await marketplace.markRestorationComplete(0);

      const salePrice = ethers.parseEther("100000");
      const tx = await marketplace.setItemForSale(0, salePrice);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "ItemPosted"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
      expect(event.args.price).to.equal(salePrice);
    });
  });

  describe("NFTTransferred Event", function () {
    it("should emit NFTTransferred with winner address", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));
      await marketplace.updateRestorer(0, restorer.address);
      await marketplace.markRestorationComplete(0);
      await marketplace.setItemForSale(0, ethers.parseEther("100000"));

      const tx = await marketplace.transferToWinner(0, winner.address);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "NFTTransferred"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
      expect(event.args.winner).to.equal(winner.address);
    });
  });

  describe("MetadataUpdated Event", function () {
    it("should emit MetadataUpdated with new URI", async function () {
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmOldMetadata"
      );

      const newURI = "ipfs://QmNewMetadata";
      const tx = await marketplace.updateMetadataURI(0, newURI);
      const receipt = await tx.wait();

      const event = receipt.logs.find(
        log => log.fragment && log.fragment.name === "MetadataUpdated"
      );

      expect(event).to.not.be.undefined;
      expect(event.args.tokenId).to.equal(0n);
      expect(event.args.metadataURI).to.equal(newURI);
    });
  });

  describe("Event Subscription Simulation", function () {
    it("should capture all events in full lifecycle", async function () {
      const allEvents = [];

      // Setup event listeners
      marketplace.on("DNFTCreated", (tokenId, projectAddress, admin) => {
        allEvents.push({ name: "DNFTCreated", tokenId, projectAddress, admin });
      });
      marketplace.on("DNFTLaunched", (tokenId) => {
        allEvents.push({ name: "DNFTLaunched", tokenId });
      });
      marketplace.on("InvestorAdded", (tokenId, investor, amount) => {
        allEvents.push({ name: "InvestorAdded", tokenId, investor, amount });
      });
      marketplace.on("RestorerUpdated", (tokenId, restorer) => {
        allEvents.push({ name: "RestorerUpdated", tokenId, restorer });
      });
      marketplace.on("RestorationCompleted", (tokenId) => {
        allEvents.push({ name: "RestorationCompleted", tokenId });
      });
      marketplace.on("ItemPosted", (tokenId, price) => {
        allEvents.push({ name: "ItemPosted", tokenId, price });
      });
      marketplace.on("NFTTransferred", (tokenId, winner) => {
        allEvents.push({ name: "NFTTransferred", tokenId, winner });
      });

      // Execute full lifecycle
      await marketplace.mintDNFT(
        investor1.address,
        ethers.parseEther("50000"),
        ethers.parseEther("10000"),
        "ipfs://QmTest"
      );
      await marketplace.launchDNFT(0);
      await marketplace.addInvestor(0, investor1.address, ethers.parseEther("1000"));
      await marketplace.updateRestorer(0, restorer.address);
      await marketplace.markRestorationComplete(0);
      await marketplace.setItemForSale(0, ethers.parseEther("100000"));
      await marketplace.transferToWinner(0, winner.address);

      // Wait for events to be processed
      await new Promise(resolve => setTimeout(resolve, 1000));

      // Remove listeners
      marketplace.removeAllListeners();

      // Verify all events captured
      expect(allEvents.length).to.be.gte(7);
      expect(allEvents.map(e => e.name)).to.include("DNFTCreated");
      expect(allEvents.map(e => e.name)).to.include("DNFTLaunched");
      expect(allEvents.map(e => e.name)).to.include("InvestorAdded");
      expect(allEvents.map(e => e.name)).to.include("RestorerUpdated");
      expect(allEvents.map(e => e.name)).to.include("RestorationCompleted");
      expect(allEvents.map(e => e.name)).to.include("ItemPosted");
      expect(allEvents.map(e => e.name)).to.include("NFTTransferred");
    });
  });
});
