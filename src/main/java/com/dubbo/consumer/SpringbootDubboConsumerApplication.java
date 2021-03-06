package com.dubbo.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


/**
 * @author chengwei
 */
@SpringBootApplication
@ImportResource(value = {"classpath:consumer.xml"})
public class SpringbootDubboConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDubboConsumerApplication.class, args);
	}
}
