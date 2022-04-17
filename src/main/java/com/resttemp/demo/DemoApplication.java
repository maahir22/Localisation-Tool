package com.resttemp.demo;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
	@Bean
	public ThreadPoolTaskExecutor getAsyncExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(7);
	    executor.setMaxPoolSize(42);
	    executor.setQueueCapacity(11);
	    executor.setThreadNamePrefix("threadPoolExecutor-");
	    executor.initialize();
	    return executor;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
