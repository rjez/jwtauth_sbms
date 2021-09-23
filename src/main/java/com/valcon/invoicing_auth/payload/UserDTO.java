package com.valcon.invoicing_auth.payload;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String pwd;
    private boolean employee;
    private List<String> roles;

    public UserDTO() {
    }
    
    public UserDTO(
            Long id,
            String username,
            String email,
            String firstName,
            String lastName,
            List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public boolean isEmployee() {
		return employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return roles;
    }

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
