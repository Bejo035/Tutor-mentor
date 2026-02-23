package ge.batumi.tutormentor.exceptions;

/**
 * Unchecked exception indicating that the client sent an invalid request.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
