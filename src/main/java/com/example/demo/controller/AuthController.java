package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.common.ErrorResponse;
import com.example.demo.model.dto.AuthUserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.auth.JwtTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenService jwtTokenService;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/token")
	public ResponseEntity<Object> getAuthToken(@RequestBody @Valid AuthUserDto userDto) {

		try {
			
			User user = userRepository.findByUsername(userDto.getUsername()).orElse(null);

			if (user == null) {
				return new ResponseEntity<Object>(new ErrorResponse("Invalid username"), HttpStatus.BAD_REQUEST);
			}
			
			boolean valid = passwordEncoder.matches(userDto.getPassword(), user.getPassword());
			
			if (!valid) {
				return new ResponseEntity<Object>(new ErrorResponse("Invalid password"), HttpStatus.BAD_REQUEST);
			}
			
			Map<String, String> responseMap = new HashMap<>();
			responseMap.put("token", jwtTokenService.generateToken(user.getUsername()));
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<Object>(new ErrorResponse("Authentication failed"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
