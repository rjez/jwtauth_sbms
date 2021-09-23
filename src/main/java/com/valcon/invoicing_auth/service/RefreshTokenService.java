package com.valcon.invoicing_auth.service;

public interface RefreshTokenService {
	
	final class UserIdAndToken {
		private final Long userId;
		private final String token;
		
		public UserIdAndToken(Long userId, String token) {
			this.userId = userId;
			this.token = token;
		}

		public Long getUserId() {
			return userId;
		}

		public String getToken() {
			return token;
		}

	}
	
	String createRefreshToken(Long userId);
	
	UserIdAndToken refreshRefreshToken(String refreshToken);
	
	void removeRefreshToken(String refreshToken);

	long getRefreshTokenTimeoutInSec();

}
