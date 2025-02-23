package com.school.elite.exception;

public class UserException extends Throwable {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidUserException extends RuntimeException {
        public InvalidUserException(String message) {
            super(message);
        }
    }

    public static class InvalidUserCredentialException extends RuntimeException {
        public InvalidUserCredentialException(String message) {
            super(message);
        }
    }

    public static class MissingFieldException extends RuntimeException {
        public MissingFieldException(String message) {
            super(message);
        }
    }
}
