package com.fitness.aiservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepsitory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
	private final RecommendationRepsitory recommendationRepsitory;

	public List<Recommendation> getUserRecommendation(String userId) {
		return recommendationRepsitory.findByUserId(userId);
	}

	public Recommendation getActivityRecommendation(String activityId) {
		return recommendationRepsitory.findByActivityId(activityId)
				.orElseThrow(()-> new RuntimeException("No recommendations found for Activity : " + activityId));
	}
}
