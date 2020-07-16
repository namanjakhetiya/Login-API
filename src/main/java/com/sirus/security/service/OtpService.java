package com.sirus.security.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpService {

	// cache based on username and OPT MAX 8
	private static final Integer EXPIRE_MINS = 5;
	private LoadingCache<String, Integer> otpCache;

	public OtpService() {
		super();
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String username) {
						return 0;
					}
				});
	}

	// This method is used to push the opt number against Key. Rewrite the OTP if it
	// exists Using user id as key
	public Integer generateOtp(String username) {
		Random random = new Random();
		Integer otpValue = 100000 + random.nextInt(900000);
		otpCache.put(username, otpValue);
		if (otpValue == -1) {
			System.out.println("OTP generator is not working...");
			return -1;
		} else {
			System.out.println("OTP generated.");
			return otpValue;
		}
	}

	// This method is used to return the OPT number against Key->Key values is
	// username
	public Integer validateOtp(String username) {
		try {
			return otpCache.get(username);
		} catch (Exception e) {
			return -1;
		}
	}

	// This method is used to clear the OTP catched already
	public void clearOtpFromCache(String key) {
		otpCache.invalidate(key);
	}
}
