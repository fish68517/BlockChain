package com.company.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.company.models.ERole;
import com.company.models.Role;
import com.company.repositories.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public void run(String... args) throws Exception {
    // Check and insert ROLE_OWNER
    if (!roleRepository.existsByName(ERole.ROLE_OWNER)) {
      Role ownerRole = new Role(ERole.ROLE_OWNER);
      roleRepository.save(ownerRole);
      System.out.println("Created ROLE_OWNER");
    }

    // Check and insert ROLE_INVESTOR
    if (!roleRepository.existsByName(ERole.ROLE_INVESTOR)) {
      Role investorRole = new Role(ERole.ROLE_INVESTOR);
      roleRepository.save(investorRole);
      System.out.println("Created ROLE_INVESTOR");
    }

    // Check and insert ROLE_RESTORER
    if (!roleRepository.existsByName(ERole.ROLE_RESTORER)) {
      Role restorerRole = new Role(ERole.ROLE_RESTORER);
      roleRepository.save(restorerRole);
      System.out.println("Created ROLE_RESTORER");
    }

    // Check and insert ROLE_BUYER
    if (!roleRepository.existsByName(ERole.ROLE_BUYER)) {
      Role buyerRole = new Role(ERole.ROLE_BUYER);
      roleRepository.save(buyerRole);
      System.out.println("Created ROLE_BUYER");
    }

    // Check and insert ROLE_ADMIN
    if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
      Role adminRole = new Role(ERole.ROLE_ADMIN);
      roleRepository.save(adminRole);
      System.out.println("Created ROLE_ADMIN");
    }

    // Check and insert ROLE_USER
    if (!roleRepository.existsByName(ERole.ROLE_USER)) {
      Role userRole = new Role(ERole.ROLE_USER);
      roleRepository.save(userRole);
      System.out.println("Created ROLE_USER");
    }
  }
}