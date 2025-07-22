package com.fitness.activityservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
	
	private final ActivityRepository activityRepository;
	private final UserValidationService uservalidationService;
	private final RabbitTemplate rabbitTemplate;
	
	@Value("${rabbitmq.exchange.name}")
	private String exchange;
	
	@Value("${rabbitmq.routing.key}")
	private String routingKey;
	
	public ActivityResponse trackActivity(ActivityRequest request) {
		
		boolean validUser = uservalidationService.validateUser(request.getUserId());
		if(!validUser) throw new RuntimeException("Invalid User Id."+ request.getUserId());
		Activity activity = Activity.builder()
							.userId(request.getUserId())
							.type(request.getType())
							.duration(request.getDuration())
							.caloriesBurned(request.getCaloriesBurned())
							.startTime(request.getStartTime())
							.additionalMetrics(request.getAdditionalMetrics())
							.build();
		Activity savedActivity = activityRepository.save(activity);
		
//		Sending the activity to rabbitmq queue.
		try {
			rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);
		}catch(Exception e){
			log.error("Failed to Send activity to rabbitmq." + savedActivity.getId());
		}
		return mapToResponse(savedActivity);
	}
	
	private ActivityResponse mapToResponse(Activity activity)
	{
		ActivityResponse res = new ActivityResponse();
		res.setId(activity.getId());
		res.setUserId(activity.getUserId());
		res.setType(activity.getType());
		res.setDuration(activity.getDuration());
		res.setAdditionalMetrics(activity.getAdditionalMetrics());
		res.setCaloriesBurned(activity.getCaloriesBurned());
		res.setStartTime(activity.getStartTime());
		res.setCreatedAt(activity.getCreatedAt());
		res.setUpdatedAt(activity.getUpdatedAt());
		
		return res;
	}

	public List<ActivityResponse> getUserActivities(String userId) {
		List<Activity> activities = activityRepository.findByUserId(userId);
		return activities.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public ActivityResponse getActivityById(String activityId) {
		return activityRepository.findById(activityId)
				.map(this::mapToResponse)
				.orElseThrow(()-> new RuntimeException("Activity Not found with Id : " + activityId));
	}
	
}
