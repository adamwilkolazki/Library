package pl.javastart.library.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException() {
    }
}
