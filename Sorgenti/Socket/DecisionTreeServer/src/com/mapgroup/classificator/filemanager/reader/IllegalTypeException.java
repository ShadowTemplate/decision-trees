package com.mapgroup.classificator.filemanager.reader;

/**
 * Represents an error occurred while reading the dataset due to unrecognized attribute types.
 *
 * */
class IllegalTypeException extends Exception {
    /** default error message for the exceptional state */
    private String defMessage = "Invalid attribute type found in dataset";

    /** Constructs this object calling the superclass' constructor with no arguments */
    public IllegalTypeException() {
        super();
    }

    /**
     * Constructs this object calling the superclass' constructor that has as argument
     * the specific error message that will be printed.
     * */
    public IllegalTypeException(String err) {
        super(err);
        this.defMessage = err;
    }

    /**
     * Returns the defined error message for this object.
     * @return specified error message or the default one if no error message is specified
     */
    public String getMessage() {
        return defMessage;

    }

}
