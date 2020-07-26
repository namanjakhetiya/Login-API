package com.sirus.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sirus.security.config.JwtTokenUtil;
import com.sirus.security.model.JwtResponse;
import com.sirus.security.model.OtpRequest;

@Service
public class OtpService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	Msg91Service msg91Service;

	public ResponseEntity<Map<String,String>> resend(String username) {
		try {
			return msg91Service.resendOtp(username);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Map<String,String>> send(String username) {
		try {
			final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
			// generate OTP.
			return msg91Service.generateOtp(userDetails.getUsername());
		} catch (UsernameNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<?> validate(OtpRequest authenticationRequest) {
		try {
			final UserDetails userDetails = jwtUserDetailsService
					.loadUserByUsername(authenticationRequest.getUsername());
			// Validate the Otp
			String verify = msg91Service.validateOtp(authenticationRequest.getUsername(),
					authenticationRequest.getOtp());
			if (verify.contains("success")) {
				final String token = jwtTokenUtil.generateToken(userDetails);
				return ResponseEntity.ok(new JwtResponse(token));
			} else {
				Map<String, String> response = new HashMap<>(2);
				response.put("status", "error");
				response.put("message", "OTP is not valid!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (UsernameNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
