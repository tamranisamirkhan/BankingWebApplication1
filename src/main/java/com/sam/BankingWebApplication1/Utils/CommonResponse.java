package com.sam.BankingWebApplication1.Utils;

import org.springframework.http.HttpStatus;

public class CommonResponse {
    public static ResponseModel OK(Object data){
        return new ResponseModel(data,null, HttpStatus.OK);
    }
    public static ResponseModel BAD_REQUEST(Object error){
        return new ResponseModel(null,error, HttpStatus.BAD_REQUEST);
    }
    public static ResponseModel NOT_FOUND(Object error) {
        return new ResponseModel(null,error, HttpStatus.NOT_FOUND);
    }
    public static ResponseModel CREATED(Object data){
        return new ResponseModel(data,null,HttpStatus.CREATED);
    }
    public static ResponseModel CONFLICT(Object data){
        return new ResponseModel(data,null,HttpStatus.CONFLICT);
    }
}
