package com.valcon.invoicing_auth.mapper;

import com.valcon.invoicing_auth.entity.User;
import com.valcon.invoicing_auth.payload.UserDTO;
import com.valcon.invoicing_auth.repository.RoleRepository;
import com.valcon.invoicing_auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * @author rjez
 */
public abstract class UserMapperDecorator implements UserMapper {

    @Qualifier("delegate")
    private UserMapper delegate;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserMapperDecorator() {
    }

    @Override
    public UserDTO toDto(User user) {
        UserDTO dto = delegate.toDto(user);
        dto.setRoles(user.getRoleNamesAsStringList());
        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {
        User user = delegate.toEntity(dto);
        // handle roles
        user.getRoles().clear();
        roleRepository.findAll().forEach(r -> {
            if (dto.getRoles().contains(r.getName().name())) {
                user.getRoles().add(r);
            }
        });
        // handle password
        if (StringUtils.isEmpty(dto.getPwd())) {
            if (user.getId() != 0) {
                user.setPassword(userRepository.findById(user.getId()).get().getPassword());
            }
        }
        else {
            user.setPassword(passwordEncoder.encode(dto.getPwd()));
        }
        return user;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setDelegate(UserMapper delegate) {
        this.delegate = delegate;
    }
}