package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * This class provides the specific Properties object
 * for the Yahoo mail server according to its configuration
 * data.
 * */
class YahooPropertiesFactory implements IPropertiesFactory {

    /**
     * Returns the Properties object specific for the Yahoo mail server
     * which respect the smtp configuration specified by the
     * Yahoo mail server.
     * @param user username for the sender mail address
     * @param pass password for the sender mail address
     * @return Yahoo mail server Properties object
     */
	public Properties getProperties(String user, String pass) {
		Properties props = new Properties();
		props.put("mail.smtps.host", "smtp.mail.yahoo.com");
		props.put("mail.smtps.port", "465");
		props.put("mail.debug", false);
		props.put("mail.smtps.auth", true);
		props.put("mail.smtp.user", user.substring(0, user.lastIndexOf('@')));
		props.put("mail.smtp.pass", pass);

		return props;
	}
}