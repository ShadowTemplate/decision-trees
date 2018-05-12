package com.mapgroup.classificator.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class models the structure of the information about the current context.
 * <p>
 * Defines the appropriate information needed in a specific situation in order to
 * grant the possibility to do the specific operation for the specific context.
 */
public class ContextRequest {
    /** list of values which belong to this structure*/
    private List<Object> values;

    /**
     * Constructs this context structure with the specified
     * values.
     *
     * @param args the values that will be added to the structure
     */
    public ContextRequest(Object... args){
        values = new LinkedList<Object>();
        values.addAll(Arrays.asList(args));
    }

    /**
     * Returns the specific data contained in the structure in the
     * specified position, defined by <code>index</code>
     *
     * @param index the attribute's position in the structure
     * @return specific attribute information
     */
    public Object getAttribute(int index) {
        return values.get(index);
    }

    /**
     * Replaces the attribute in the specified position, with the new value
     * specified.
     *
     * @param index attribute's position which will be modified
     * @param val the new value for the attribute
     */
    public void setAttributes(int index,Object val){
        values.set(index,val);
    }

    /**
     * Checks if the current structure is empty.
     *
     * @return <code>true</code> if the structure has no values in it, <code>false</code> otherwise
     */
    public boolean isEmpty(){
        return values.isEmpty();
    }

    /**
     * Inserts the specified attribute to the structure.
     *
     * @param o the new attribute that will be added
     */
    public void addAttribute(Object o) {
        values.add(o);
    }
}
