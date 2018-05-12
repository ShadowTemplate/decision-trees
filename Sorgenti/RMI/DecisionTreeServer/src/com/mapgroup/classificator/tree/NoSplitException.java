package com.mapgroup.classificator.tree;

public class NoSplitException extends Exception {
    private String errMsg = "(NoSplitException): No split value generated.";

    public NoSplitException(){
        super();
    }

    public NoSplitException(String msg){
        super(msg);
        this.errMsg = msg;
    }

    public String getMessage(){
        return errMsg;
    }

}
