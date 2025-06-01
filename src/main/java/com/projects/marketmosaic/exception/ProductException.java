package com.projects.marketmosaic.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductException extends RuntimeException {

	private final int errorCode; // Field to store the error code

	// Default constructor
	public ProductException() {
		super(); // Call the superclass (Exception) constructor
		this.errorCode = 0; // Default error code
	}

	// Constructor that accepts a message
	public ProductException(String message) {
		super(message); // Call the superclass constructor with a message
		this.errorCode = 0; // Default error code
	}

	// Constructor that accepts a message and a cause
	public ProductException(String message, Throwable cause) {
		super(message, cause); // Call the superclass constructor with a message and cause
		this.errorCode = 0; // Default error code
	}

	// Constructor that accepts a cause
	public ProductException(Throwable cause) {
		super(cause); // Call the superclass constructor with a cause
		this.errorCode = 0; // Default error code
	}

	// Constructor that accepts a message and an error code
	public ProductException(String message, int errorCode) {
		super(message); // Call the superclass constructor with a message
		this.errorCode = errorCode; // Set the custom error code
	}

	public ProductException(int errorCode, String message) {
		super(message); // Call the superclass constructor with a message
		this.errorCode = errorCode; // Set the custom error code
	}

	// Constructor that accepts a message, cause, and error code
	public ProductException(String message, Throwable cause, int errorCode) {
		super(message, cause); // Call the superclass constructor with message and cause
		this.errorCode = errorCode; // Set the custom error code
	}

	// Constructor that accepts a cause and error code
	public ProductException(Throwable cause, int errorCode) {
		super(cause); // Call the superclass constructor with a cause
		this.errorCode = errorCode; // Set the custom error code
	}

}
