package com.valcon.invoicing_auth.config;

import com.valcon.invoicing.security.AbstractWebSecurityConfig;
import com.valcon.invoicing_auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends AbstractWebSecurityConfig {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	@Bean
	@Override
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http.authorizeRequests()
				.antMatchers("/api/auth/**").permitAll()
				.anyRequest().authenticated();
	}

}
