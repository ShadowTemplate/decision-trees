package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * This class provides the specific Properties object
 * for the Google mail server according to its configuration
 * data.
 * */
class GmailPropertiesFactory implements IPropertiesFactory {

    /**
     * Returns the Properties object specific for the Google mail server
     * which respect the smtp configuration specified by the
     * Google mail server.
     * @param user username for the sender mail address
     * @param pass password for the sender mail address
     * @return Google mail server Properties object
     */
	public Properties getProperties(String user, String pass) {

		Properties props = new Properties();
		props.put("mail.smtps.host", "smtp.gmail.com");
		props.put("mail.smtps.port", "465");
		props.put("mail.debug", false);
		props.put("mail.smtps.auth", true);
		props.put("mail.smtp.user", user.substring(0, user.lastIndexOf('@')));
		props.put("mail.smtp.pass", pass);

		return props;
	}
}
