package com.sam.BankingWebApplication1.Exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
