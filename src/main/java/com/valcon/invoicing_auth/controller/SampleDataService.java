package com.valcon.invoicing_auth.controller;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.valcon.invoicing_auth.entity.ERole;
import com.valcon.invoicing_auth.entity.Role;
import com.valcon.invoicing_auth.entity.User;
import com.valcon.invoicing_auth.repository.RoleRepository;
import com.valcon.invoicing_auth.repository.UserRepository;

@Service
public class SampleDataService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public void createSampleData() {
		if (roleRepository.count() == 0) {
			createRoles();
			createUsers();
		}
	}
	
	private void createRoles() {
		Stream.of(ERole.values())
			.map(Role::new)
			.forEach(e -> roleRepository.save(e));
	}
	
	private void createUsers() {
		Iterable<Role> roles = roleRepository.findAll();
		Role adminRole = RoleRepository.getRole(roles, ERole.ADMIN);
		Role accountantRole = RoleRepository.getRole(roles, ERole.ACCOUNTANT);
		Role userRole = RoleRepository.getRole(roles, ERole.USER);
		//
		createUser("admin", "admin", "admin@valconsystems.com", "Admin", "Admin", adminRole, userRole);
		createUser("accountant", "accountant", "accountant@valconsystems.com", "Mzdová", "Účetní", accountantRole, userRole);
		createUser("user", "user", "user@valconsystems.com", "Obyčejný", "Uživatel", userRole);
	}
	
	private void createUser(
			String username,
			String password,
			String email,
			String firstName,
			String lastName,
			Role... roles) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setFirstName(firstName);
		user.setLastName(lastName);
		if(roles!=null) {
			user.getRoles().addAll(Arrays.asList(roles));
		}
		userRepository.save(user);
	}
	
}
