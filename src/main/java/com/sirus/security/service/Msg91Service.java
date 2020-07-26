package com.sirus.security.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class Msg91Service {

	// cache based on username and OPT MAX 8
	private static final Integer EXPIRE_MINS = 5;
	private LoadingCache<String, Integer> otpCache;

	@Autowired
	RestTemplate restTemplate;

	// Send SMS API
	@Value("${msg91-send-url}")
	private String MSG91_SEND_URL;
	@Value("${msg91-verify-url}")
	private String MSG91_VERIFY_URL;
	@Value("${msg91-retry-url}")
	private String MSG91_RETRY_URL;
	@Value("${authKey}")
	private String authKey;
	@Value("${templateId}")
	private String templateId;

	public Msg91Service() {
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
	public ResponseEntity<Map<String,String>> generateOtp(String username) {
		Random random = new Random();
		Integer otpValue = 100000 + random.nextInt(900000);
		otpCache.put(username, otpValue);
		Map<String, String> response = new HashMap<>(2);
		if (otpValue > 0) {
			response.put("user", username);
			response.put("otp", String.valueOf(otpValue));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put("message", "OTP not generated.");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Call this method in service class, if you want to user Msg91 APIs to Send OTP.
	public String sendOtp(String username) {
		Map<String, String> params = getSendParams(username);
		return post(MSG91_SEND_URL, params);
	}

	private Map<String, String> getSendParams(String mobile) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("authkey=", authKey);
		params.put("&template_id=", templateId);
		params.put("&mobile=", mobile);
		return params;
	}

	// This method is used to return the OPT number against Key->Key values is
	// username
	public String validateOtp(String username, Integer otp) throws ExecutionException {
		if (otp.equals(otpCache.get(username))) {
			clearOtpFromCache(username);
			return "success";
		} else {
			return "error";
		}
	}

	// This method is used to clear the OTP catched already
	public void clearOtpFromCache(String key) {
		otpCache.invalidate(key);
	}

	// Call this method in service class, if you want to user Msg91 APIs to verify OTP.
	public String verify(String username, Integer otp) {
		Map<String, String> params = getVerifyParams(username, otp.toString());
		return post(MSG91_VERIFY_URL, params);
	}

	private Map<String, String> getVerifyParams(String mobile, String otp) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("mobile=", mobile);
		params.put("&otp=", otp);
		params.put("&authkey=", authKey);
		return params;
	}

	// Call this method in service class, if you want to user Msg91 APIs to resend OTP.
	public void resendOtp(String username, String retryType) {

		Map<String, String> params = getRetryParams(username, retryType);
		post(MSG91_RETRY_URL, params);

	}

	private Map<String, String> getRetryParams(String mobile, String retryType) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("mobile=", mobile);
		params.put("&authkey=", authKey);
		params.put("&retrytype=", retryType);
		return params;
	}

	public ResponseEntity<Map<String,String>> resendOtp(String username) throws ExecutionException {
		Map<String, String> response = new HashMap<>(2);
		response.put("user", username);
		response.put("OTP", otpCache.get(username).toString());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public String post(final String path, final Map<String, String> params) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		StringBuilder sbPostData = new StringBuilder(path);
		for (Map.Entry<String, String> param : params.entrySet()) {
			sbPostData.append(param.getKey() + param.getValue());
		}
		// final string
		String mainUrl = sbPostData.toString();
		return restTemplate.exchange(mainUrl, HttpMethod.POST, entity, String.class).getBody();
	}
}
