package com.mapgroup.classificator.filemanager.reader;

import com.mapgroup.classificator.filemanager.RequestStruct;

/**
 * This class grant the possibility to generate a specific
 * way to read the correct dataset formatted file based upon the
 * type of file used.
 */
public class DataReaderFactory {

    private DataReaderFactory() {
    }

    /**
     * Supported dataset file extensions
     */
    private enum ReaderType {
        /**
         * Attribute-Relation File Format
         */
        ARFF,
        /**
         * Database table
         */
        DB,
        /**
         * Excel file
         */
        XLS,
        /**
         * Textual file
         */
        TXT
    }

    /**
     * Retrieves from the specified filename string, <code>fileName</code>
     * its extension.
     *
     * @return specific file extension
     */
    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());

    }

    /**
     * Instantiates the specific DataReader for the specific
     * dataset formatted file considered.
     *
     * @param struct current context information
     * @return  specific DataReader instance for the specified dataset file, or <code>null</code> if unsupported file
     * format is specified
     */
    public static DataReader getDataReader(RequestStruct struct) {

        ReaderType type = ReaderType.valueOf(getFileExtension(
                (String) struct.getAttribute(0)).toUpperCase());

        switch (type) {
            case ARFF:
                return new ArffReader();
            case DB:
                return new dbReader();
            case XLS:
                return new XlsReader();
            case TXT:
                return new TxtReader();
            default:
                return null;
        }
    }
}
