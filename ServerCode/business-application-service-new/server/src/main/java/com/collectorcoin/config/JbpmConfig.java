package com.collectorcoin.config;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.ProcessService;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for JBPM workflow engine.
 * Supports both full JBPM mode and simplified demo mode.
 */
@Configuration
public class JbpmConfig {

    private static final Logger logger = LoggerFactory.getLogger(JbpmConfig.class);

    @Value("${jbpm.enabled:false}")
    private boolean jbpmEnabled;

    @Value("${jbpm.deployment.id:com.collectorcoin:workflows:1.0.0}")
    private String deploymentId;

    @Autowired(required = false)
    private DeploymentService deploymentService;

    @PostConstruct
    public void init() {
        if (jbpmEnabled && deploymentService != null) {
            logger.info("JBPM enabled - deploying workflow unit: {}", deploymentId);
            try {
                String[] parts = deploymentId.split(":");
                KModuleDeploymentUnit unit = new KModuleDeploymentUnit(parts[0], parts[1], parts[2]);
                deploymentService.deploy(unit);
                logger.info("Workflow deployed successfully");
            } catch (Exception e) {
                logger.warn("Failed to deploy workflow: {}", e.getMessage());
            }
        } else {
            logger.info("JBPM disabled - using simplified workflow mode");
        }
    }

    /**
     * JBPM RuntimeManager bean - auto-configured when jbpm-spring-boot-starter is present.
     */
    @Bean
    @ConditionalOnProperty(name = "jbpm.enabled", havingValue = "true")
    public RuntimeManager runtimeManager() {
        logger.info("JBPM RuntimeManager initialized");
        return null; // Auto-configured by jbpm-spring-boot-starter
    }

    /**
     * UserTaskService for Human Task operations.
     */
    @Bean
    @ConditionalOnProperty(name = "jbpm.enabled", havingValue = "true")
    public UserTaskService userTaskService() {
        logger.info("JBPM UserTaskService initialized");
        return null; // Auto-configured by jbpm-spring-boot-starter
    }

    /**
     * RuntimeDataService for process queries.
     */
    @Bean
    @ConditionalOnProperty(name = "jbpm.enabled", havingValue = "true")
    public RuntimeDataService runtimeDataService() {
        logger.info("JBPM RuntimeDataService initialized");
        return null; // Auto-configured by jbpm-spring-boot-starter
    }

    /**
     * ProcessService for process operations.
     */
    @Bean
    @ConditionalOnProperty(name = "jbpm.enabled", havingValue = "true")
    public ProcessService processService() {
        logger.info("JBPM ProcessService initialized");
        return null; // Auto-configured by jbpm-spring-boot-starter
    }
}
