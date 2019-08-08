package com.example.app.ws.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.app.ws.ui.model.response.ErrorMessage;

@ControllerAdvice
public class AppExceptionsHandler {

	@ExceptionHandler(value = { UserServiceException.class })
	public ResponseEntity<Object> handleUserServiceException(UserServiceException e, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), e.getMessage());

		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<Object> handleGeneralExceptions(Exception e, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), e.getMessage());

		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
