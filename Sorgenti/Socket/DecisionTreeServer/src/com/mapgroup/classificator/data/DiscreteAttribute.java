package com.mapgroup.classificator.data;

/**
 * This class represents an attribute with only discrete values
 */

public class DiscreteAttribute extends Attribute {
    /**
     * Possible values for this attribute
     *
     */
    private final String[] values;

    /**
     * Initialize this attribute with input values
     *
     * @param name symbolic name for the attribute
     * @param index identification's value of the attribute
     * @param values possible values for this attribute
     */
    public DiscreteAttribute(String name, int index, String values[]) {
        // calls the superclass constructor
        super(name, index);
        this.values = values;
    }

    /**
     * Return the number of the possible values for this attribute
     *
     * @return number of the possible values for this attribute
     */
    public int getNumOfDistinctValues() {
        return values.length;
    }

    /**
     * Return the value which position is the input index
     *
     * @param i index which represents position
     * @return attribute's value
     */
    public String getValue(int i) {
        return values[i];
    }
}
