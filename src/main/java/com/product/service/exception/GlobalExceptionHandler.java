package com.product.service.exception;

import com.product.service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.info("Handled exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ResourceConflictException ex, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.info("Handled exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(DuplicateProductException ex, HttpServletRequest request) {
        var status = HttpStatus.CONFLICT;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.info("Handled exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException ex, HttpServletRequest request) {
        var status = HttpStatus.SERVICE_UNAVAILABLE;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.warn("Handled external service exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Solicitud inválida");
        var status = HttpStatus.BAD_REQUEST;
        var body = buildErrorResponse(status, msg, request);
        log.warn("Handled validation error status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        var msg = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Solicitud inválida");
        var status = HttpStatus.BAD_REQUEST;
        var body = buildErrorResponse(status, msg, request);
        log.warn("Handled constraint violation status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        var msg = "El cuerpo de la solicitud no tiene un formato válido";
        var status = HttpStatus.BAD_REQUEST;
        var body = buildErrorResponse(status, msg, request);
        log.warn("Handled bad request status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.warn("Handled bad request status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductId(InvalidProductIdException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var body = buildErrorResponse(status, ex.getMessage(), request);
        log.warn("Handled bad request status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        var msg = "Conflicto de datos al persistir el producto";
        var status = HttpStatus.CONFLICT;
        var body = buildErrorResponse(status, msg, request);
        log.error("Handled data integrity exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternal(Exception ex, HttpServletRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("Handled unexpected exception status={} method={} path={} message={}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        var body = buildErrorResponse(status, ex.getMessage(), request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }
}