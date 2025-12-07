package com.company.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.models.User;
import com.company.repositories.UserRepository;
import com.company.exception.ResourceNotFoundException;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(long id) throws ResourceNotFoundException {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  public User updateUser(long id, User user) throws ResourceNotFoundException {
    User existingUser = getUserById(id);

    // Update the fields that are allowed to be updated
    if (user.getUsername() != null) {
      existingUser.setUsername(user.getUsername());
    }
    if (user.getEmail() != null) {
      existingUser.setEmail(user.getEmail());
    }
    if (user.getPassword() != null) {
      existingUser.setPassword(user.getPassword());
    }
    if (user.getRoles() != null) {
      existingUser.setRoles(user.getRoles());
    }

    return userRepository.save(existingUser);
  }

  public void deleteUser(long id) throws ResourceNotFoundException {
    User user = getUserById(id);
    userRepository.delete(user);
  }
}