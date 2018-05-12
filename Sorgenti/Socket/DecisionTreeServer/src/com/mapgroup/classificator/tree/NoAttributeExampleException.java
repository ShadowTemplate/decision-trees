package com.mapgroup.classificator.tree;

class NoAttributeExampleException extends Exception {
    private String msgError = "(Exception) Decision tree doesn't exists.";

    public NoAttributeExampleException(){
        super();
    }

    public NoAttributeExampleException(String msg){
        super(msg);
        this.msgError = msg;
    }

    public String getMessage(){
        return msgError;
    }
}
