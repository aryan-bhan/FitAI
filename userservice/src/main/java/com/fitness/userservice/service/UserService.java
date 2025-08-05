package com.fitness.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

//import jakarta.validation.Valid;

@Service
@Slf4j
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	public UserResponse register(RegisterRequest request) {
		
		if(repository.existsByEmail(request.getEmail()))
		{
			User ExistingUser = repository.findByEmail(request.getEmail());
			UserResponse userResponse = new UserResponse();
			userResponse.setId(ExistingUser.getId());
			userResponse.setPassword(ExistingUser.getPassword());
			userResponse.setKeycloakId(ExistingUser.getKeycloakId());
			userResponse.setEmail(ExistingUser.getEmail());
			userResponse.setFirstName(ExistingUser.getFirstName());
			userResponse.setLastName(ExistingUser.getLastName());
			userResponse.setCreatedAt(ExistingUser.getCreatedAt());
			userResponse.setUpdatedAt(ExistingUser.getUpdatedAt());
			return userResponse;
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setKeycloakId(request.getKeycloakId());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		User savedUser = repository.save(user);
		UserResponse userResponse = new UserResponse();
		userResponse.setId(savedUser.getId());
		userResponse.setPassword(savedUser.getPassword());
		userResponse.setKeycloakId(savedUser.getKeycloakId());
		userResponse.setEmail(savedUser.getEmail());
		userResponse.setFirstName(savedUser.getFirstName());
		userResponse.setLastName(savedUser.getLastName());
		userResponse.setCreatedAt(savedUser.getCreatedAt());
		userResponse.setUpdatedAt(savedUser.getUpdatedAt());
		
		return userResponse;
	}

	public UserResponse getUserProfile(String userid) {
		User user = repository.findById(userid).orElseThrow(()-> new RuntimeException("User not fouund."));
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setPassword(user.getPassword());
		userResponse.setEmail(user.getEmail());
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setUpdatedAt(user.getUpdatedAt());
		return userResponse;
	}

	public Boolean existByUserId(String userid) {
		log.info("Exist By KeyCloack Id is called for : {}",userid);
		return repository.existsByKeycloakId(userid);
	}

}
