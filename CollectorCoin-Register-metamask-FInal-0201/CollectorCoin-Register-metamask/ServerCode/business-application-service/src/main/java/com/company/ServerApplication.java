package com.company;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication()
@RestController
@ImportAutoConfiguration(GreenMailBean.class)
@EnableAutoConfiguration
@EntityScan("com.company.models")
public class ServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}
