package com.valcon.invoicing_auth.payload;

public class LogoutRequest {
	
//	@NotBlank
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
