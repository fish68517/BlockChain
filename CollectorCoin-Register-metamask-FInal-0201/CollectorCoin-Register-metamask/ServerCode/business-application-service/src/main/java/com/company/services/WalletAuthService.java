package com.company.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import com.company.security.jwt.JwtUtils;
import com.company.models.ERole;
import com.company.models.Role;
import com.company.models.User;
import com.company.repositories.RoleRepository;
import com.company.repositories.UserRepository;
import com.company.request.WalletAuthRequest;
import com.company.request.WalletSignupRequest;
import com.company.response.JwtResponse;
import com.company.exception.ResourceAlreadyExistsException;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WalletAuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  // Generate a nonce message for signing
  public String generateNonce(String walletAddress) {
    long timestamp = System.currentTimeMillis();
    return "Sign this message to authenticate with CollectorCoin.\n\nWallet: " + walletAddress + "\nTimestamp: " + timestamp + "\nNonce: " + UUID.randomUUID().toString();
  }

  // Check if wallet exists
  public boolean walletExists(String walletAddress) {
    return userRepository.existsByWalletAddress(walletAddress.toLowerCase());
  }

  // Get user by wallet
  public Optional<User> getUserByWallet(String walletAddress) {
    return userRepository.findByWalletAddress(walletAddress.toLowerCase());
  }

  // Verify signature and authenticate existing user
  public ResponseEntity<?> authenticateWithWallet(WalletAuthRequest request) {
    try {
      String walletAddress = request.getWalletAddress().toLowerCase();
      
      // Verify the signature
      if (!verifySignature(request.getMessage(), request.getSignature(), walletAddress)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid signature");
      }

      // Find user by wallet
      Optional<User> userOpt = userRepository.findByWalletAddress(walletAddress);
      if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Wallet not registered. Please sign up first.");
      }

      User user = userOpt.get();
      
      // Create authentication token
      UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
      UsernamePasswordAuthenticationToken authentication = 
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Generate JWT
      String jwt = jwtUtils.generateJwtToken(authentication);

      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      return ResponseEntity.ok(new JwtResponse(jwt,
          user.getId(),
          user.getUsername(),
          user.getEmail(),
          roles));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Authentication failed: " + e.getMessage());
    }
  }

  // Register new user with wallet
  public ResponseEntity<?> registerWithWallet(WalletSignupRequest request) {
    try {
      String walletAddress = request.getWalletAddress().toLowerCase();

      // Verify signature first
      if (!verifySignature(request.getMessage(), request.getSignature(), walletAddress)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Invalid signature");
      }

      // Check if wallet already exists
      if (userRepository.existsByWalletAddress(walletAddress)) {
        throw new ResourceAlreadyExistsException("Error: Wallet address is already registered!");
      }

      // Check if username exists
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new ResourceAlreadyExistsException("Error: Username is already taken!");
      }

      // Check if email exists
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new ResourceAlreadyExistsException("Error: Email is already in use!");
      }

      // Create new user
      User user = new User();
      user.setUsername(request.getUsername());
      user.setEmail(request.getEmail());
      user.setWalletAddress(walletAddress);
      user.setPassword(request.getUsername()); // Wallet users don't need password

      // Set roles
      Set<String> strRoles = request.getRole();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null || strRoles.isEmpty()) {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          Role foundRole = findRole(role);
          if (foundRole != null) {
            roles.add(foundRole);
          }
        });
      }

      user.setRoles(roles);
      User savedUser = userRepository.save(user);

      // Auto-login after registration
      UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
      UsernamePasswordAuthenticationToken authentication = 
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = jwtUtils.generateJwtToken(authentication);

      List<String> rolesList = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new JwtResponse(jwt,
              savedUser.getId(),
              savedUser.getUsername(),
              savedUser.getEmail(),
              rolesList));

    } catch (ResourceAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Registration failed: " + e.getMessage());
    }
  }

  // Verify Ethereum signature
  private boolean verifySignature(String message, String signature, String expectedAddress) {
    try {
      // Prefix the message as Ethereum does
      String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
      byte[] msgHash = org.web3j.crypto.Hash.sha3((prefix + message).getBytes());

      // Parse signature
      byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
      
      byte v = signatureBytes[64];
      if (v < 27) {
        v += 27;
      }

      Sign.SignatureData sd = new Sign.SignatureData(
          v,
          Arrays.copyOfRange(signatureBytes, 0, 32),
          Arrays.copyOfRange(signatureBytes, 32, 64)
      );

      // Recover public key
      BigInteger publicKey = Sign.signedPrefixedMessageToKey(message.getBytes(), sd);
      String recoveredAddress = "0x" + Keys.getAddress(publicKey);

      return recoveredAddress.equalsIgnoreCase(expectedAddress);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private Role findRole(String roleName) {
    switch (roleName.toLowerCase()) {
      case "admin":
        return roleRepository.findByName(ERole.ROLE_ADMIN).orElse(null);
      case "owner":
        return roleRepository.findByName(ERole.ROLE_OWNER).orElse(null);
      case "investor":
        return roleRepository.findByName(ERole.ROLE_INVESTOR).orElse(null);
      case "restorer":
        return roleRepository.findByName(ERole.ROLE_RESTORER).orElse(null);
      case "buyer":
        return roleRepository.findByName(ERole.ROLE_BUYER).orElse(null);
      default:
        return roleRepository.findByName(ERole.ROLE_USER).orElse(null);
    }
  }
}