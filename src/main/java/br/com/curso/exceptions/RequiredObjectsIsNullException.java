package br.com.curso.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectsIsNullException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public RequiredObjectsIsNullException() {
		super("It is not allowed to persiste a null objetc");
	}
	

	public RequiredObjectsIsNullException(String ex) {
		super(ex);
	}
	
	
	
}
