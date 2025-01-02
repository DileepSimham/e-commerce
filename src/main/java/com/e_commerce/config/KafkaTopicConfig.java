package com.e_commerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
	
	
//	@Bean
//	public NewTopic ecommerceTopic() {
//		return TopicBuilder.name("order-status-updates").build();
//	}

}
