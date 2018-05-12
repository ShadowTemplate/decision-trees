package com.mapgroup.classificator.mail.properties;

import java.util.Properties;

/**
 * This class provides the specific Properties object
 * for the Hotmail mail server according to its configuration
 * data.
 * <p>
 * This class is perfectly compatible with the Outlook and Live
 * mail addresses.
 * */
class HotmailPropertiesFactory implements IPropertiesFactory {
    /**
     * Returns the Properties object specific for the Hotmail mail server
     * which respect the smtp configuration specified by the
     * Hotmail mail server.
     * @param user username for the sender mail address
     * @param pass password for the sender mail address
     * @return Hotmail mail server Properties object
     */
	public Properties getProperties(String user, String pass) {

		Properties props = new Properties();

		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", "smtp.live.com");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.port", "587");
		props.setProperty("mail.smtp.quitwait", "false");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.user", user);
		props.put("mail.smtp.pass", pass);

		return props;

	}
}
