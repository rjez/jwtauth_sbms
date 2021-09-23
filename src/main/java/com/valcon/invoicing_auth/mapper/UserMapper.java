package com.valcon.invoicing_auth.mapper;

import com.valcon.invoicing_auth.entity.ERole;
import com.valcon.invoicing_auth.entity.Role;
import com.valcon.invoicing_auth.entity.User;
import com.valcon.invoicing_auth.payload.UserDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author rjez
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    UserDTO toDto(User user);

    @InheritInverseConfiguration
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserDTO dto);

    default List<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(e -> e.getName().name())
                .collect(Collectors.toList());
    }

}
