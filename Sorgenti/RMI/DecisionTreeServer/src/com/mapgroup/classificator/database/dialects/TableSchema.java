package com.mapgroup.classificator.database.dialects;

import com.mapgroup.classificator.database.dao.DbAccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class which models the database table's structure retrieving
 * important information from the DBMS and creating a mapping between the
 * programming language's type and the SQL types.
 */
public abstract class TableSchema {
    /**
     * List of all the columns of the table analyzed
     */
    private final List<Column> tableSchema = new ArrayList<Column>();

    /**
     * Defines the mapping between the SQL types and the JAVA types
     * according to the specific DBMS rules.
     *
     * @param mapSQL_JAVATypes data structure used to grant the mapping
     */
    protected abstract void createTypeMapping(
            HashMap<String, String> mapSQL_JAVATypes);

    /**
     * Constructs the structure of the specified database's table which as name the
     * specified value <code>tableName</code>.
     * <p>
     * The structure of the table is retrieved using the metadata content of the current
     * database.
     *
     * @param tableName the table that will be analyzed
     * @throws SQLException - if some errors occurred querying the database
     */
    TableSchema(String tableName) throws SQLException {
        HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();

        createTypeMapping(mapSQL_JAVATypes);

        Connection con = DbAccess.getConnection();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getColumns(null, null, tableName, null);

        while (res.next()) {
            if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
                tableSchema.add(new Column(res.getString("COLUMN_NAME"),
                        mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
        }
        res.close();
    }

    /**
     * Returns the list of all the types used in the current table.
     *
     * @return list of available types
     */
    public List<String> getAttributeTypes() {
        List<String> listTypes = new LinkedList<String>();
        // now got all available type.
        for (int i = 0; i < getNumberOfAttributes(); i++) {
            if (tableSchema.get(i).isNumber()) {
                listTypes.add("Float");
            } else {
                listTypes.add("String");
            }
        }

        return listTypes;
    }

    /**
     * Returns the columns' list of names present in this table.
     * <p>
     * Each column represent a specific attribute, so the column's name represents
     * the attribute's name.
     *
     * @return attribute's list of names
     */
    public List<String> getAttributeNames() {
        List<String> listNames = new LinkedList<String>();

        for (int i = 0; i < getNumberOfAttributes(); i++) {
            listNames.add(tableSchema.get(i).getColumnName());
        }

        return listNames;
    }

    /**
     * Returns the number of attributes present in this table.
     *
     * @return the number of attributes
     */
    public int getNumberOfAttributes() {
        return tableSchema.size();
    }

    /**
     * Retrieves specific information about the column in position
     * defined by the specified index.
     *
     * @param index position of the column selected
     * @return the column selected
     */
    public Column getColumn(int index) {
        return tableSchema.get(index);
    }

    /**
     * Class which defines the structure of each column with some information
     * about the type and the name of the attribute contained in it.
     */
    public class Column {
        /** the column's name */
        private final String name;
        /** the column's type */
        private final String type;

        /**
         * Constructs this object with the specified name and type passed
         * as parameters.
         *
         * @param name the column's name
         * @param type the column's type
         */
        private Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * Returns this column's name.
         * @return the column's name
         */
        public String getColumnName() {
            return name;
        }

        /**
         * Checks if this column represent a numeric value or not.
         *
         * @return <code>true</code> if the type is equal to <code>number</code>, otherwise <code>false</code>
         */
        public boolean isNumber() {
            return type.equals("number");
        }

        /**
         * Returns the string representation of this column, displaying
         * its name and type.
         *
         * @return column's string representation
         */
        public String toString() {
            return name + ":" + type;
        }
    }

}
