package com.mapgroup.classificator.database;

/**
 * Exception that is thrown when the log in to the DBMS fails.
 */
public class IncorrectLoginException extends Exception {
    
    /** Message of the exception*/
    private String errMsg = "(Login Exception): Wrong combination of username and password.";

    /** 
     * Creates this object calling the superclass constructor.
     * */
    public IncorrectLoginException(){
        super();
    }

    /**
     * Creates this object calling the superclass constructor,
	 * with a message attached.
     * 
     * @param errMsg Message of the exception
     * */
    public IncorrectLoginException(String errMsg){
        super(errMsg);
        this.errMsg = errMsg;
    }

    /**
     * Returns the error message of the exception.
     * 
     * @return the error message 
     */
    public String getMessage(){
        return errMsg;
    }
}
