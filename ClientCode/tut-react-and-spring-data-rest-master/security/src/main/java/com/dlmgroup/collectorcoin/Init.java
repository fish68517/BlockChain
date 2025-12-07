package com.dlmgroup.collectorcoin;
import javax.annotation.Resource;

import com.dlmgroup.collectorcoin.services.FileService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Init implements CommandLineRunner {

  @Resource
  FileService fs;

  @Override
  public void run(String...args) throws Exception {
    fs.init();
  }
}
