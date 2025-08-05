package com.fitness.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter{
	private final UserService userService;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
		String userid = exchange.getRequest().getHeaders().getFirst("X-User-ID");
		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		RegisterRequest registerRequest = getUserDetails(token);
		
		if(userid == null) {
			userid = registerRequest.getKeycloakId();
		}
		if(userid != null && token != null) {
			String finaluserid = userid;
			return userService.validateUser(userid)
					.flatMap(exist-> {
						if(!exist) {
							
							if(registerRequest != null) {
								return userService.register(registerRequest)
										.then(Mono.empty());
							}
							else return Mono.empty();
						} else {
							log.info("User already exists.");
							return Mono.empty();
						}})
					.then(Mono.defer(()->{
						ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
															.header("X-User-ID",finaluserid)
															.build();
						return chain.filter(exchange.mutate().request(mutatedRequest).build());
					}));
		}
		return chain.filter(exchange);
	}

	private RegisterRequest getUserDetails(String token) {
		try {
			String tokenWithoutBearer = token.replace("Bearer ", "").trim();
			SignedJWT signedjwt = SignedJWT.parse(tokenWithoutBearer);
			JWTClaimsSet claims = signedjwt.getJWTClaimsSet();
			
			RegisterRequest req = new RegisterRequest();
			req.setEmail(claims.getStringClaim("email"));
			req.setFirstName(claims.getStringClaim("given_name"));
			req.setKeycloakId(claims.getStringClaim("sub"));
			req.setLastName(claims.getStringClaim("family_name"));
			req.setPassword("dummypassword@123");
			
			return req;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
