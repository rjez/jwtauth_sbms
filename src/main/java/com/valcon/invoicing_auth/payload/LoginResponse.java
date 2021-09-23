package com.valcon.invoicing_auth.payload;

import java.util.List;

public class LoginResponse {
	
	private UserDTO user;
	private TokensDTO tokens;

	public LoginResponse(
			UserDTO user,
			TokensDTO tokens) {
		setUser(user);
		setTokens(tokens);
	}

	public LoginResponse(
			String accessToken,
			String refreshToken,
			Long id,
			String username,
			String email,
			List<String> roles) {
		setUser(new UserDTO(id, username, email, null, null, roles));
		setTokens(new TokensDTO(accessToken, refreshToken));
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public TokensDTO getTokens() {
		return tokens;
	}

	public void setTokens(TokensDTO tokens) {
		this.tokens = tokens;
	}
}
