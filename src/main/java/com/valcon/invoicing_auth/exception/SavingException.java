package com.valcon.invoicing_auth.exception;

import com.valcon.invoicing_auth.entity.support.EntityId;

/**
 * @author rjez
 *
 */
public class SavingException extends EntryException {

	public SavingException(Class<? extends EntityId> entityClass, Throwable t) {
		this(entityClass, null, t);
	}

	public <E extends EntityId> SavingException(E entity) {
		this(entity, null);
	}

	public <E extends EntityId> SavingException(E entity, Throwable t) {
		this(entity.getClass(), entity.getId(), t);
	}

	public SavingException(Class<? extends EntityId> entityClass, Long id, Throwable t) {
		super("entry.not.saved", entityClass.getSimpleName(), id, t);
	}
}
