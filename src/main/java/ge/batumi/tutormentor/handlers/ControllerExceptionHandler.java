package ge.batumi.tutormentor.handlers;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Custom exception handler class.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 28.05.2025
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.badRequest().header("errorMessage", exception.getMessage()).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> exception(Exception exception) {
        return ResponseEntity.internalServerError().header("errorMessage", exception.getMessage()).body(Map.of("message", "Something went wrong"));
    }
}
