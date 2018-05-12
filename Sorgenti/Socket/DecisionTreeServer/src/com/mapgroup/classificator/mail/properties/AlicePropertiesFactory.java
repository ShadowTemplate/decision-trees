package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * This class provides the specific Properties object
 * for the Alice mail server according to its configuration
 * data.
 * */
class AlicePropertiesFactory implements IPropertiesFactory {

    /**
     * Returns the Properties object specific for the Alice mail server
     * which respect the smtp configuration specified by the
     * Alice mail server.
     * @param user username for the sender mail address
     * @param pass password for the sender mail address
     * @return Alice mail server Properties object
     */
	public Properties getProperties(String user, String pass) {

		Properties props = new Properties();

		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "out.alice.it");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.port", "587");
		props.setProperty("mail.smtp.quitwait", "false");
		props.put("mail.smtp.user", user);
		props.put("mail.smtp.pass", pass);

		return props;

	}
}
