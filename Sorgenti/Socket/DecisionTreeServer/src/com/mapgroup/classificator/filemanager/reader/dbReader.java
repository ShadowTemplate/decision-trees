package com.mapgroup.classificator.filemanager.reader;

import java.util.List;

import com.mapgroup.classificator.database.TableData;
import com.mapgroup.classificator.database.dialects.TableSchema;
import com.mapgroup.classificator.database.dialects.TableSchemaFactory;
import com.mapgroup.classificator.filemanager.RequestStruct;

/**
 *  This class provide a way to populate the dataset
 *  from a table contained in a specific database.
 *  <p>
 *  The databases could be stored and managed by one of the
 *  supported DBMS.
 *  </p>
 */
class dbReader extends DataReader {
    /**
     * Reads from the specified database table whose name was contained
     * in the request struct passed to the method all the information
     * about the attributes type and name.
     *
     * @param rs specific information about the current request
     * @throws IllegalTypeException - if an invalid type is read
     * @throws Exception - if some errors occurs in reading the specified table
     */
    public void initPopulator(RequestStruct rs) throws Exception {
        String fullTableName = (String) rs.getAttribute(0);
        String tableName = fullTableName.substring(0,
                fullTableName.lastIndexOf('.'));
        TableSchema ts = TableSchemaFactory.createTableSchema(tableName);

        List<String> attributes = ts.getAttributeTypes();

        for (String t : attributes) {
            boolean errorFlag = true;
            for (String legalType : legalTypes) {
                if (legalType.equals(t))
                    errorFlag = false;
            }
            if (errorFlag)
                throw new IllegalTypeException();
        }

        typeList.addAll(attributes);
        attributeNames.addAll(ts.getAttributeNames());

    }

    /**
     * Creates the dataset structured format from the specific
     * database table.
     *
     * @param rs specific information about the current request
     * @return structured dataset information
     * @throws Exception - if some errors occurred while reading from the database
     */
    public Object[][] tupleFactory(RequestStruct rs) throws Exception {

        String fullTableName = (String) rs.getAttribute(0);
        String tableName = fullTableName.substring(0,
                fullTableName.lastIndexOf('.'));

        Object[][] data;
        List<TableData.TupleData> tuples = new TableData()
                .getTransactions(tableName);
        int numColumns = tuples.get(0).tuple.size();
        data = new Object[tuples.size()][numColumns];

        for (int i = 0; i < tuples.size(); i++) {
            for (int j = 0; j < numColumns; j++) {
                data[i][j] = tuples.get(i).tuple.get(j);
            }
        }

        return data;
    }

}
