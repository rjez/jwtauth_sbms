package com.valcon.invoicing_auth.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.valcon.invoicing_auth.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.valcon.invoicing_auth.entity.ERole;
import com.valcon.invoicing_auth.entity.User;
import com.valcon.invoicing_auth.exception.EntryConstraintException;
import com.valcon.invoicing_auth.exception.EntryNotFoundException;
import com.valcon.invoicing_auth.exception.SavingException;
import com.valcon.invoicing_auth.payload.UserDTO;
import com.valcon.invoicing_auth.payload.UserResponse;
import com.valcon.invoicing_auth.repository.RoleRepository;
import com.valcon.invoicing_auth.repository.UserRepository;

@RestController
@RequestMapping(path = "/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UsersController {

	private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);
	public static final String PATH_ID_PARAM = "/{id}";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping
	public @ResponseBody List<UserDTO> getUsers() {
		return StreamSupport.stream(userRepository.findAll().spliterator(), false).map(this::toDto)
				.sorted((o1, o2) -> o1.getLastName().compareTo(o2.getLastName())).collect(Collectors.toList());
	}

	@GetMapping("/employees")
	public @ResponseBody List<UserDTO> getEmployees() {
		return userRepository.findByEmployee(Boolean.TRUE).stream().map(this::toDto).sorted((o1, o2) -> o1.getLastName().compareTo(o2.getLastName())).collect(Collectors.toList());
	}
	
	@GetMapping("/findByIds")
	public @ResponseBody List<UserDTO> findUsers(Authentication authentication, @RequestParam List<Long> id) {
		LOG.info("Find users by ids: " + id == null ? "null" : id.toString());
		return userRepository.findByIdIn(id).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	@GetMapping(path = PATH_ID_PARAM)
	public @ResponseBody ResponseEntity<UserDTO> get(@PathVariable(value = "id") long id) {
		LOG.info("Get user by id: " + id);
		Optional<User> u = userRepository.findById(id);
		if (u.isPresent()) {
			return ResponseEntity.of(Optional.of(toDto(u.get())));
		}
		throw new EntryNotFoundException(User.class, id);
	}

	@GetMapping("/self")
	public ResponseEntity<UserResponse> getSelf(Authentication authentication) {
		UserResponse userResponse = UserResponse.build(authentication);
		return ResponseEntity.ok(userResponse);
	}

	@GetMapping("/self4edit")
	public ResponseEntity<UserDTO> getSelf4edit(Authentication authentication) {
		UserResponse userResponse = UserResponse.build(authentication);
		return get(userResponse.getId());
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody ResponseEntity<UserDTO> createNew(@RequestBody UserDTO newUser) {
		if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
			throw new EntryConstraintException(User.class, "username");
		}
		try {
			User u = userRepository.save(toEntity(newUser));
			return ResponseEntity.of(Optional.of(toDto(u)));
		} catch (Exception e) {
			LOG.error("Coudln't save user.", e);
			throw new SavingException(User.class, e);
		}
	}

	@PutMapping(path = PATH_ID_PARAM)
	public @ResponseBody ResponseEntity<UserDTO> update(Authentication authentication,
			@PathVariable(value = "id") long id, @RequestBody UserDTO userDto) {
		UserResponse ur = getSelf(authentication).getBody();
		if (userDto.getId() != id || !ur.isAdmin() && userDto.getId() != id) {
			return ResponseEntity.badRequest().build();
		}
		if (!userRepository.existsById(id)) {
			throw new EntryNotFoundException(User.class, id);
		}
		if (userRepository.findByUsernameAndIdNot(userDto.getUsername(), userDto.getId()).isPresent()) {
			throw new EntryConstraintException(User.class, "username");
		}
		User entity = toEntity(userDto);
		// password could be empty in DTO if not changed. Load and use the existing one.
		if (StringUtils.isEmpty(userDto.getPwd())) {
			entity.setPassword(userRepository.findById(id).get().getPassword());
		}
		try {
			return ResponseEntity.ok(toDto(userRepository.save(entity)));
		} catch (Exception e) {
			throw new SavingException(entity, e);
		}
	}

	private UserDTO toDto(User entity) {
		return userMapper.toDto(entity);
	}

	private User toEntity(UserDTO dto) {
		return userMapper.toEntity(dto);
	}
}
