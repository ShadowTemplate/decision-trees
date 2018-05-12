package com.mapgroup.to;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Models the possible response of the server during the communication
 * with the client.
 */
public class ServerResponse implements Serializable {
    /** list of information contained in the structure */
    private final List<Object> values;

    /**
     * Constructs this object using the information passed as
     * parameter.
     * @param args the information that will be added to the structure
     */
    public ServerResponse(Object... args){
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
     * Inserts the specified attribute to the structure.
     *
     * @param o the new attribute that will be added
     */
    public void addAttribute(Object o) {
        values.add(o);
    }
}
