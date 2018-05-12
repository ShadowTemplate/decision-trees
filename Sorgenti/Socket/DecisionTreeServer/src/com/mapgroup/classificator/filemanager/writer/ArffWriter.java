package com.mapgroup.classificator.filemanager.writer;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;
import com.mapgroup.classificator.filemanager.RequestStruct;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents a way in order to generate a dataset
 * representation using the Attribute-Relation File Format.
 *
  */
class ArffWriter implements IDataWriter {

    /**
     * Writes the data set, contained in <code>rs</code> structure, to file with arff extension
     * whose name is contained in the specified structure.
     *
     * @param rs structure which contains the file path and the data set that will be saved to the file
     * @throws IOException - if some errors occurred processing the file
     */

    public void write(RequestStruct rs) throws IOException {
        // gets the complete file path
        BufferedWriter out = new BufferedWriter(new FileWriter(
                (String) rs.getAttribute(0)));
        // gets the dataset's content
        Data dataSet = (Data) rs.getAttribute(1);
        
        // writes the relation tag
        out.write("@relation " + rs.getAttribute(2));
        out.newLine();
        out.newLine();

        // Write the attributes' information
        for (int i = 0; i < dataSet.getNumberOfExplanatoryAttributes(); i++) {
            String temp = "@attribute "
                    + dataSet.getExplanatoryAttribute(i).toString() + " ";
            if (dataSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute) {
                temp += "{";
                DiscreteAttribute attributeTemp = (DiscreteAttribute) dataSet
                        .getExplanatoryAttribute(i);
                for (int j = 0; j < attributeTemp.getNumOfDistinctValues() - 1; j++) {
                    temp += attributeTemp.getValue(j) + ", ";
                }
                temp += attributeTemp.getValue(attributeTemp
                        .getNumOfDistinctValues() - 1);
                temp += "}";
            } else
                temp += "REAL";
            out.write(temp);
            out.newLine();
        }
        // Writes class attribute
        String temp = "@attribute " + dataSet.getClassAttribute().toString()
                + " {";
        for (int j = 0; j < dataSet.getClassAttribute()
                .getNumOfDistinctValues() - 1; j++) {
            temp += dataSet.getClassAttribute().getValue(j) + ", ";
        }

        temp += dataSet.getClassAttribute().getValue(
                dataSet.getClassAttribute().getNumOfDistinctValues() - 1);
        out.write(temp + "}");

        out.newLine();
        out.newLine();

        // Writes the dataset's content
        out.write("@data");
        out.newLine();

        for (int i = 0; i < dataSet.getNumberOfExamples(); i++) {
            for (int j = 0; j < dataSet.getNumberOfExplanatoryAttributes(); j++) {
                out.write(dataSet.getExplanatoryValue(i, j).toString() + ",");
            }
            out.write(dataSet.getClassValue(i));
            out.newLine();
        }
        out.close();
    }
}
