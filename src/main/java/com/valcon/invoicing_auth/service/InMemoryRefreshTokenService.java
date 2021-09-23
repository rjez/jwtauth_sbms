package com.valcon.invoicing_auth.service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

@Service
public class InMemoryRefreshTokenService implements RefreshTokenService {
	
	@Value("${com.valcon.invoicing.jwt.refreshTokenTimeoutInMin}")
	private long refreshTokenTimeoutInMin;
	
	private Cache<Long, String> userId2RefreshTokenMap;
	private ConcurrentHashMap<String, Long> refreshToken2UserIdMap;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Session timeout: "+refreshTokenTimeoutInMin);
		userId2RefreshTokenMap = Caffeine.newBuilder()
				.initialCapacity(64)
				.expireAfterAccess(refreshTokenTimeoutInMin, TimeUnit.MINUTES)
				.writer(new CacheWriter<Long, String>() {
					@Override
					public void write(Long userId, String refreshToken) {
						refreshToken2UserIdMap.put(refreshToken, userId);
						System.out.println("Write: "+userId+" -> "+refreshToken);
					}

					@Override
					public void delete(Long userId, String refreshToken, RemovalCause cause) {
						System.out.println("Delete: "+userId+" -> "+refreshToken);
						refreshToken2UserIdMap.remove(refreshToken);
					}
				})
				.removalListener((Long userId, String refreshToken, RemovalCause cause) -> {
					System.out.println("Remove: "+userId+" -> "+refreshToken);
					refreshToken2UserIdMap.remove(refreshToken);
				})
				.build();
		refreshToken2UserIdMap = new ConcurrentHashMap<>(64);
	}
	
	
	@Override
	public String createRefreshToken(Long userId) {
		String refreshToken = generateRefreshToken();
		userId2RefreshTokenMap.put(userId, refreshToken);
		System.out.println("U2R: "+userId2RefreshTokenMap);
		System.out.println("R2U: "+refreshToken2UserIdMap);
		return refreshToken;
	}

	@Override
	public UserIdAndToken refreshRefreshToken(String refreshToken) {
		if(refreshToken!=null) {
			Long userId = refreshToken2UserIdMap.remove(refreshToken);
			if (userId != null) {
				String newRefreshToken = generateRefreshToken();
				boolean ok = userId2RefreshTokenMap.asMap().replace(
						userId, refreshToken, newRefreshToken);
				if (ok) {
					System.out.println("U2R: " + userId2RefreshTokenMap);
					System.out.println("R2U: " + refreshToken2UserIdMap);
					return new UserIdAndToken(userId, newRefreshToken);
				}
			}
		}
		return null;
	}

	@Override
	public void removeRefreshToken(String refreshToken) {
		if(refreshToken!=null) {
			Long userId = refreshToken2UserIdMap.remove(refreshToken);
			if (userId != null) {
				userId2RefreshTokenMap.asMap().remove(userId, refreshToken);
			}
		}
	}

	@Override
	public long getRefreshTokenTimeoutInSec() {
		return 60 * refreshTokenTimeoutInMin;
	}

	//
	//
	//
	
	private static final int leftLimit = 48; // numeral '0'
	private static final int rightLimit = 122; // letter 'z'
	private static final Random random = new SecureRandom();
	
	private static String generateRefreshToken() {
		int targetStringLength = 64;
		return random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

}
