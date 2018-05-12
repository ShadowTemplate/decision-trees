package com.mapgroup.classificator.utility;

import java.util.ArrayList;

/**
 * This class was used in order to manage
 * all the available and supported dataset format extension.
 * <p>
 *     Currently supported extension for the dataset are:
 *     <ul>
 *         <li>Excel files (.xls)</li>
 *         <li>Textual files (.txt)</li>
 *         <li> <a href=http://www.cs.waikato.ac.nz/ml/weka/arff.html>Attribute-Relation File Format</a> (.arff)</li>
 *     </ul>
 * </p>
 *
 * */
public class SupportedExtensions {
    /** a list of all the supported extensions */
    private final ArrayList<String> supportedExt = new ArrayList<String>();

    /**
     * Populates the list of supported extensions
     * with the ones available.
     * */
    public SupportedExtensions()
    {
        supportedExt.add("xls");
        supportedExt.add("txt");
        supportedExt.add("arff");
    }

    /**
     * Returns the list of supported extensions.
     * @return the supported extensions, <code>supportedExt</code>
     * */
    public ArrayList<String> getSupportedExtension() {
        return supportedExt;
    }
}
