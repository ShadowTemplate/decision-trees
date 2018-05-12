package com.mapgroup.classificator.database.dialects;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Provides utility to map MySQL types to types that
 * can be assumed by attribute. 
 * <p>
 * Every original type is 
 * mapped to <i>string</i> or to <i>number</i>.
 */
class TableSchemaMySql extends TableSchema {
    
    /**
     * Creates this object calling the superclass constructor.
     * 
     * @param tableName Name of the dataset's table in the database.
     * @throws SQLException if superclass' constructor does 
     */
    public TableSchemaMySql(String tableName) throws SQLException {
        super(tableName);
    }

    /**
     * Creates a mapping between MySQL types and the attributes'
     * possible types.
     * 
     * @param mapSQL_JAVATypes HashMap containing information on mapping 
     */
    protected void createTypeMapping(HashMap<String, String> mapSQL_JAVATypes) {
        mapSQL_JAVATypes.put("CHAR", "string");
        mapSQL_JAVATypes.put("VARCHAR", "string");
        mapSQL_JAVATypes.put("LONGVARCHAR", "string");
        mapSQL_JAVATypes.put("BIT", "string");
        mapSQL_JAVATypes.put("SHORT", "number");
        mapSQL_JAVATypes.put("INT", "number");
        mapSQL_JAVATypes.put("LONG", "number");
        mapSQL_JAVATypes.put("FLOAT", "number");
        mapSQL_JAVATypes.put("DOUBLE", "number");

    }
}
