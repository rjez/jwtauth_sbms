package com.valcon.invoicing_auth.exception;

import java.util.Map;

import com.valcon.invoicing_auth.entity.support.EntityId;

/**
 * @author rjez
 *
 */
public class EntryException extends ExpectedException {

	private String objectName;
	private Long id;

	public EntryException(String msg) {
		this(msg, null, null, null);
	}

	public EntryException(String msg, String objectName) {
		this(msg, objectName, null, null);
	}

	public EntryException(String msg, String objectName, Long id) {
		this(msg, objectName, id, null);
	}

	public <E extends EntityId> EntryException(String msg, E entity) {
		this(msg, entity, null);
	}
	
	public EntryException(String msg, String objectName, Throwable source) {
		this(msg, objectName, null, source);
	}

	public <E extends EntityId> EntryException(String msg, E entity, Throwable source) {
		this(msg, entity.getClass().getSimpleName(), entity.getId(), source);
	}
	
	public EntryException(String msg, String objectName, Long id, Throwable source) {
		super(msg, source);
		this.objectName = objectName;
		this.id = id;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, Object> toLinkedMap() {
		Map<String, Object> map = super.toLinkedMap();
		if (getObjectName() != null) {
			map.put("entryType", getObjectName());
		}
		if (getId() != null) {
			map.put("entryId", getId());
		}
		return map;
	}
}
