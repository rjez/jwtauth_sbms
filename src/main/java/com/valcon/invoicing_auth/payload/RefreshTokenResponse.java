package com.valcon.invoicing_auth.payload;

public class RefreshTokenResponse {

	private UserDTO user;
	private TokensDTO tokens;

	public RefreshTokenResponse(
			UserDTO user,
			TokensDTO tokens) {
		setUser(user);
		setTokens(tokens);
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
