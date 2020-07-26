package com.sirus.security.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtpRequest implements Serializable {
	private static final long serialVersionUID = 5926468583005150707L;
	private String username;
	private Integer otp;

	public OtpRequest(String username, Integer otp) {
		this.username = username;
		this.otp = otp;
	}
}
