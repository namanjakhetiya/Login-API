package com.sirus.security.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sirus.security.model.OtpResponse;

@Service
public class OtpService {

	@Autowired
	OtpResponse otpResponse;

	// cache based on username and OPT MAX 8
	private static final Integer EXPIRE_MINS = 5;
	private LoadingCache<String, Integer> otpCache;
	// Send SMS API
	private static final String MSG91_SEND = "https://api.msg91.com/api/v5/otp?";
	private static final String MSG91_VERIFY = "https://api.msg91.com/api/v5/otp/verify?";
	private static final String MSG91_RETRY = "https://api.msg91.com/api/v5/otp/retry?";
	private final String authKey = "334549AIWjqfpww5eff28eeP1";
	private final String templateId = "5f10449bd6fc0540ba1040d3";

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
			Map<String, String> params = getSendParams(username, otpValue.toString());
			post(MSG91_SEND, params);
			return otpValue;
		}
	}

	// This method is used to return the OPT number against Key->Key values is
	// username
	public Integer validateOtp(String username, Integer otp) {
		try {
			Map<String, String> params = getVerifyParams(username, otp.toString());
			post(MSG91_VERIFY, params);
			return otpCache.get(username);
		} catch (Exception e) {
			return -1;
		}
	}

	// This method is used to clear the OTP catched already
	public void clearOtpFromCache(String key) {
		otpCache.invalidate(key);
	}

	private Map<String, String> getSendParams(String mobile, String otp) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("authkey=", authKey);
		params.put("&template_id=", templateId);
		params.put("&mobile=", mobile);
		params.put("&otp=", otp);
		return params;
	}

	private Map<String, String> getVerifyParams(String mobile, String otp) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("mobile=", mobile);
		params.put("&otp=", otp);
		params.put("&authkey=", authKey);
		return params;
	}

	public void post(final String path, final Map<String, String> params) {
		// Prepare Url
		URLConnection myURLConnection = null;
		URL myURL = null;
		BufferedReader reader = null;

		// Prepare parameter string
		StringBuilder sbPostData = new StringBuilder(path);
		for (Map.Entry<String, String> param : params.entrySet()) {
			sbPostData.append(param.getKey() + param.getValue());
			System.out.println(param.getKey() + param.getValue());
		}

		// final string
		String mainUrl = sbPostData.toString();
		try {
			// prepare connection
			myURL = new URL(mainUrl);
			myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			// reading response
			String response;
			while ((response = reader.readLine()) != null)
				// print response
				System.out.println(response);

			// finally close connection
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resendOtp(String username, String retryType) {
		try {
			Map<String, String> params = getRetryParams(username,retryType);
			post(MSG91_RETRY, params);
			//return otpCache.get(username);

		} catch (Exception e) {
			return;
		}
	}

	private Map<String, String> getRetryParams(String mobile, String retryType) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("mobile=", mobile);
		params.put("&authkey=", authKey);
		params.put("&retrytype=", retryType);
		return params;
	}

}
