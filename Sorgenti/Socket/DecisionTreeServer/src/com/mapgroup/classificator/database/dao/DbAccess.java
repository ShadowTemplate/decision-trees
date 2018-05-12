package com.mapgroup.classificator.database.dao;

import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.database.IncorrectLoginException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides all the services needed to connect and disconnect
 * the server from a generic DBMS.
 */
public abstract class DbAccess {

    /** Connection with the DBMS */
    protected static Connection conn;
    /** Name of the user currently in use */
    protected static transient String currentUser;
    /** Password associated to the user currently in use */
    protected static transient String currentPass;
    /** Username of the account chosen for connection */
    static transient final String USER_ID = "decisiontreeid";
    /** Password of the account chosen for connection */
    static transient final String PASSWORD = "dectreepassword";
    /** Server to which the connection will be established */
    static final String SERVER = "localhost";
    /** Name of the database to which the connection will be established */
    static final String DATABASE = "classificationdata";

    /**
     * Returns the name of the driver necessary
     * to connect with the DBMS.
     * 
     * @return the name of the driver
     */
    protected abstract String getDBMSDriverClass();
    
    /**
     * Checks if the database already exists.
     * 
     * @return true if the database exists, false otherwise
     */
    protected abstract boolean existDatabase();

    /**
     * Returns the URL connection needed to connect to the
     * DBMS as the username specified in the class fields.
     * 
     * @return the URL connection
     */
    protected abstract String getMainConnectionString();

    /**
     * Returns the URL connection needed to connect with
     * administrator privileges to the DBMS, in order 
     * to check the existence of user and database 
     * root specified in the class fields.
     * 
     * @return the URL connection
     */
    protected abstract String getRootConnectionString();

    /**
     * Creates the database with the information in the
     * class' fields.
     * 
     * @param s Statement by which the query are executed
     * @throws SQLException if the creation fails
     */
    protected abstract void createDatabase(Statement s) throws SQLException;

    /**
     * Loads the driver of the DBMS and initializes the database, 
     * creating it if it does not exists.
     * 
     * @throws DataException if database can not be correctly created
     * @throws IncorrectLoginException if login to the DBMS fails
     */
    void initDatabase() throws DataException, IncorrectLoginException {
        Statement s = null;
        try {

            Class.forName(getDBMSDriverClass());

            try {
                conn = DriverManager.getConnection(getRootConnectionString(), currentUser, currentPass);
            } catch (SQLException e) {
                throw new IncorrectLoginException();
            }

            s = conn.createStatement();

            if (!existDatabase()) {
                createDatabase(s);
            }

        } catch (ClassNotFoundException e) {
            throw new DataException("(Exception): JDBC driver '" + getDBMSDriverClass() + "' not found.\nLoad it in your classpath\n");
        } catch (SQLException e) {
            throw new DataException("(Database error): " + e.getMessage() + " from " + e.getCause() + ":" + e.getSQLState());

        } finally {

            try {
                if( s != null )
                    s.close();
                if (conn != null)
                    conn.close();

            } catch (SQLException e) {
                System.err.println("(SQLException): " + e.getMessage());
            }

            conn = null;
        }
    }

    /**
     * Initialize the connection with the DBMS, logging as an user.
     * 
     * @param user Name of the user
     * @param pass Password of the user
     * @throws DataException if some errors occur
     * @throws IncorrectLoginException if login fails
     */
    public final void initConnection(String user, String pass) throws DataException, IncorrectLoginException {
        try {
            // initialize the static field.
            currentUser = user;
            currentPass = pass;

            initDatabase();

            conn = DriverManager.getConnection(getMainConnectionString(), USER_ID, PASSWORD);

        } catch (SQLException e) {
            throw new DataException("(Database error): " + e.getMessage() + ":" + e.getSQLState());
        }
    }

    /**
     * Returns an object that represents the connection with the DBMS.
     * @return the object
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Closes the connection with the DBMS.
     */
    public static void closeConnection() {

        try {
            if (conn != null)
                conn.close();
            // we log-out
            currentUser = null;
            currentPass = null;

        } catch (SQLException e) {
            System.err.println("(Database error): Unable to close connection.\n");
        }

    }
}
