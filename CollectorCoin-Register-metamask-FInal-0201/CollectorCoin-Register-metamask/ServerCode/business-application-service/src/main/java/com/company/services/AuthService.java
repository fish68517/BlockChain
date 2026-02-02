package com.company.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.security.jwt.JwtUtils;
import com.company.models.ERole;
import com.company.models.Role;
import com.company.models.User;
import com.company.repositories.RoleRepository;
import com.company.repositories.UserRepository;
import com.company.request.SignupRequest;
import com.company.request.LoginRequest;
import com.company.exception.ResourceAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import com.company.response.JwtResponse;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  public User registerUser(SignupRequest signUpRequest) {
    // Check if username exists
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      throw new ResourceAlreadyExistsException("Error: Username is already taken!");
    }

    // Check if email exists
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new ResourceAlreadyExistsException("Error: Email is already in use!");
    }

    String password = signUpRequest.getPassword();

    if (password.length() < 6 || password.length() > 40) {
      throw new ResourceAlreadyExistsException("Error: Password must be between 6 and 40 characters");
    }

    // Create new user
    User user = new User();
    user.setUsername(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(passwordEncoder.encode(password));

    // Set roles
    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null || strRoles.isEmpty()) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "owner":
            Role ownerRole = roleRepository.findByName(ERole.ROLE_OWNER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(ownerRole);
            break;
          case "investor":
            Role investorRole = roleRepository.findByName(ERole.ROLE_INVESTOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(investorRole);
            break;
          case "contributor":
            Role contributorRole = roleRepository.findByName(ERole.ROLE_CONTRIBUTOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(contributorRole);
            break;
          case "restorer":
            Role restorerRole = roleRepository.findByName(ERole.ROLE_RESTORER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(restorerRole);
            break;
          case "buyer":
            Role buyerRole = roleRepository.findByName(ERole.ROLE_BUYER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(buyerRole);
            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    return userRepository.save(user);
  }

  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    try {
      System.out.println(
          "authenticateUser " + loginRequest.getUsername().toString() + " " + loginRequest.getPassword().toString());
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = jwtUtils.generateJwtToken(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
          .collect(Collectors.toList());
      System.out.println("authenticateUser " + jwt);
      System.out.println("authenticateUser " + userDetails.getId());
      System.out.println("authenticateUser " + userDetails.getUsername());
      System.out.println("authenticateUser " + userDetails.getEmail());
      System.out.println("authenticateUser " + roles);
      return ResponseEntity.ok(new JwtResponse(jwt,
          userDetails.getId(),
          userDetails.getUsername(),
          userDetails.getEmail(),
          roles));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body("Invalid username or password");
    }
  }

  // Clear the authentication on logout
  public void logoutUser() {
    SecurityContextHolder.clearContext();
  }
}
