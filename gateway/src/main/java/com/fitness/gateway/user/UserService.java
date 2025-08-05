package com.fitness.gateway.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final WebClient userServiceWebClient;
	
	public Mono<Boolean> validateUser(String userId){
				log.info("Checking existence of userid : {}",userId);
				log.info("/api/users/{}/validate",userId);
				return userServiceWebClient.get()
					.uri("/api/users/{}/validate",userId)
					.retrieve()
					.bodyToMono(Boolean.class)
					.onErrorResume(WebClientResponseException.class , e -> {
						if(e.getStatusCode() == HttpStatus.NOT_FOUND)
							return Mono.error(new RuntimeException("User Not Found." + userId));
						else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
							return Mono.error(new RuntimeException("Invalid Request."));
						return Mono.error(new RuntimeException("Unknown Error : " + e.getMessage()));
					});
	}

	public Mono<UserResponse> register(RegisterRequest req) {
		log.info("Calling Registration for Email : {}",req.getEmail());
		return userServiceWebClient.post()
				.uri("/api/users/register")
				.bodyValue(req)
				.retrieve()
				.bodyToMono(UserResponse.class)
				.onErrorResume(WebClientResponseException.class , e -> {
					if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
						return Mono.error(new RuntimeException("Bad Request."));
					else if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
						return Mono.error(new RuntimeException("Internal Server Error" + e.getMessage()));
					return Mono.error(new RuntimeException("Unknown Error : " + e.getMessage()));
				});
	}
}
