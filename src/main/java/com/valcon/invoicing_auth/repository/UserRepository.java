package com.valcon.invoicing_auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.valcon.invoicing_auth.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	Optional<User> findByUsername(String username);
	
	Optional<User> findByUsernameAndIdNot(String username, Long id);
	
	List<User> findByIdIn(List<Long> ids);
	
	List<User> findByEmployee(Boolean employee);

}
