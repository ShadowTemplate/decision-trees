package com.mapgroup.classificator.filemanager.writer;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;
import com.mapgroup.classificator.filemanager.RequestStruct;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Represents a way in order to generate a dataset
 * representation using the text document with the .txt format.
 * <p/>
 * Each dataset will be saved in the text document in a single
 * sheet which will be divided in this way:
 * <ul>
 * <li> attributes' name </li>
 * <li> attributes' type </li>
 * <li> dataset's content </li>
 * </ul>
 */

class TxtWriter implements IDataWriter {

    /**
     * Writes the dataset, contained in <code>rs</code>, to file with txt extension, whose
     * name is contained in the specified struct.
     *
     * @param rs structure which contains the file path and the data set that will be contain in the file
     * @throws IOException - if some errors occurred processing the file
     */
    public void write(RequestStruct rs) throws IOException {

        BufferedWriter out = new BufferedWriter(new FileWriter((String) rs.getAttribute(0)));//gets file path
        Data dataSet = (Data) rs.getAttribute(1);//gets data set


        LinkedList<String> type = new LinkedList<String>();


        //Writes the attributes' name and at the end the class attribute's name
        for (int i = 0; i < dataSet.getNumberOfExplanatoryAttributes(); i++) {
            out.write(dataSet.getExplanatoryAttribute(i).toString() + " ");
        }
        out.write(dataSet.getClassAttribute().toString());


        for (int i = 0; i < dataSet.getNumberOfExplanatoryAttributes(); i++) {
            if (dataSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute)
                type.add("String");
            else
                type.add("Float");
        }

        //validity check of the attributes' type
        if (dataSet.getClassAttribute() instanceof DiscreteAttribute)
            type.add("String");
        else
            type.add("Float");

        out.newLine();

        // writes the attributes' type
        for (String aType : type) {
            out.write(aType + " ");
        }
        out.newLine();

        //writes the dataset's content
        for (int row = 0; row < dataSet.getNumberOfExamples(); row++) {
            for (int col = 0; col < dataSet.getNumberOfExplanatoryAttributes(); col++) {
                out.write(dataSet.getExplanatoryValue(row, col).toString() + ",");
            }
            out.write(dataSet.getClassValue(row));
            out.newLine();
        }
        out.close();
    }
}
