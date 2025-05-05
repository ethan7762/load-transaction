package com.example.demo.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.model.common.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		List<String> errorFields = new ArrayList<>();
		
		ex.getBindingResult().getFieldErrors().stream().forEach(fieldError->{	
			String fieldName = fieldError.getField();
			String message = fieldError.getDefaultMessage();
			errorFields.add("'" + fieldName + "' " + message);
		});

		String errMessage = errorFields.stream().collect(Collectors.joining(", "));
		return new ResponseEntity<Object>(new ErrorResponse(errMessage), HttpStatus.BAD_REQUEST);
	}
}
