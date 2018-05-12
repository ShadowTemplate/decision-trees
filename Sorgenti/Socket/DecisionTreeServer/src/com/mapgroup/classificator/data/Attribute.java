package com.mapgroup.classificator.data;

import java.io.Serializable;

/**
 * Abstract class which represents a generic (discrete or continue) attribute of the decision tree
 */
public abstract class Attribute implements Serializable {

    /**
     * Attribute's name
     */
    private final String name;

    /**
     * Identification's value which represents the position of attribute in the dataset
     */
    private final int index;

    /**
     *Initialize this attribute with input values
     *
     * @param name  Symbolic name for the attribute
     * @param index Identification's value of the attribute
     */
    Attribute(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Returns the symbolic name of attribute
     *
     * @return attribute's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the identification's value which represents the position of attribute in the dataset
     *
     * @return attribute's index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * An attribute will be represent in the format NAME_ATTRIBUTE
     *
     * @return string which represents the name of attribute
     */

    @Override
    public String toString() {

        return this.name;
    }
}
