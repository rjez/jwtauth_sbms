package com.valcon.invoicing_auth.exception;

import java.util.Map;

import com.valcon.invoicing_auth.entity.support.EntityId;

/**
 * @author rjez
 *
 */
public class EntryConstraintException extends EntryException {

	private final String[] constraints;

	public EntryConstraintException(Class<? extends EntityId> entityClass, String... constraints) {
		this(entityClass, null, constraints);
	}

	public EntryConstraintException(Class<? extends EntityId> entityClass, Long entityId, String... constraints) {
		super("entry.constraint", entityClass.getSimpleName(), entityId);
		this.constraints = constraints;
	}

	public <E extends EntityId> EntryConstraintException(E entity, String... constraints) {
		this(entity.getClass(), entity.getId(), constraints);
	}

	@Override
	public Map<String, Object> toLinkedMap() {
		Map<String, Object> map = super.toLinkedMap();
		if (constraints != null) {
			map.put("entryContraints", String.join(",", constraints));
		}
		return map;
	}

}
