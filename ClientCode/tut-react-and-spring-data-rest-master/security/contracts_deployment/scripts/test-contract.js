const { ethers, waffle} = require("hardhat");

const provider = waffle.provider;
const getBalance = async () => {
    const balanceInWei = await provider.getBalance("0x5FbDB2315678afecb367f032d93F642f64180aa3");
    return balanceInWei;
}
console.log(getBalance());