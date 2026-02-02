package com.collectorcoin.controller;

import com.collectorcoin.dto.RegisterRequest;
import com.collectorcoin.model.User;
import com.collectorcoin.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

import com.collectorcoin.security.jwt.JwtUtils;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request for user: {}", request.getUsername());
        try {
            User user = userService.register(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User registered successfully",
                "userId", user.getId(),
                "username", user.getUsername(),
                "walletAddress", user.getWalletAddress(),
                "walletPrivateKey", user.getWalletPrivateKey(),
                "ethBalance", user.getEthBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Registration failed: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody com.collectorcoin.dto.LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        logger.info("Login attempt for user: {}", username);

        return userService.login(username, password)
            .map(user -> {
                String token = jwtUtils.generateJwtToken(user.getUsername());
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("token", token);
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                response.put("walletAddress", user.getWalletAddress());
                response.put("ethBalance", user.getEthBalance());
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Invalid username or password"
            )));
    }

    @PostMapping("/{userId}/bind-wallet")
    public ResponseEntity<?> bindWallet(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String walletAddress = request.get("walletAddress");
        try {
            User user = userService.bindWallet(userId, walletAddress);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Wallet bound and ETH allocated",
                "ethBalance", user.getEthBalance()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/wallet/{walletAddress}")
    public ResponseEntity<?> getByWallet(@PathVariable String walletAddress) {
        return userService.findByWalletAddress(walletAddress)
            .map(user -> ResponseEntity.ok(Map.of(
                "status", "success",
                "userId", user.getId(),
                "username", user.getUsername(),
                "ethBalance", user.getEthBalance()
            )))
            .orElse(ResponseEntity.notFound().build());
    }
}
