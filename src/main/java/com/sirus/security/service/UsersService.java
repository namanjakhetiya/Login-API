package com.sirus.security.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sirus.security.model.PasswordResetRequest;
import com.sirus.security.model.Users;
import com.sirus.security.repo.UsersRepository;

@Service
public class UsersService {

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	Msg91Service msg91Service;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public ResponseEntity<List<Users>> all() {
		try {
			return ResponseEntity.ok().body(usersRepository.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Users> update(Users user) {
		try {
			user.setUpdatedOn(user.getUpdatedOn());
			return ResponseEntity.ok().body(usersRepository.save(user));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Users> getUserByMobile(String mobile) {
		try {
			return ResponseEntity.ok().body(usersRepository.findByMobile(mobile).get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Users> getUserById(Long id) {
		try {
			return ResponseEntity.ok().body(usersRepository.findById(id).get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Users> delete(Users user) {
		try {
			usersRepository.delete(user);
			return ResponseEntity.ok().body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Map<String, String>> resetPassword(PasswordResetRequest request) {
		try {
			// validate the Otp
			String verify = msg91Service.validateOtp(request.getUsername(), request.getOtp());
			if (verify.contains("success")) {
				request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
				usersRepository.updatePassword(request.getPassword(), new Date(), request.getUsername());
				Map<String, String> response = new HashMap<>(2);
				response.put("message", "Password reset successfully.");
				return ResponseEntity.ok().body(response);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
