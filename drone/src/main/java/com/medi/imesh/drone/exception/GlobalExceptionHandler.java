package com.medi.imesh.drone.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for handling all the exceptions in the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler method to handle business logic violations.
     *
     * @param e - Validation exception
     * @return - Bad request response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Request validation failed");
        errors.put("detail", e.getMessage());
        return ResponseEntity.badRequest().body(errors);

    }

    /**
     * Handler method to handle Json mapping violations.
     *
     * @param e - ConstraintViolation exception
     * @return - Bad request response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {

        Map<String, String> errors = new HashMap<>();

        for (var violation : e.getConstraintViolations()) {
            String message = violation.getMessage();
            errors.put("error", "Bad request");
            errors.put("detail", message);
        }
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handler method to handle Json parsing violations.
     *
     * @param e - HttpMessageNotReadable exception
     * @return - Bad request response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {

        Throwable mostSpecificCause = e.getMostSpecificCause();
        Map<String, String> errors = new HashMap<>();

        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException) mostSpecificCause;
            String field = formatException.getPath().get(0).getFieldName();
            String invalidValue = formatException.getValue().toString();
            errors.put("error", "Error parsing JSON request");
            errors.put("detail", String.format("invalid value %s for field: %s", invalidValue, field));
            return ResponseEntity.badRequest().body(errors);
        }

        // Generic error structure for other types of parsing issues
        errors.put("error", "Error parsing JSON request");
        errors.put("detail", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handler method to handle violations not handled by above methods.
     *
     * @param e - MethodArgumentNotValid exception
     * @return - Bad request response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            if (error instanceof FieldError fieldError) {
                String errorMessage = fieldError.getDefaultMessage();
                errors.put("error", "Bad request");
                errors.put("detail", errorMessage);
            }
        }

        return ResponseEntity.badRequest().body(errors);
    }

}
