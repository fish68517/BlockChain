package com.company.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import com.company.services.AuthService;
import com.company.request.SignupRequest;
import com.company.request.LoginRequest;
import com.company.models.User;
import org.springframework.http.HttpStatus;
import com.company.exception.ResourceAlreadyExistsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = bindingResult.getFieldErrors().stream()
          .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
      return ResponseEntity.badRequest().body(errors);
    }
    try {
      User user = authService.registerUser(signUpRequest);
      return new ResponseEntity<>(user, HttpStatus.CREATED);
    } catch (ResourceAlreadyExistsException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (RuntimeException e) {
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      ResponseEntity<?> response = authService.authenticateUser(loginRequest);
      if (response.getStatusCode() == HttpStatus.OK) {
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(response.getBody(), HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred during authentication", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    authService.logoutUser();
    return ResponseEntity.ok("You've been signed out!");
  }
}
