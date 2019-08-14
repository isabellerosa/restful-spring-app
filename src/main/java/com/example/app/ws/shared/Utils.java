package com.example.app.ws.shared;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Utils {
	private static final SecureRandom random = new SecureRandom();
	private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

	public String generateID(int length) {
		byte[] buffer = new byte[length];
		random.nextBytes(buffer);
		return encoder.encodeToString(buffer);
	}
}
