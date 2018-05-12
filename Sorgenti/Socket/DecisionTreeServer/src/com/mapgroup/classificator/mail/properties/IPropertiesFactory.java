package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * An interface which provides a shared structure for the
 * creation of specific mail server Properties object.
 *
 * */
interface IPropertiesFactory {

    /**
     * Returns a specific Properties object with all the
     * server mail protocol information and inserts the
     * authentication data connected with the sender's mail address too.
     * @param user username for the sender mail address
     * @param pass password for the sender mail address
     * @return the properties needed to establish connection with the mail server
     */
    public Properties getProperties(String user, String pass);
   
}
