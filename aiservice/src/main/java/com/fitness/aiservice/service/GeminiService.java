package com.fitness.aiservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
	
	@Value("${gemini.api.url}")
	private String geminiurl;
	@Value("${gemini.api.key}")
	private String geminikey;
	
	private final WebClient webclient;
	public GeminiService(WebClient.Builder webClientBuilder) {
		this.webclient = webClientBuilder.build();
	}
	
	public String getAnswer(String question) {
		Map<String,Object> requestBody = Map.of(
			"contents",new Object[] {
					Map.of("parts",new Object[] {
							Map.of("text",question)})
		});
		
		String response = webclient.post()
				.uri(geminiurl+geminikey)
				.header("Content-Type","application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return response;
	}
}
