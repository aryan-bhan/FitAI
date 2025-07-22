package com.fitness.aiservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.model.Activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
	
	private final ActivityAIService aiService;
	
	@RabbitListener(queues = "activity.queue")
	public void processActivity(Activity activity) {
		log.info("Recieved activity for processing : {}",activity.getId());
		log.info("Generated Recommendation: {}",aiService.generateRecommendation(activity));
	}
}
