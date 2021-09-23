package com.valcon.invoicing_auth.payload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.valcon.invoicing.security.UserDetailsImpl;
import com.valcon.invoicing_auth.entity.ERole;

import org.springframework.security.core.Authentication;

public class UserResponse {

	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	
	public UserResponse() {
	}

	public UserResponse(
			Long id, 
			String username, 
			String email, 
			List<String> roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		if(roles==null) {
			roles = new ArrayList<>();
		}
		return roles;
	}
	
	//
	//
	//
	
	public static UserResponse build(Authentication authentication) {
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		UserResponse res = new UserResponse();
		res.setId(userDetails.getId());
		res.setUsername(userDetails.getUsername());
		res.setEmail(userDetails.getEmail());
		res.roles = userDetails.getRoles();
		return res;
	}
	
	@JsonIgnore
	public boolean isAdmin() {
		return roles.stream().anyMatch(r -> ERole.ADMIN.name().equals(r));
	}

}
