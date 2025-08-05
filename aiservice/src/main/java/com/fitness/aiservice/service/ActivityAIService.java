package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
//import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
	private final GeminiService geminiService;
	
	public Recommendation generateRecommendation(Activity activity) {
		String prompt = createPromptForActivity(activity);
		String aiResponse = geminiService.getAnswer(prompt);
//		log.info("Message from gemini : {}",aiResponse);
		return processAiResponse(activity, aiResponse);
	}

	private String createPromptForActivity(Activity activity) {
		 return String.format("""
			        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
			        {
			          "analysis": {
			            "overall": "Overall analysis here",
			            "pace": "Pace analysis here",
			            "heartRate": "Heart rate analysis here",
			            "caloriesBurned": "Calories analysis here"
			          },
			          "improvements": [
			            {
			              "area": "Area name",
			              "recommendation": "Detailed recommendation"
			            }
			          ],
			          "suggestions": [
			            {
			              "workout": "Workout name",
			              "description": "Detailed workout description"
			            }
			          ],
			          "safety": [
			            "Safety point 1",
			            "Safety point 2"
			          ]
			        }

			        Analyze this activity:
			        Activity Type: %s
			        Duration: %d minutes
			        Calories Burned: %d
			        Additional Metrics: %s
			        
			        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
			        Ensure the response follows the EXACT JSON format shown above.
			        """,
			                activity.getType(),
			                activity.getDuration(),
			                activity.getCaloriesBurned(),
			                activity.getAdditionalMetrics()
			        );
			    }
	
	private Recommendation processAiResponse(Activity activity,String aiResponse) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = 	mapper.readTree(aiResponse);
			
			JsonNode textNode = rootNode.path("candidates")
								.get(0)
								.path("content")
								.path("parts")
								.get(0)
								.path("text");
			
			String jsonContent = textNode.asText().replaceAll("```json\\n","")
								 .replaceAll("\\n```","")
								 .trim();
			
//			log.info("Parsed response from AI : {}",jsonContent);
			JsonNode analysis = mapper.readTree(jsonContent);
			JsonNode analysisNode = analysis.path("analysis");
			StringBuilder fullanalysis = new StringBuilder();
			addAnalysisSection(analysisNode,fullanalysis,"overall","Overall:");
			addAnalysisSection(analysisNode,fullanalysis,"pace","Pace:");
			addAnalysisSection(analysisNode,fullanalysis,"heartRate","Heart Rate:");
			addAnalysisSection(analysisNode,fullanalysis,"caloriesBurned","Calories:");
			
			List<String> improvements = extractImprovements(analysis.path("improvements"));
			List<String> suggestions = extractSuggestions(analysis.path("suggestions"));
			List<String> safety = extractSafety(analysis.path("safety"));
			
			return Recommendation.builder()
					.activityId(activity.getId())
					.userId(activity.getUserId())
					.activityType(activity.getType())
					.recommendation(fullanalysis.toString().trim())
					.improvements(improvements)
					.suggestions(suggestions)
					.safety(safety)
					.createdAt(LocalDateTime.now())
					.build();
			
		}catch(Exception e) {
			e.printStackTrace();
			return createDefaultRecommendation(activity);
		}
	}

	private Recommendation createDefaultRecommendation(Activity activity) {
		return Recommendation.builder()
				.activityId(activity.getId())
				.userId(activity.getUserId())
				.activityType(activity.getType())
				.recommendation("Unable to generate recommendations.")
				.improvements(Collections.singletonList("No improvements to be made as such."))
				.suggestions(Collections.singletonList("Not able to generate suggestions."))
				.safety(Collections.singletonList("Follow usual safety guidelines"))
				.createdAt(LocalDateTime.now())
				.build();
	}

	private List<String> extractSafety(JsonNode Node) {
		List<String> safety = new ArrayList<>();
		if(Node.isArray()) {
			Node.forEach(guideline ->{
				safety.add(guideline.asText());
				});}
		return safety.isEmpty() ? Collections.singletonList("Follow general Safety Guidelines.") : safety;
		
	}

	private List<String> extractSuggestions(JsonNode Node) {
		List<String> res = new ArrayList<>();
		if(Node.isArray()) {
			Node.forEach(suggestion -> {
				String area = suggestion.path("workout").asText();
				String detail = suggestion.path("description").asText();
				res.add(String.format("%s : %s",area,detail));
			});
		}
		return res.isEmpty() ? Collections.singletonList("No specific improvements provided.") : res;
	}

	private List<String> extractImprovements(JsonNode Node) {
		List<String> res = new ArrayList<>();
		if(Node.isArray()) {
			Node.forEach(improvement -> {
				String workout = improvement.path("area").asText();
				String description = improvement.path("recommendation").asText();
				res.add(String.format("%s : %s",workout,description));
			});
		}
		return res.isEmpty() ? Collections.singletonList("No specific suggestions provided.") : res;
	}

	private void addAnalysisSection(JsonNode analysisNode, StringBuilder fullanalysis, String key, String prefix) {
		if(!analysisNode.path(key).isMissingNode()) {
			fullanalysis.append(prefix)
						.append(analysisNode.path(key).asText())
						.append("\n\n");
							
		}
		
	}
	
}
