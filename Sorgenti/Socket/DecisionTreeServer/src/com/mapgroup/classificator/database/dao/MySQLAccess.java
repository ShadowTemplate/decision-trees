package com.mapgroup.classificator.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides utility to connect with a MySQL DBMS.
 */
final class MySQLAccess extends DbAccessWithUser {
    /** Name of the driver */
    private static final String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
    /** URL part with the DBMS information */
    private static final String DBMS = "jdbc:mysql";
    /** DBMS port */
    private static final String PORT = "3306";

    /**
     * Checks if the user already exists looking into the
     * information_schema table, that contains information
     * on the user currently present in the database.
     * 
     * @return true if the user exists, false otherwise
     */
    protected boolean existUser() {
        Statement s = null;
        boolean flag = false;
        try {
            s = conn.createStatement();
            s.execute("use information_schema");
            ResultSet set = null;
            try {
                String query = "select distinct grantee from user_privileges where grantee = \"'" + USER_ID + "'@'localhost'\"";
                set = s.executeQuery(query);
                flag = set.first();


            } catch (SQLException e) {
                System.err.println("(SQLException): Unable to check if user exists.\n");

            } finally {
                if (set != null) {
                    set.close();
                }
            }

        } catch (SQLException e) {
            System.err.println("(SQLException): " + e.getMessage());
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (SQLException ignored) {
                System.err.println("(SQLException): Unable to close statement.\n");
            }
        }

        return flag;
    }

    /**
     * Creates the user with the information in the
     * class' fields.
     * 
     * @param s Statement by which the query are executed
     * @throws SQLException if the creation fails
     */
    protected void createUser(Statement s) throws SQLException {
        String one = "create user " + USER_ID + "@localhost IDENTIFIED BY '" + PASSWORD + "';",
                two = "GRANT ALL ON " + DATABASE + ".* TO " + USER_ID + "@localhost";

        s.executeUpdate(one);
        s.executeUpdate(two);
    }

    /**
     * Returns the name of the driver necessary
     * to connect with the DBMS.
     * 
     * @return the name of the driver
     */    
    protected String getDBMSDriverClass() {
        return DRIVER_CLASS_NAME;
    }

    /**
     * Checks if the database already exists.
     * <p>
     * The method tries to use the database: if an exception
     * is received while connecting to it the database
     * is assumed as not present.
     * 
     * @return true if the database exists, false otherwise
     */    
    protected boolean existDatabase() {
        Statement s = null;
        boolean flag = true;
        try {
            s = conn.createStatement();

            try {
                s.execute("use " + DATABASE);

            } catch (SQLException e) {
                flag = false;
            }

        } catch (SQLException e) {
            System.err.println("(SQLException): Unable to check if database exists.\n");
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (SQLException ignored) {
                System.err.println("(SQLException): Unable to close statement.\n");
            }
        }

        return flag;
    }

    /**
     * Returns the URL connection needed to connect to the
     * DBMS as the username specified in the class fields.
     * 
     * @return the URL connection
     */
    protected String getMainConnectionString() {
        return DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE;
    }

    /**
     * Returns the URL connection needed to connect with
     * administrator privileges to the DBMS, in order 
     * to check the existence of user and database 
     * root specified in the class fields.
     * 
     * @return the URL connection
     */
    protected String getRootConnectionString() {
        return DBMS + "://" + SERVER + ":" + PORT;
    }

    /**
     * Creates the database with the information in the
     * class' fields.
     * 
     * @param s Statement by which the query are executed
     * @throws SQLException if the creation fails
     */
    protected void createDatabase(Statement s) throws SQLException {
        s.executeUpdate("create database " + DATABASE);
    }
}
