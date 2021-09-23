package com.valcon.invoicing_auth.repository;

import java.util.stream.StreamSupport;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.valcon.invoicing_auth.entity.ERole;
import com.valcon.invoicing_auth.entity.Role;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
	
	Role findByName(ERole name);
	
	static Role getRole(Iterable<Role> roles, ERole roleName) {
		return StreamSupport.stream(roles.spliterator(), false)
				.filter(r -> r.getName().equals(roleName))
				.findFirst()
				.orElse(null);
	}

}
