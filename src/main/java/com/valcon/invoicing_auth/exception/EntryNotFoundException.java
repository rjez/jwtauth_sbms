package com.valcon.invoicing_auth.exception;

import com.valcon.invoicing_auth.entity.support.EntityId;

/**
 * @author rjez
 *
 */
public class EntryNotFoundException extends EntryException {

	public EntryNotFoundException(Class<? extends EntityId> entityClass, long id) {
		super("entry.not.found", entityClass.getSimpleName(), id);
	}
}
