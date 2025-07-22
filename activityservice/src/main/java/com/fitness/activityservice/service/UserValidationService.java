package com.fitness.activityservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
	private final WebClient userServiceWebClient;
	
	public boolean validateUser(String userId){
		try{
				log.info("Checking existence of userid : {}",userId);
				return Boolean.TRUE.equals(userServiceWebClient.get()
				.uri("/api/users/{userId}/validate",userId)
				.retrieve()
				.bodyToMono(Boolean.class)
				.block());
		}
		catch (WebClientRequestException e) {
		    throw new RuntimeException("Service Unavailable: " + e.getMessage(), e);
		}
		catch(WebClientResponseException e){
			if(e.getStatusCode() == HttpStatus.NOT_FOUND)
				throw new RuntimeException("User Not Found." + userId);
			else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
				throw new RuntimeException("Invalid Request.");
		}
		return false;
	}
}
