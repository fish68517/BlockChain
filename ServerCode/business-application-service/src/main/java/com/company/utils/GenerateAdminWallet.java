package com.company.utils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Utility class to generate a new Ethereum wallet key pair
 * Run this once to generate an admin wallet, then use the private key in your environment variables
 * 
 * Usage: Run the main method and copy the output
 */
public class GenerateAdminWallet {
    
    public static void main(String[] args) {
        try {
            // Generate a new key pair
            org.web3j.crypto.ECKeyPair keyPair = Keys.createEcKeyPair();
            
            // Create credentials from the key pair
            Credentials credentials = Credentials.create(keyPair);
            
            // Get the private key (with 0x prefix)
            String privateKey = keyPair.getPrivateKey().toString(16);
            if (!privateKey.startsWith("0x")) {
                privateKey = "0x" + privateKey;
            }
            
            // Get the public address
            String address = credentials.getAddress();
            
            System.out.println("========================================");
            System.out.println("NEW ADMIN WALLET GENERATED");
            System.out.println("========================================");
            System.out.println("Address (Public Key):");
            System.out.println(address);
            System.out.println("\nPrivate Key:");
            System.out.println(privateKey);
            System.out.println("\n========================================");
            System.out.println("IMPORTANT:");
            System.out.println("1. Copy the private key above");
            System.out.println("2. Set it as environment variable: BLOCKCHAIN_ADMIN_PRIVATE_KEY");
            System.out.println("3. NEVER share or commit this private key to git");
            System.out.println("4. Fund this address with test ETH (Sepolia) for gas fees");
            System.out.println("5. Delete this file after generating your wallet");
            System.out.println("========================================");
            
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println("Error generating wallet: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



