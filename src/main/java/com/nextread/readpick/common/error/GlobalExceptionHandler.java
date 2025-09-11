package com.nextread.readpick.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return ApiError.of("VALIDATION_ERROR", "요청 형식이 올바르지 않습니다.", req.getRequestURI());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return ApiError.of("NOT_FOUND", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleEtc(Exception ex, HttpServletRequest req) {
        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) throw rse;
        return ApiError.of("INTERNAL_ERROR", "서버 오류", req.getRequestURI());
    }

}

