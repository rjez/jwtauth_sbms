package com.valcon.invoicing_auth.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author rjez
 *
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntryNotFoundException.class)
	public ResponseEntity<Object> handleEntryNotFoundException(EntryNotFoundException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(SavingException.class)
	public ResponseEntity<Object> handleEntryNotSavedException(SavingException ex, WebRequest request) {
		return new ResponseEntity<>(ex.toLinkedMap(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EntryConstraintException.class)
	public ResponseEntity<Object> handleEntryConstraintException(EntryConstraintException ex, WebRequest request) {
		return handleException(ex, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(EntryException.class)
	public ResponseEntity<Object> handleEntryException(EntryException ex, WebRequest request) {
		return handleException(ex, HttpStatus.PRECONDITION_REQUIRED);
	}

	@ExceptionHandler(ExpectedException.class)
	public ResponseEntity<Object> handleExpectedException(ExpectedException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST);
	}

	private ResponseEntity<Object> handleException(ExpectedException ex, HttpStatus httpStatus) {
		return new ResponseEntity<>(ex.toLinkedMap(), httpStatus);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status.value());

		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.toList());

		body.put("errors", errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}