package com.valcon.invoicing_auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.valcon.invoicing_auth.entity.support.EntityId;

@Entity
public class Role extends EntityId {
	
	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false)
	private ERole name;

	public Role() {
	}

	public Role(ERole name) {
		this.name = name;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}
	
}
