package com.dlmgroup.collectorcoin.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dlmgroup.collectorcoin.models.ERole;
import com.dlmgroup.collectorcoin.models.Role;
import com.dlmgroup.collectorcoin.models.User;
import com.dlmgroup.collectorcoin.payload.request.LoginRequest;
import com.dlmgroup.collectorcoin.payload.request.SignupRequest;
import com.dlmgroup.collectorcoin.payload.response.JwtResponse;
import com.dlmgroup.collectorcoin.payload.response.MessageResponse;
import com.dlmgroup.collectorcoin.repositories.RoleRepository;
import com.dlmgroup.collectorcoin.repositories.UserRepository;
import com.dlmgroup.collectorcoin.security.jwt.JwtUtils;
import com.dlmgroup.collectorcoin.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@Controller
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

/* ORIGINAL CONTROLLER - BEGIN */
	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
/* ORIGINAL CONTROLLER - END */


//	@PostMapping("/signin")
//	public void authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//
//	  Authentication authentication = authenticationManager.authenticate(
//	      new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//	  SecurityContextHolder.getContext().setAuthentication(authentication);
//	  String jwt = jwtUtils.generateJwtToken(authentication);
//
//	  UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//	  List<String> roles = userDetails.getAuthorities().stream()
//	      .map(item -> item.getAuthority())
//	      .collect(Collectors.toList());
//
//	}

/* ORIGINAL SAMPLE - BEGIN */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

	  Authentication authentication = authenticationManager.authenticate(
	      new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

	  SecurityContextHolder.getContext().setAuthentication(authentication);
	  String jwt = jwtUtils.generateJwtToken(authentication);

	  UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	  List<String> roles = userDetails.getAuthorities().stream()
	      .map(item -> item.getAuthority())
	      .collect(Collectors.toList());


	  /* ORIGINAL SAMPLE - BEGIN */
	  return ResponseEntity.ok(new JwtResponse(jwt,
	                       userDetails.getId(),
	                       userDetails.getUsername(),
	                       userDetails.getEmail(),
	                       roles));
	  /* ORIGINAL SAMPLE - END */
	}
/* ORIGINAL SAMPLE - END */

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
				signUpRequest.getEmail(),
	            encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
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
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
