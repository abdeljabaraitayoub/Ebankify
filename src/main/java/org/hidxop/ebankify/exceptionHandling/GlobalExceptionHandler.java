// GlobalExceptionHandler.java
package org.hidxop.ebankify.exceptionHandling;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hidxop.ebankify.exceptionHandling.exceptions.InvalidStateException;
import org.hidxop.ebankify.exceptionHandling.exceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

//        String message = "Invalid request format. Please check the request body and ensure it matches the expected format";
        log.debug("JSON parse error: ", ex);

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStateException(
            InvalidStateException ex, HttpServletRequest request) {

        return createErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
                        (existing, replacement) -> existing + "; " + replacement
                ));

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                errors
        );
    }

    // Handle Constraint Violations
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Constraint violation",
                request.getRequestURI(),
                errors
        );
    }

    // Handle Data Integrity Violations (e.g., unique constraint violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                "Data integrity violation: " + ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
    }

    // Handle Authentication Errors
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleBadCredentials(
//            BadCredentialsException ex, HttpServletRequest request) {
//        return createErrorResponse(
//                HttpStatus.UNAUTHORIZED,
//                "Invalid credentials",
//                request.getRequestURI()
//        );
//    }

    // Handle Authorization Errors
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request.getRequestURI()
        );
    }

    // Handle Missing Parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing parameter: " + ex.getParameterName(),
                request.getRequestURI()
        );
    }

    // Handle Type Mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String error = String.format("Parameter '%s' should be of type %s",
                ex.getName(), ex.getRequiredType().getSimpleName());
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                error,
                request.getRequestURI()
        );
    }

    // Handle Custom Business Exception
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessException(
//            BusinessException ex, HttpServletRequest request) {
//        return createErrorResponse(
//                ex.getStatus(),
//                ex.getMessage(),
//                request.getRequestURI(),
//                ex.getErrors()
//        );
//    }

    // Handle All Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(
            Exception ex, HttpServletRequest request) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    // Helper Methods
    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpStatus status, String message, String path) {
        return createErrorResponse(status, message, path, null);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpStatus status, String message, String path, Map<String, String> errors) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errors
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}