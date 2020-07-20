package com.sirus.security.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sirus.security.model.Users;
import com.sirus.security.service.UsersService;

@RestController
@RequestMapping("/api/v1/user/")
public class UsersController {

	@Autowired
	UsersService usersService;

	@PostMapping("add")
	public ResponseEntity<Users> add(@RequestBody Users user) {
		return usersService.add(user);
	}

	@GetMapping("all")
	public ResponseEntity<List<Users>> all() {
		return usersService.all();
	}

	@GetMapping("byid")
	public ResponseEntity<Optional<Users>> getUserById(@RequestParam Long id) {
		return usersService.getUserById(id);
	}

	@GetMapping("bymobile")
	public ResponseEntity<Optional<Users>> getUserByMobile(@RequestParam String mobile) {
		return usersService.getUserByMobile(mobile);
	}

	@PutMapping("update")
	public ResponseEntity<Users> update(@RequestBody Users user) {
		return usersService.update(user);
	}

	@DeleteMapping("delete")
	public ResponseEntity<Users> delete(@RequestBody Users user) {
		return usersService.delete(user);
	}

}
