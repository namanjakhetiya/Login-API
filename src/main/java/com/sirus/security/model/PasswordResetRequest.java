package com.sirus.security.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordResetRequest implements Serializable {
	private static final long serialVersionUID = 5926468583005150707L;
	private String username;
	private String password;
	private Integer otp;

	public PasswordResetRequest(String username, Integer otp, String password) {
		this.username = username;
		this.otp = otp;
		this.password = password;
	}
}
