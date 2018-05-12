package com.mapgroup.classificator.database.dao;

import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.database.IncorrectLoginException;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides utility to handle a connection with a
 * generic DBMS that supports the creation of users.
 */
abstract class DbAccessWithUser extends DbAccess {

    /**
     * Checks if the user already exists.
     * 
     * @return true if the user exists, false otherwise
     */
    protected abstract boolean existUser();

    /**
     * Creates the user with the information in the
     * class' fields.
     * 
     * @param s Statement by which the query are executed
     * @throws SQLException if the creation fails
     */
    protected abstract void createUser(Statement s) throws SQLException;

    /**
     * Loads the driver of the DBMS, initializes the database 
     * (creating it if it does not exists) and initializing
     * the user (creating it if it does not exists).
     * 
     * @throws DataException if database can not be correctly created
     * @throws IncorrectLoginException if login to the DBMS fails
     */
    protected void initDatabase() throws DataException, IncorrectLoginException {

        try {
            Class.forName(getDBMSDriverClass());

            try {
                conn = DriverManager.getConnection(getRootConnectionString(), currentUser, currentPass);
            } catch (SQLException e) {
                  throw new IncorrectLoginException();
            }

            Statement s = conn.createStatement();

            if (!existDatabase()) {
                createDatabase(s);
            }

            if (!existUser()) {
                createUser(s);
            }


        } catch (ClassNotFoundException e) {
            throw new DataException("JDBC driver '" + getDBMSDriverClass() + "' not found.\nLoad it in your classpath\n");
        } catch (SQLException e) {
            throw new DataException("(Database error): " + e.getMessage() + " from " + e.getCause() + ":" + e.getSQLState());
        } finally {

            try {
                if( conn != null )
                    conn.close();
            } catch (SQLException e) {
                System.err.println("(SQLException): " + e.getMessage());
            }

            conn = null;
        }
    }
}
