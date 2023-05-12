package br.com.curso.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundExcepcion extends RuntimeException{

	public ResourceNotFoundExcepcion(String ex) {
		super(ex);
	}
	
	private static final long serialVersionUID = 1L;
	}
