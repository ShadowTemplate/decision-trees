package com.mapgroup.classificator.filemanager.writer;

import com.mapgroup.classificator.filemanager.RequestStruct;

import java.io.IOException;

/**
 * Abstract representation for the utility classes whose aim
 * is to create a specific formatted representation of the dataset
 * currently processed.
 */

public interface IDataWriter {

    /**
     * Writes the dataset contained in the struct to a file whose name
     * is contained in the same structure.
     *
     * @param rs structure which contains the values to create dataset representation
     * @throws IOException - if some errors occurred processing the file
     */
    void write(RequestStruct rs) throws IOException;

}
