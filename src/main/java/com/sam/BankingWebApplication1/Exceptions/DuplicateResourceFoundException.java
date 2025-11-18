package com.sam.BankingWebApplication1.Exceptions;

public class DuplicateResourceFoundException extends RuntimeException{
    public DuplicateResourceFoundException(String message) {
        super(message);
    }
}
