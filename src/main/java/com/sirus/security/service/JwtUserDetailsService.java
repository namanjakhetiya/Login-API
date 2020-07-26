package com.sirus.security.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.sirus.security.config.JwtTokenUtil;
import com.sirus.security.model.JwtRequest;
import com.sirus.security.model.JwtResponse;
import com.sirus.security.model.Users;
import com.sirus.security.repo.UsersRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UsersRepository usersRepository;

	public ResponseEntity<JwtResponse> loginAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

			final UserDetails userDetails = loadUserByUsername(authenticationRequest.getUsername());

			final String token = jwtTokenUtil.generateToken(userDetails);

			return ResponseEntity.ok(new JwtResponse(token));
		} catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (BadCredentialsException e) {
			System.out.println("Unauthorized");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> optionaluser = usersRepository.findByMobile(username);
		if (!optionaluser.isPresent()) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		} else {
			Users user = optionaluser.get();
			return new User(user.getMobile(), user.getPassword(), new ArrayList<>());
		}
	}
	
	public ResponseEntity<Users> saveUser(Users user) {
		try {
			user.setCreateOn(user.getCreateOn());
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			return ResponseEntity.ok(usersRepository.save(user));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
