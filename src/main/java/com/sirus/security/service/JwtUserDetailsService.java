package com.sirus.security.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sirus.security.model.Users;
import com.sirus.security.repo.UsersRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepository.findByMobile(username).get();
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		} else {
			return new User(user.getMobile(), user.getPassword(), new ArrayList<>());
		}
	}
}
