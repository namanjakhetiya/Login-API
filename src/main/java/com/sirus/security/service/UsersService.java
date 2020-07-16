package com.sirus.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sirus.security.model.Users;
import com.sirus.security.repo.UsersRepository;

@Service
public class UsersService {

	@Autowired
	UsersRepository usersRepository;

	public ResponseEntity<Users> add(Users user) {
		try {
			return ResponseEntity.ok(usersRepository.save(user));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<List<Users>> all() {
		try {
			return ResponseEntity.ok().body(usersRepository.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Users> update(Users user) {
		try {
			return ResponseEntity.ok().body(usersRepository.save(user));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Optional<Users>> getUserByMobile(String mobile) {
		try {
			return ResponseEntity.ok().body(usersRepository.findByMobile(mobile));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	public ResponseEntity<Optional<Users>> getUserById(Long id) {
		try {
			return ResponseEntity.ok().body(usersRepository.findById(id));
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

}
