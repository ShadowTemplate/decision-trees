package com.mapgroup.classificator.tree;

/**
 * This exception is launched when is not possible to generate a split value
 */

class NoSplitException extends Exception {

    /**
     * Message which the exception prints
     */
    private String errMsg = "(NoSplitException): No split value generated.";

    public NoSplitException(){
        super();
    }

    /**
     * Sets <code>errMsg</code> for the exception
     *
     * @param msg personal message
     */

    public NoSplitException(String msg){
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
