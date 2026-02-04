package com.company;

import com.company.services.blockchain.TransactionMonitorService;
import com.icegreen.greenmail.spring.GreenMailBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
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

  /**
   * 🔍 强行检查：打印关键组件是否加载
   */
  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      System.out.println("==================================================");
      System.out.println("🔎 Spring Boot Bean 完整性自检 (Self-Check):");
      
      boolean hasListener = ctx.containsBean("transactionMonitorService");
      if (hasListener) {
        System.out.println("✅ [OK] TransactionMonitorService 已成功注入容器！");
      } else {
        System.err.println("❌ [FAIL] TransactionMonitorService 未找到！(根本没加载)");
      }
      
      boolean hasBlockchainService = ctx.containsBean("blockchainServiceImpl");
      if (hasBlockchainService) {
        System.out.println("✅ [OK] BlockchainServiceImpl 已成功注入容器！");
      } else {
        System.err.println("❌ [FAIL] BlockchainServiceImpl 未找到！");
      }
      System.out.println("==================================================");
    };
  }
}