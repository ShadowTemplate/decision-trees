package com.mapgroup.client.utility;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * This class provides a way to filter all the supported files
 * used by the program.
 */
public class ExtensionFilter extends FileFilter {
    /** The list of all supported file extension */
    private final ArrayList<String> supportedExt = new ArrayList<String>();

    /**
     * Constructs this object initializing the list of supported extensions
     * with the specified list.
     *
     * @param ext the list of supported extensions
     */
    public ExtensionFilter(ArrayList<String> ext)
    {
        supportedExt.addAll(ext);
    }

    /**
     * Checks if the current file is a directory or has as extension the
     * one of the valid extension contained in the list of supported extensions.
     *
     * @param f the file that will be checked
     * @return <code>true</code> if the file is a directory or has a valid extension, <code>false</code> otherwise
     */
    public boolean accept(File f) {

        return f.isDirectory() || checkExtension(f.getName());
    }

    /**
     * Checks if the specified filename has the correct extensions according
     * to the list of supported extension
     *
     * @param fileName the filename that will be checked
     * @return <code>true</code> if the filename is valid and it has a valid extension, <code>false</code> otherwise
     */
    private boolean checkExtension(String fileName) {
        if (!fileName.contains("."))
            return false;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        return supportedExt.indexOf(extension) != -1;

    }

    /**
     * Returns a description for this filename filter
     *
     * @return the filter description
     */
    public String getDescription() {
        return "Dataset format";
    }
}