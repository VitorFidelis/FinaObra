package br.com.finatech.application.exception;

public class AlreadyExistingException extends RuntimeException {

    public AlreadyExistingException(String message) {
        super(message);
    }
}
