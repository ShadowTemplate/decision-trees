package com.mapgroup.classificator.database.dialects;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Provides utility to map PostgreSQL types to types that
 * can be assumed by attribute. 
 * <p>
 * Every original type is 
 * mapped to <i>string</i> or to <i>number</i>.
 */
class TableSchemaPG extends TableSchema {
    
    /**
     * Creates this object calling the superclass constructor.
     * 
     * @param tableName Name of the dataset's table in the database.
     * @throws SQLException if superclass' constructor does 
     */
    TableSchemaPG(String tableName) throws SQLException {
        super(tableName);
    }

    /**
     * Creates a mapping between PostgreSQL types and the attributes'
     * possible types.
     * 
     * @param mapSQL_JAVATypes HashMap containing information on mapping 
     */
    protected void createTypeMapping(HashMap<String, String> mapSQL_JAVATypes) {
        mapSQL_JAVATypes.put("varchar", "string");
        mapSQL_JAVATypes.put("character", "string");
        mapSQL_JAVATypes.put("char", "string");
        mapSQL_JAVATypes.put("character varying", "string");
        mapSQL_JAVATypes.put("text", "string");
        mapSQL_JAVATypes.put("numeric", "number");
        mapSQL_JAVATypes.put("integer", "number");
        mapSQL_JAVATypes.put("decimal", "number");
        mapSQL_JAVATypes.put("real", "number");
    }
}
