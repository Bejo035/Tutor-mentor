package ge.batumi.tutormentor.exceptions;

/**
 * Custom exception for not found element
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 28.05.2025
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
