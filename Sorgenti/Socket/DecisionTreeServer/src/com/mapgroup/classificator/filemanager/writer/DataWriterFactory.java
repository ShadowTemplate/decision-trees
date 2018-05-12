package com.mapgroup.classificator.filemanager.writer;

/**
 * Models, according to the factory design patter, a class whose aim is
 * to generate specific instances of class used in order to write
 * dataset to specific file.
 */

public class DataWriterFactory {

    /**
     * Creates the data writer for a file with extension <code>extension</code>
     *
     * @param extension represents the extension of the file
     * @return the data writer for the specified extension
     */

    public static IDataWriter createDataWriter(String extension) {
        if (extension.equals("xls")) {
            return new XlsWriter();
        }

        if (extension.equals("txt")) {
            return new TxtWriter();
        }

        if (extension.equals("arff")) {
            return new ArffWriter();
        }

        // extension.equals("script")
        return new DbWriter();
    }

}
