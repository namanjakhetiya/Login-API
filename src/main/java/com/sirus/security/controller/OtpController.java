package com.sirus.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sirus.security.model.OtpRequest;
import com.sirus.security.model.PasswordResetRequest;
import com.sirus.security.service.OtpService;
import com.sirus.security.service.UsersService;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/otp/")
public class OtpController {

	@Autowired
	OtpService otpService;
	
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "send", method = RequestMethod.POST)
	public ResponseEntity<?> send(@RequestParam String username) {
		return otpService.send(username);
	}

	@GetMapping("resend")
	public ResponseEntity<Map<String,String>> resend(@RequestParam String username) {
		return otpService.resend(username);
	}

	@PostMapping("validate")
	public ResponseEntity<?> validate(@RequestBody OtpRequest authenticationRequest) throws Exception {
		return otpService.validate(authenticationRequest);
	}
	
	@RequestMapping(value = "reset/password", method = RequestMethod.POST)
	public ResponseEntity<Map<String,String>> resetPassword(@RequestBody PasswordResetRequest request) {
		return usersService.resetPassword(request);
	}
	
}
