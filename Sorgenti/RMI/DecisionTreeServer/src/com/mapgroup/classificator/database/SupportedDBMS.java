package com.mapgroup.classificator.database;

/**
 * Type that represents the supported DBMS for the server. 
 */
public enum SupportedDBMS {
    /** <a href="http://www.mysql.com/">MySQL</a> DBMS */
    MYSQL, 
    
    /** <a href="http://www.postgresql.org/">PostgreSQL</a> DBMS*/
    POSTGRE;
    
    /**
     * Returns the list of the currently supported DBMS.
     * 
     * @return An array with the names of the supported DBMS
     */
    public static String[] getSupportedDBMSName() {
	
	SupportedDBMS[] suppEnum = SupportedDBMS.values();
	int totNumSupported = suppEnum.length;
	String[] supported = new String[totNumSupported];

	for (int i = 0; i < totNumSupported; i++)
	    supported[i] = suppEnum[i].name();

	return supported;
    }
}
