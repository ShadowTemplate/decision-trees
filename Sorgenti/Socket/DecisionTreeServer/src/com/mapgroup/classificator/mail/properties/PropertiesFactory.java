package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * A factory class which grant the possibility to
 * construct specific Properties object for the specific
 * mail address.
 */
public class PropertiesFactory {
    /**
     * Supported mail server by this factory class
     */
    private enum MailServer {
        /** Google gmailserver identifier */
        GMAIL,
        /** Yahoo server identifier */
        YAHOO,
        /** Hotmail server identifier */
        HOTMAIL,
        /** Alice server identifier */
        ALICE,
        /** Outlook server identifier */
        OUTLOOK,
        /** Identifier for another Outlook's mail domain */
        LIVE
    }

    /**
     * Returns the Properties object constructed specifically for the
     * mail server specified(<code>serverName</code>) with authentication
     * data provided for the sender's mail address.
     * @param serverName mail server of the sender's mail address
     * @param user username for the sender's mail address
     * @param pass password for the sender's mail address
     * @return Properties object for the mail server, or <code>null</code> if the mail server wasn't supported
     */
    public static Properties getProperties(String serverName, String user, String pass) {
        MailServer serverMail = MailServer.valueOf(serverName.toUpperCase());
        if (serverMail != null) {
            // create the correct properties structure for the specific MailServer

            switch (serverMail) {
                case GMAIL:
                    return new GmailPropertiesFactory().getProperties(user, pass);
                case YAHOO:
                    return new YahooPropertiesFactory().getProperties(user, pass);
                case HOTMAIL:
                case OUTLOOK:
                case LIVE:
                    return new HotmailPropertiesFactory().getProperties(user, pass);
                case ALICE:
                	return new AlicePropertiesFactory().getProperties(user, pass);
            }
        }

        return null;
    }

    /**
     * Returns the specific communication protocol for the specific
     * mail server identified by <code>serverName</code>.
     *
     * @param serverName mail server name
     * @return the communication protocol for the supported mail server, <code>null</code> if the mail server was not supported
     */
    public static String getProtocol(String serverName) {
        MailServer serverMail = MailServer.valueOf(serverName.toUpperCase());
        if (serverMail != null) {
            // create the correct properties structure for the specific MailServer

            switch (serverMail) {
                case GMAIL:
                case YAHOO:
                    return "smtps";
                case ALICE:
                case HOTMAIL:
                case LIVE:
                case OUTLOOK:
                    return "smtp";
            }
        }

        return null;
    }
}
