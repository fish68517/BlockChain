const { ethers } = require("ethers");

async function main() {
  const privateKey = "0xea6c44ac03bff858b476bba40716402b03e41b8e97e276d1baec7c37d42484a0";
  
  // Create wallet from private key
  const wallet = new ethers.Wallet(privateKey);
  
  console.log("Private Key:", privateKey);
  console.log("Public Address:", wallet.address);
  console.log("\nThis is the admin wallet address that should own the factory contract.");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

