package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;

@SpringBootApplication
public class LoadTransactionApplication {

	private final static Logger logger = LoggerFactory.getLogger(LoadTransactionApplication.class);

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(LoadTransactionApplication.class);
		ConfigurableApplicationContext applicationContext = app.run(args);
		Environment env = applicationContext.getEnvironment();

		JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
		Job loadTransactionJob = applicationContext.getBean(Job.class);
		UserRepository userRepository = applicationContext.getBean(UserRepository.class);
		PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

		try {
			JobExecution jobExecution = jobLauncher.run(loadTransactionJob, new JobParameters());
			logger.info("Job Execution Status: " + jobExecution.getStatus());
		} catch (Exception e) {
			logger.error("jobExecution error", e);
		}
		
		try {
			User user = new User();
			user.setUsername(env.getProperty("default-user.username"));
			user.setPassword(passwordEncoder.encode(env.getProperty("default-user.password")));
			
			user = userRepository.save(user);
			logger.info("Default user created");
		} catch (Exception e) {
			logger.error("create default user error", e);
		}
		
	}

}
