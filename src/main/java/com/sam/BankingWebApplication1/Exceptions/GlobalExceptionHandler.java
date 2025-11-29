package com.sam.BankingWebApplication1.Exceptions;


import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseModel resourceNotFoundExceptionHandler(ResourceNotFoundException e){
        String message = e.getMessage();
        return CommonResponse.NOT_FOUND(message);
    }

    @ExceptionHandler(DuplicateResourceFoundException.class)
    public ResponseModel duplicateResourceFoundExceptionHandler(DuplicateResourceFoundException e){
        String message = e.getMessage();
        return CommonResponse.CONFLICT(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseModel handleMethodArgsNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> resp = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            resp.put(fieldName,message);
        });
        return CommonResponse.BAD_REQUEST(resp);
    }
    @ExceptionHandler(IOException.class)
    public ResponseModel handleIOException(IOException ex){
        String message = ex.getMessage();
        return CommonResponse.BAD_REQUEST(message);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseModel handleTokenExpiredException(TokenExpiredException ex){
        String message = ex.getMessage();
        return CommonResponse.BAD_REQUEST(message);
    }
}

