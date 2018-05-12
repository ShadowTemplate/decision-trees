package com.mapgroup.classificator.filemanager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent the current context in which client requests is done
 */

public class RequestStruct {

    /**
     * List of values that represents the current context
     */

    private final List<Object> values;

    /**
     * Sets <code>values</code> with current context
     *
     * @param args objects which represents the context
     */

    public RequestStruct(Object... args) {
        values = new LinkedList<Object>();
        values.addAll(Arrays.asList(args));
    }

    /**
     * Return the values indexed by <code>index</code>
     *
     * @param index values thant will be returns
     * @return values indexed by <code>index</code>
     */

    public Object getAttribute(int index) {
        return values.get(index);
    }

    /**
     * Adds values to the list
     *
     * @param o values that will be adds in list of values
     */

    public void addAttribute(Object o) {
        values.add(o);
    }
}
