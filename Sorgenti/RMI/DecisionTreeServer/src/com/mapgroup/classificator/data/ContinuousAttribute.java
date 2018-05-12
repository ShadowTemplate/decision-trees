package com.mapgroup.classificator.data;

/**
 * This class represents an attribute with only continues values
 *
 */

public class ContinuousAttribute extends Attribute {
    /**
     * @param name  Symbolic name for the attribute
     * @param index Identification's value of the attribute
     */
    public ContinuousAttribute(String name, int index) {

        // calls the superclass constructor
        super(name, index);
    }
}
