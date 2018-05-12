package com.mapgroup.classificator.tree;

/**
 * This exception is launched when the tree is not exist
 */

public class NoTreeExists extends Exception {
    private String errMsg = "(Exception) Decision tree doesn't exists.";

    /**
     * Calls the superclass constructor
     */

    public NoTreeExists(){
        super();
    }

    /**
     *  Sets <code>errMsg</code> for the exception
     *
     * @param msg personal message
     */

    public NoTreeExists(String msg){
        super(msg);
        this.errMsg = msg;
    }

    /**
     * Returns the <code>errMsg</code> of the exception
     *
     * @return <code>String</code> which represents the <code>errMsg</code>
     */

    public String getMessage(){
        return errMsg;
    }
}
