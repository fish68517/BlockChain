// require("@nomicfoundation/hardhat-toolbox");
//
// /** @type import('hardhat/config').HardhatUserConfig */
// module.exports = {
//   solidity: "0.8.17",
// };

// https://eth-goerli.g.alchemy.com/v2/URAAJxw6J33Y9vmlOsVi4Yqz1n0AfI8t
require("@nomiclabs/hardhat-waffle");

module.exports = {
  solidity: "0.8.0",
  networks: {
    goerli: {
      url: "https://eth-goerli.g.alchemy.com/v2/URAAJxw6J33Y9vmlOsVi4Yqz1n0AfI8t",
      accounts: ['b31fa01255e82131ab62303610a7812f46b7756ab612f9e2f66be5b273d9d7f4']
    }
  }
}