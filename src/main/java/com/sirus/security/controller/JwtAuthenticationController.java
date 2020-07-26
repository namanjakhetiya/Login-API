package com.sirus.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sirus.security.model.JwtRequest;
import com.sirus.security.model.JwtResponse;
import com.sirus.security.model.Users;
import com.sirus.security.service.JwtUserDetailsService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class JwtAuthenticationController {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		return jwtUserDetailsService.loginAuthenticationToken(authenticationRequest);
	}

	@RequestMapping(value = "register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody Users user) throws Exception {
		return ResponseEntity.ok(jwtUserDetailsService.saveUser(user));
	}

}
