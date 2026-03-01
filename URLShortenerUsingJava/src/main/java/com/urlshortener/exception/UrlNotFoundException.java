package com.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}

class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
