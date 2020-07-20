package com.sirus.security.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sirus.security.service.OtpService;

@RestController
public class OtpController {

	@Autowired
	OtpService otpService;

	@GetMapping("/generateOtp")
	public ResponseEntity<?> generateOtp() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Map<String, String> response = new HashMap<>(2);

		// check authentication
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		// generate OTP.
		Integer otp = otpService.generateOtp(username);
		if (otp == -1) {
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} else {
			response.put("user", username);
			response.put("otp", String.valueOf(otp));
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@GetMapping("/resendOtp")
	public ResponseEntity<?> resendOtp(@RequestParam String retryType) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Map<String, String> response = new HashMap<>(2);

		// check authentication
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		otpService.resendOtp(username,retryType);
		response.put("user", username);
		response.put("message", "OTP is Sent!");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/validateOtp")
	public ResponseEntity<?> validateOtp(@RequestParam int otp) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		Map<String, String> response = new HashMap<>(2);

		// check authentication
		if (username == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Integer serverOtp = -1;
		// Validate the Otp
		if (otp >= 0) {
			serverOtp = otpService.validateOtp(username, otp);
		}

		if (serverOtp > 0 && otp == serverOtp) {
			otpService.clearOtpFromCache(username);

			// success message
			response.put("status", "success");
			response.put("message", "Entered OTP is valid!");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("status", "error");
			response.put("message", "OTP is not valid!");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}
}
