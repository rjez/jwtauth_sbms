package com.valcon.invoicing_auth.controller;

import com.valcon.invoicing.security.JwtUtils;
import com.valcon.invoicing.security.UserDetailsImpl;
import com.valcon.invoicing_auth.entity.User;
import com.valcon.invoicing_auth.payload.*;
import com.valcon.invoicing_auth.repository.UserRepository;
import com.valcon.invoicing_auth.service.RefreshTokenService;
import com.valcon.invoicing_auth.service.RefreshTokenService.UserIdAndToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // wildacrd (*) can't be used because credentials are not sent in such case
public class AuthController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";
	private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth/refresh-token";

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/login")
	@CrossOrigin(allowCredentials = "true")
	public ResponseEntity<?> loginUser(
			@RequestBody LoginRequest loginRequest,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		// LOG.info("Login - username: {}, password: {}", loginRequest.getUsername(), loginRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), 
						loginRequest.getPassword()));
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Long userId = userDetails.getId();
		String accessToken = jwtUtils.generateAccessToken(authentication);
		String refreshToken = refreshTokenService.createRefreshToken(userId);
		List<String> roles = userDetails.getRoles();
		LoginResponse loginResponse = new LoginResponse(
				accessToken,
				refreshToken,
				userId, 
				userDetails.getUsername(), 
				userDetails.getEmail(), 
				roles);
		addRefreshTokenCookie(httpServletRequest, httpServletResponse, refreshToken);
		return ResponseEntity.ok(loginResponse);
	}

	@GetMapping("/logout")
	@CrossOrigin(allowCredentials = "true")
	public ResponseEntity<?> logoutUser(
			@CookieValue(name=REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return logoutUserImpl(refreshToken, httpServletRequest, httpServletResponse);
	}
	
	@PostMapping("/logout")
	@CrossOrigin(allowCredentials = "true")
	public ResponseEntity<Void> logoutUser(
			@RequestBody LogoutRequest logoutRequest,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return logoutUserImpl(logoutRequest.getRefreshToken(), httpServletRequest, httpServletResponse);
	}

	private ResponseEntity<Void> logoutUserImpl(
			String refreshToken,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		LOG.info("Logout - token: {}", refreshToken);
		refreshTokenService.removeRefreshToken(refreshToken);
		deleteRefreshTokenCookie(httpServletRequest, httpServletResponse);
		return ResponseEntity.ok(null);
	}

	@GetMapping("/refresh-token")
	@CrossOrigin(allowCredentials = "true")
	public ResponseEntity<?> refreshToken(
			@CookieValue(name=REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return refreshTokenImpl(refreshToken, httpServletRequest, httpServletResponse);
	}

	@PostMapping("/refresh-token")
	@CrossOrigin(allowCredentials = "true")
	public ResponseEntity<?> refreshToken(
			@RequestBody RefreshTokenRequest refreshTokenRequest,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return refreshTokenImpl(refreshTokenRequest.getRefreshToken(), httpServletRequest, httpServletResponse);
	}

	private ResponseEntity<?> refreshTokenImpl(
			String refreshToken,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		LOG.info("Refresh token - token: {}", refreshToken);
		UserIdAndToken userIdAndToken = refreshTokenService.refreshRefreshToken(refreshToken);
		if(userIdAndToken==null) {
			deleteRefreshTokenCookie(httpServletRequest, httpServletResponse);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		refreshToken = userIdAndToken.getToken();
		User user = userRepository.findById(userIdAndToken.getUserId()).orElse(null);
		if(user==null) {
			deleteRefreshTokenCookie(httpServletRequest, httpServletResponse);
			refreshTokenService.removeRefreshToken(refreshToken);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String accessToken = jwtUtils.generateAccessToken(
				user.getId(),
				user.getUsername(),
				user.getRoleNamesAsStringList());
		List<String> roles = user.getRoles().stream().map(e->e.getName().name()).collect(Collectors.toList());
		//
		UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(),
				user.getFirstName(), user.getLastName(), roles);
		TokensDTO tokensDTO = new TokensDTO(accessToken, refreshToken);
		RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(userDTO, tokensDTO);
		addRefreshTokenCookie(httpServletRequest, httpServletResponse, refreshToken);
		return ResponseEntity.ok(refreshTokenResponse);
	}

	private void addRefreshTokenCookie(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			String refreshToken) {
		ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
				.maxAge(refreshTokenService.getRefreshTokenTimeoutInSec())
				.httpOnly(true)
				.secure(httpServletRequest.isSecure())
				.path(REFRESH_TOKEN_COOKIE_PATH)
				.sameSite("Strict")
				.build();
		httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private void deleteRefreshTokenCookie(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, null)
				.maxAge(0)
				.httpOnly(true)
				.secure(httpServletRequest.isSecure())
				.path(REFRESH_TOKEN_COOKIE_PATH)
				.sameSite("Strict")
				.build();
		httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
	//
	//
	//
	
	@GetMapping("/test/public")
	public String testPublicAccess() {
		return "TEST - public access";
	}
	
	@GetMapping("/test/authenticated")
	@PreAuthorize("isAuthenticated()")
	public String testAuthenticatedAccess() {
		return "TEST - authenticated access";
	}
	
	@GetMapping("/test/user")
//	@Secured("USER", "ROLE_USER")
	@PreAuthorize("hasAuthority('USER')")
	public String testUserAccess(Authentication auth) {
		return "TEST - user access; "+auth;
	}
	
	@GetMapping("/test/accountant")
	@PreAuthorize("hasAuthority('ACCOUNTANT')")
	public String testAccountantAccess() {
		return "TEST - accountant access";
	}
	
	@GetMapping("/test/admin")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String testAdminAccess() {
		return "TEST - admin access";
	}
	
}
