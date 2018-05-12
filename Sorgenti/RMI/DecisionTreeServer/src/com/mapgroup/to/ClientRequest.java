package com.mapgroup.to;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class containing all the information about a request
 * performed by a client that will be satisfied by a proper
 * executor.
 */
public class ClientRequest implements Serializable{
    
    /** List of values of the request */
    private final List<Object> values;

    /**
     * Construct this object adding some values
     * to the list field.
     * 
     * @param args Values to be added
     */
    public ClientRequest(Object... args){
        values = new LinkedList<Object>();
        values.addAll(Arrays.asList(args));
    }

    /**
     * Returns the value in the list in a specific position.
     * 
     * @param index Position of the value
     * @return the value
     */
    public Object getAttribute(int index) {
        return values.get(index);
    }

    /**
     * Add a value to the list field of the object.
     * 
     * @param o The value to be added
     */
    public void addAttribute(Object o) {
        values.add(o);
    }
}
