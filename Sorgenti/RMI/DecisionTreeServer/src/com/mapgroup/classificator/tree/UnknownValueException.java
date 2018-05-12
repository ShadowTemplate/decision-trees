package com.mapgroup.classificator.tree;

public class UnknownValueException extends Exception{
    private String errMsg = "(UnknownValueException): An invalid value was used for this attribute.";

    public UnknownValueException(){
        super();
    }

    public UnknownValueException(String msg){
        super(msg);
        this.errMsg = msg;
    }

    public String getMessage(){
        return errMsg;
    }
}
