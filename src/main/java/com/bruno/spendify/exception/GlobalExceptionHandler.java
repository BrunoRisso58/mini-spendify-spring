package com.bruno.spendify.exception;

import com.bruno.spendify.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(ResourceNotFoundException ex) {
        return ApiError.of(404, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.computeIfAbsent(error.getField(), field -> new ArrayList<>()).add(error.getDefaultMessage()));
        return ApiError.of(422, "Validation failed", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ApiError.of(400, "Malformed request body. Check the JSON syntax, the date format (yyyy-MM-dd) and the category.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ApiError.of(400, "Invalid value for parameter '" + ex.getName() + "'");
    }

    // Spring's own HTTP errors (unknown route, unsupported method...) keep their original status;
    // anything else is an unexpected failure and must not leak internal details to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        if (ex instanceof ErrorResponse httpError) {
            int status = httpError.getStatusCode().value();
            return ResponseEntity.status(status).body(ApiError.of(status, ex.getMessage()));
        }
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(ApiError.of(500, "Internal server error. Please try again later."));
    }
}
