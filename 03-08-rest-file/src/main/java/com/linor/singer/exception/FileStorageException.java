package com.linor.singer.exception;

public class FileStorageException extends RuntimeException {
	private static final long serialVersionUID = -5290293409485429846L;

	public FileStorageException(String message) {
		super(message);
	}
	
	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
