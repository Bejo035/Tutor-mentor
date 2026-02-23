package ge.batumi.tutormentor.handlers;

import ge.batumi.tutormentor.exceptions.BadRequestException;
import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
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

    /**
     * Handles {@link ResourceNotFoundException} by returning a 400 response with the error message.
     */
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.badRequest().header("errorMessage", exception.getMessage()).body(Map.of("message", exception.getMessage()));
    }

    /**
     * Handles {@link BadRequestException} by returning a 400 response with the error message.
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<?> badRequestException(BadRequestException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    /**
     * Handles {@link ExpectationsNotMet} by returning a 400 response with the error message.
     */
    @ExceptionHandler(value = {ExpectationsNotMet.class})
    public ResponseEntity<?> badRequestException(ExpectationsNotMet exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    /**
     * Handles bean validation failures by returning a 400 response with per-field error details.
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> validationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(Map.of("message", "Validation failed", "errors", errors));
    }
}
