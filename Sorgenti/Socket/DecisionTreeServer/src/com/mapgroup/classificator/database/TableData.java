package com.mapgroup.classificator.database;

import com.mapgroup.classificator.database.dialects.TableSchema;
//import com.mapgroup.classificator.database.dialects.TableSchema.Column;
import com.mapgroup.classificator.database.dao.DbAccess;
import com.mapgroup.classificator.database.dialects.TableSchemaFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class models the way in order to retrieve the data from the
 * database's tables. It grants the possibility to get all the tuples
 * needed from the table.
 */
public class TableData {
    /**
     * Models a single tuple of the database's table analyzed.
     */
    public class TupleData {
        /** content of the represented tuple */
        public final List<Object> tuple = new ArrayList<Object>();

        /**
         * Returns the string representation of this tuple, collecting
         * in it, all the information about its content.
         *
         * @return tuple string representation
         */
        public String toString() {
            String value = "";
            for (Object aTuple : tuple)
                value += (aTuple.toString() + " ");

            return value;
        }
    }

    /**
     * Constructs this table data object
     */
    public TableData() {

    }

    /**
     * Returns the table's content of the table which has as name the specified
     * name.
     *
     * @param table the table name
     * @return the table's content
     * @throws SQLException - if some errors occurred during the querying process.
     */
    public List<TupleData> getTransactions(String table) throws SQLException {
        LinkedList<TupleData> transSet = new LinkedList<TupleData>();
        Connection c = DbAccess.getConnection();
        Statement t = c.createStatement();
        ResultSet currSet;
        currSet = t.executeQuery("SELECT * FROM " + table);

        TableSchema ts = TableSchemaFactory.createTableSchema(table);

        while (currSet.next()) {
            TupleData row = new TupleData();
            for (int i = 0; i < ts.getNumberOfAttributes(); i++) {
                if (ts.getColumn(i).isNumber())
                    row.tuple.add(currSet.getFloat(i + 1));
                else
                    row.tuple.add(currSet.getString(i + 1));
            }
            transSet.add(row);
        }

        currSet.close();
        t.close();

        return transSet;

    }

    /*public  List<Object> getColumnValues(String table,Column column, QUERY_TYPE modality) throws SQLException{
		LinkedList<Object> valueSet = new LinkedList<Object>();

        Connection c = DbAccess.getConnection();
        Statement t = c.createStatement();
        String queryStr;

        queryStr = ((modality.equals(QUERY_TYPE.DISTINCT)) ? ("SELECT DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName()) : ("SELECT DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName()));

        ResultSet currSet = t.executeQuery(queryStr);

        TableSchema ts = TableSchemaFactory.createTableSchema(table);

        while(currSet.next())
        {
            TupleData row = new TupleData();
            for(int i = 0; i < ts.getNumberOfAttributes(); i++)
            {
                if(ts.getColumn(i).isNumber())
                    row.tuple.add(currSet.getFloat(i + 1));
                else
                    row.tuple.add(currSet.getString(i + 1));
            }
            valueSet.add(row);
        }

        currSet.close();
        t.close();
        c.close();

		return valueSet;

	}*/
}
