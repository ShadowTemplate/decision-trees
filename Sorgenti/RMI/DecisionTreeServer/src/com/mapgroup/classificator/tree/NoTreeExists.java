package com.mapgroup.classificator.tree;

public class NoTreeExists extends Exception {
    private String msgError = "(Exception) Decision tree doesn't exists.";

    public NoTreeExists(){
        super();
    }

    public NoTreeExists(String msg){
        super(msg);
        this.msgError = msg;
    }

    public String getMessage(){
        return msgError;
    }
}
