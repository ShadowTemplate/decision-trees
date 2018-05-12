package com.mapgroup.classificator.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides utility to connect with a PostgreSQL DBMS.
 */
final class PostgreSQLAccess extends DbAccessWithUser {
    /** Name of the driver */
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    /** URL part with the DBMS information */
    private static final String DBMS = "jdbc:postgresql";
    /** DBMS port */
    private static final String PORT = "5432";

    /**
     * Checks if the user already exists looking into the
     * pg_roles table, that contains information
     * on the user currently present in the database.
     * 
     * @return true if the user exists, false otherwise
     */
    protected boolean existUser() {
        Statement s = null;
        boolean flag = false;
        try {
            s = conn.createStatement();
            ResultSet set = null;
            try {
                //
                String query = "SELECT pg_roles.rolname FROM pg_catalog.pg_roles WHERE rolname=\'" + USER_ID.toLowerCase() + "\'";
                set = s.executeQuery(query);
                flag = set.next();


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
                System.err.println("(SQLException): Unable to close the statement.\n");
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
        String one = "create user " + USER_ID + " CREATEDB CREATEROLE PASSWORD '" + PASSWORD + "';",
                two = "GRANT ALL PRIVILEGES ON DATABASE " + DATABASE + " TO " + USER_ID + ";";

        s.executeUpdate(one);
        s.executeUpdate(two);
    }

    /**
     * Creates the database with the information in the
     * class' fields.
     * 
     * @param s Statement by which the query are executed
     * @throws SQLException if the creation fails
     */
    protected void createDatabase(Statement s)throws SQLException {
        s.executeUpdate("create database " + DATABASE);
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
     * Checks if the database already exists looking into the
     * pg_database table, that contains information
     * on the databases currently present.
     * 
     * @return true if the database exists, false otherwise
     */ 
    protected boolean existDatabase() {
        Statement s = null;
        boolean flag = true;
        try {
            s = conn.createStatement();

            ResultSet set = null;
            try {
                set = s.executeQuery("select pg_database.datname from pg_catalog.pg_database where datname=\'" + DATABASE + "\'");
                flag = set.next();
            } catch (SQLException e) {
                flag = false;
            } finally {
                if (set != null)
                    set.close();
            }

        } catch (SQLException e) {
            System.err.println("(SQLException): Unable to check if database exists.\n");
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (SQLException e) {
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
}
