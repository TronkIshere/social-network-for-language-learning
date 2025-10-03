package com.private_project.social_network_for_language_learning.exception;

import com.private_project.social_network_for_language_learning.dto.response.common.ResponseAPI;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ResponseAPI<String>> handlingRunTimeException(RuntimeException exception) {
        ResponseAPI<String> apiResponse = new ResponseAPI<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(value = ApplicationException.class)
    ResponseEntity<ResponseAPI<?>> handlingAppException(ApplicationException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ResponseAPI<?> apiResponse = new ResponseAPI<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }


    @ExceptionHandler( value = MethodArgumentNotValidException.class)
    ResponseEntity<ResponseAPI<?>> handleValidationException(MethodArgumentNotValidException exception) {
        System.out.println("handling validation exception");
        List<String> messages = new ArrayList<>();

        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                messages.add(fieldName + ": " + defaultMessage);
            }
        }
        ResponseAPI<?> apiResponse = new ResponseAPI<>();
        apiResponse.setCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage(String.join(";", messages));
        return ResponseEntity
                .badRequest()
                .body(apiResponse);
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ResponseAPI<?>> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> messages = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ResponseAPI<?> response = new ResponseAPI<>();
        response.setCode(ErrorCode.INVALID_KEY.getCode());
        response.setMessage(String.join("; ", messages));

        return ResponseEntity.badRequest().body(response);
    }
}
