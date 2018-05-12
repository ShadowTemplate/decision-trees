package com.mapgroup.classificator.database;

/**
 * Exception that is thrown when the dataset can not be
 * correctly built.
 */
public class DataException extends Exception{
    
    /** Message of the exception*/
    private String errMsg = "(DataException) Unable to create Dataset";
    
    /** 
     * Creates this object calling the superclass constructor.
     * */
    public DataException() {
        super();
    }
    
    /**
     * Creates this object calling the superclass constructor,
	 * with a message attached.
     * 
     * @param msg Message of the exception
     * */
    public DataException(String msg) {
        super(msg);
        this.errMsg = msg;
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
