package com.mapgroup.classificator.database.dialects;

import com.mapgroup.classificator.database.dao.DbAccess;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Provides an utility to choose the appropriate TableSchema 
 * for the database currently in use.
 * <p>
 * To choose the correct TableSchema the server will check the
 * database product name between DBMS metadata.
 * 
 * @see TableSchema
 */
public class TableSchemaFactory {

    /**
     * Chooses the correct TableSchema and returns it.
     * 
     * @param tableName Name of the dataset's table name in the DBMS.
     * @return the TableSchema appropriate for the DBMS
     * @throws SQLException if the current DBMS isn't supported
     */
    public static TableSchema createTableSchema(String tableName) throws SQLException{
        DatabaseMetaData databaseMetaData = DbAccess.getConnection().getMetaData();

        if (databaseMetaData.getDatabaseProductName().contains("MySQL")) {
            return new TableSchemaMySql(tableName);
        }
        if (databaseMetaData.getDatabaseProductName().contains("PostgreSQL")) {
            return new TableSchemaPG(tableName);
        }

        throw new SQLException("Not supported SQL dialect for the specified DBMS.\n");
    }
}
