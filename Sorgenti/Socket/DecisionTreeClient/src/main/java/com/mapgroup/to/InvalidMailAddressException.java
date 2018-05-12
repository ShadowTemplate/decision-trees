package com.mapgroup.to;

/**
 * Exception thrown when an email address is invalid.
 * <p>
 * Further information can be found <a href=https://en.wikipedia.org/wiki/Email_address>here</a>.
 */
public class InvalidMailAddressException extends Exception{
    
    /**
     * Build this object calling the superclass constructor.
     */
    public InvalidMailAddressException(){
        super();
    }

    /**
     * Build this object calling the superclass constructor,
     * with a message attached.
     */
    public InvalidMailAddressException(String msg){
        super(msg);
    }

}
