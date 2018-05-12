package com.mapgroup.classificator.database.dao;

import com.mapgroup.classificator.database.SupportedDBMS;

/**
 * Provides an utility to choose the appropriate DbAccess 
 * class for the database currently in use.
 * 
 * @see DbAccess
 */
public class DbAccessFactory {

    /**
     * Returns the correct DbAccess class.
     * 
     * @param dbmsName Name of the DBMS
     * @return the DbAccess class
     */
    public static DbAccess createDbAccess(String dbmsName) {
	SupportedDBMS dbmsType = SupportedDBMS.valueOf(dbmsName.toUpperCase());
	switch (dbmsType) {
	case MYSQL:
	    return new MySQLAccess();
	case POSTGRE:
	    return new PostgreSQLAccess();
	default:
	    return null;
	}
    }
}
