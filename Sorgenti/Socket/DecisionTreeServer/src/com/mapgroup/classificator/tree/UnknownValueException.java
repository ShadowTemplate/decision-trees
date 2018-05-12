package com.mapgroup.classificator.tree;

/**
 * This exception is thrown when it is acquired or
 * a missing value or a value is outside the range of the attribute
 * for a new example to be classified
 */

public class UnknownValueException extends Exception {
    
    /** The exception message*/
    private String errMsg = "(UnknownValueException): An invalid value was used for this attribute.";

    /**
     * Construct this object calling the superclass constructor.
     */

    public UnknownValueException() {
        super();
    }

    /**
     * Construct this object calling the superclass constructor,
     * with a message attached. 
     *
     * @param msg The exception message
     */

    public UnknownValueException(String msg) {
        super(msg);
        this.errMsg = msg;
    }

    public String getMessage() {
        return errMsg;
    }
}
