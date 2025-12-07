package com.dlmgroup.collectorcoin.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dlmgroup.collectorcoin.exception.ResourceNotFoundException;
import com.dlmgroup.collectorcoin.models.User;
import com.dlmgroup.collectorcoin.repositories.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String username) {
		List<User> users = new ArrayList<User>();
		
		if(username == null)
			userRepository.findAll().forEach(users::add);
		//else
		//	userRepository.findByUsername(username).forEach(users::add);
		
		if (users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping("/users/{id}")
	  public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		User user = userRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));

	    return new ResponseEntity<>(user, HttpStatus.OK);
	  }

	/* DON'T NEED - SEE AuthController.java */
//	@PostMapping("/users")
//	public ResponseEntity<User> createUser(@RequestBody User user) {
//		User _user = userRepository.save(new User(user.getUsername(), user.getEmail(), true));
//	    return new ResponseEntity<>(_user, HttpStatus.CREATED);
//	  }
	
	@PutMapping("/users/{id}")
	  public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
		User _user = userRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));

		_user.setUsername(user.getUsername());
		_user.setEmail(user.getEmail());
	    
	    return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
	  }

	  @DeleteMapping("/users/{id}")
	  public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
		  userRepository.deleteById(id);
	    
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	  }

	  @DeleteMapping("/users")
	  public ResponseEntity<HttpStatus> deleteAllUsers() {
		  userRepository.deleteAll();
	    
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	  }	
}
