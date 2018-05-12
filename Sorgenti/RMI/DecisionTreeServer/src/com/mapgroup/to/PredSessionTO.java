package com.mapgroup.to;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class transmit information from the client to the server about the last
 * prediction happened. It is necessary to generate PDF with the report of the
 * prediction.
 */
public class PredSessionTO implements Serializable {

    /** The tree on which the prediction has been done */
    private String tree = "";
    /** A tracker of the choices done for each attribute (key: name of the attribute; value: choice) */
    private final HashMap<String, String> tracker;
    /** Final result of the prediction */
    private String predictedValue;

    /**
     * Build this object creating a new empty HashMap.
     */
    public PredSessionTO() {
	tracker = new HashMap<String, String>();
    }

    /**
     * Returns information on the tree on which the prediction
     * has been done.
     *  
     * @return the tree
     */
    public String getTree() {
	return tree;
    }

    /**
     * Sets the value of the tree on which the prediction is going
     * to start.
     * 
     * @param tree The new tree
     */
    public void setTree(String tree) {
	this.tree = tree;
    }

    /** Adds an entry into the HashMap with the choice done
     * for an attribute.
     * 
     * @param key The attribute
     * @param value The choice
     * */
    public void addEntry(String key, String value) {
	tracker.put(key, value);
    }

    /**
     * Deletes all the entries from the HashMap.
     */
    public void clearTracker() {
	tracker.clear();
    }

    /**
     * Initializes all the fields of the tracker. 
     * */
    public void resetSession() {
	tree = "";
	predictedValue = "";
	clearTracker();
    }

    /**
     * Returns the HashMap with all the choices done for
     * each attribute during the prediction.
     * 
     * @return the HashMap with all the possible values.
     */
    public HashMap<String, String> getTracker() {
	return tracker;
    }

    /**
     * Returns the value predicted at the end of the
     * prediction.
     * 
     * @return the value predicted
     */
    public String getPredictedValue() {
	return predictedValue;
    }

    /**
     * Sets the predicted value at the end of the prediction.
     * 
     * @param val The value predicted
     */
    public void setPredictedValue(String val) {
	predictedValue = val;
    }

    /**
     * Returns a representation of the tracker
     * by its HashMap.
     * 
     * @return the representation of the HashMap
     */
    public String toString() {
	return tracker.toString();
    }
}
