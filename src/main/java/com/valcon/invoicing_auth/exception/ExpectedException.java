package com.valcon.invoicing_auth.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author rjez
 *
 */
public class ExpectedException extends RuntimeException {

	private Throwable source;

	public ExpectedException(String msg) {
		this(msg, null);
	}

	public ExpectedException(String msg, Throwable source) {
		super(msg);
		this.source = source;
	}

	public Throwable getSource() {
		return source;
	}

	public void setSource(Throwable source) {
		this.source = source;
	}

	public Map<String, Object> toLinkedMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		if (getMessage() != null) {
			map.put("message", getMessage());
		}
		if (getSource() != null) {
			map.put("sourceType", getSource().getClass().getSimpleName());
			if (getSource().getMessage() != null) {
				map.put("sourceMessage", getSource().getMessage());
			}
		}
		return map;
	}
}
