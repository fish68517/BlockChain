package com.company.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import com.company.services.AuthService;
import com.company.services.WalletAuthService;
import com.company.request.SignupRequest;
import com.company.request.LoginRequest;
import com.company.request.WalletAuthRequest;
import com.company.request.WalletSignupRequest;
import com.company.models.User;
import org.springframework.http.HttpStatus;
import com.company.exception.ResourceAlreadyExistsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @Autowired
  private WalletAuthService walletAuthService;


  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
    System.out.println(">>> [AuthController] registerUser 调用: " + signUpRequest.getUsername());
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body("Invalid data");
    }
    try {
        // 修正点：AuthService 返回的是 User，必须用 ResponseEntity 包装一下
        User user = authService.registerUser(signUpRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Register failed: " + e.getMessage());
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    System.out.println(">>> [AuthController] authenticateUser 调用: " + loginRequest.getUsername());
    return authService.authenticateUser(loginRequest);
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    System.out.println(">>> [AuthController] logoutUser 调用");
    authService.logoutUser();
    return ResponseEntity.ok("You've been signed out!");
  }

  

  @GetMapping("/wallet/nonce/{walletAddress}")
  public ResponseEntity<?> getNonce(@PathVariable String walletAddress) {
    System.out.println(">>> [AuthController] getNonce 请求收到 | Address: " + walletAddress);
    try {
        String nonce = walletAuthService.generateNonce(walletAddress);
        Map<String, Object> response = new HashMap<>();
        response.put("message", nonce);
        response.put("walletExists", walletAuthService.walletExists(walletAddress));
        
        System.out.println(">>> [AuthController] getNonce 成功返回: " + nonce);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        System.err.println(">>> [AuthController] getNonce 异常: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting nonce");
    }
  }

  @GetMapping("/wallet/exists/{walletAddress}")
  public ResponseEntity<?> checkWalletExists(@PathVariable String walletAddress) {
    System.out.println(">>> [AuthController] checkWalletExists 请求 | Address: " + walletAddress);
    boolean exists = walletAuthService.walletExists(walletAddress);
    Map<String, Boolean> response = new HashMap<>();
    response.put("exists", exists);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/wallet/signin")
  public ResponseEntity<?> walletSignin(@Valid @RequestBody WalletAuthRequest request) {
    System.out.println(">>> [AuthController] walletSignin 请求 | Address: " + request.getWalletAddress());
    try {
        return walletAuthService.authenticateWithWallet(request);
    } catch (Exception e) {
        System.err.println(">>> [AuthController] walletSignin 异常: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wallet signin failed");
    }
  }

  /**
   * 这里的日志将帮你定位 SyntaxError 的原因
   */
  @PostMapping("/wallet/signup")
  public ResponseEntity<?> walletSignup(@Valid @RequestBody WalletSignupRequest request, BindingResult bindingResult) {
    System.out.println("=================================================");
    System.out.println(">>> [AuthController] walletSignup 接口被触发!");
    System.out.println(">>> 参数 Username: " + request.getUsername());
    System.out.println(">>> 参数 Email:    " + request.getEmail());
    System.out.println(">>> 参数 Address:  " + request.getWalletAddress());
    System.out.println(">>> 参数 Roles:    " + request.getRole());
    System.out.println("=================================================");

    // 1. 检查参数校验错误
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = bindingResult.getFieldErrors().stream()
          .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
      
      System.err.println(">>> [AuthController] 参数校验失败: " + errors);
      return ResponseEntity.badRequest().body(errors);
    }

    // 2. 执行注册逻辑，并捕获所有异常
    try {
        ResponseEntity<?> response = walletAuthService.registerWithWallet(request);
        System.out.println(">>> [AuthController] walletSignup 执行成功，返回状态: " + response.getStatusCode());
        return response;
    } catch (Exception e) {
        // 这就是防止 500 HTML 报错的关键！
        System.err.println(">>> [AuthController] walletSignup 发生严重异常 (CRITICAL ERROR):");
        e.printStackTrace(); // 打印完整堆栈到控制台
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Server Error: " + e.getMessage()); // 返回 JSON 格式的错误信息
    }
  }
}