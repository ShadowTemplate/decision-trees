package com.mapgroup.classificator.filemanager.writer;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;
import com.mapgroup.classificator.filemanager.RequestStruct;

import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This class represents the utility class used in order to
 * creates a sql script used in order to create a database's table
 * whose content is represented by the specified dataset.
 */

class DbWriter implements IDataWriter {

    /**
     * Creates the sql script, whose name is contained in the specified struct,
     * that could be used in order to create the database's table whose content
     * is equal to the dataset contained in the specified struct.
     *
     * @param rs structure which contains the file path, the data set that will be contained in the file and the table name
     * @throws IOException - if there are errors doing file operations
     */

    public void write(RequestStruct rs) throws IOException {

        // gets the complete script filename
        String completeFileName = ((String) rs.getAttribute(0));
        BufferedWriter out = new BufferedWriter(
                new FileWriter(completeFileName));
        // gets the dataset representation
        Data d = (Data) rs.getAttribute(1);
        LinkedList<Attribute> attributes = new LinkedList<Attribute>();

        for (int i = 0; i < d.getNumberOfExplanatoryAttributes(); i++) {
            attributes.add(d.getExplanatoryAttribute(i));

        }

        // Generates the command in order to create the specified table
        String tableName = (String) rs.getAttribute(2); //gets table's name
        String createTable = "CREATE TABLE " + tableName + "( ";

        // adds all the attributes' types
        for (Attribute a : attributes) {
            createTable += attributeToSql(a) + ", ";
        }

        Attribute classAttr = d.getClassAttribute();

        createTable += attributeToSql(classAttr) + " );";

        // Insert values for each attribute
        String insertFormat = "INSERT INTO " + tableName + " VALUES( ";

        out.write(createTable);
        out.newLine();

        for (int i = 0; i < d.getNumberOfExamples(); i++) {
            for (int j = 0; j < d.getNumberOfExplanatoryAttributes(); j++)
                insertFormat += "'" + d.getExplanatoryValue(i, j) + "', ";
            insertFormat += "'" + d.getClassValue(i) + "');";
            out.write(insertFormat);
            out.newLine();
            insertFormat = "INSERT INTO " + tableName + " VALUES( ";
        }
        out.close();
    }

    /**
     * Converts the string representation of the attribute's name in the
     * specific SQL string representation.
     *
     * @param attr attribute that will be represented in SQL format
     * @return SQL string representation for the specified attribute
     */

    private static String attributeToSql(Attribute attr) {
        String name = attr.getName();

        return (attr instanceof DiscreteAttribute) ? name + " VARCHAR(100)"
                : name + " DECIMAL(10,2)";
    }
}