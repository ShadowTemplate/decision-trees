package com.mapgroup.classificator.mail;

import com.mapgroup.classificator.mail.properties.PropertiesFactory;
import com.mapgroup.to.MailDataTO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * A support class which provides functionality
 * in order to grant the possibility to send emails
 * to some addressees.
 * <p>
 * At the moment are supported only some mail server like:
 * <ul>
 * <li> Gmail </li>
 * <li> Yahoo </li>
 * <li> Alice </li>
 * <li> Outlook (with all the related mail addresses)</li>
 * </ul>
 * </p>
 *
 * */
public class MailSender {

	private MailSender() {

	}

    /**
     * Returns an array of email addresses in the correct internet format
     * taking them from the <code>struct</code> attributes.
     * @param struct the structure which contains all the main email data
     * @return the list of addressees in the correct internet address format
     * @throws AddressException - if one of the specified address doesn't respect the internet standard
     * */
	private static InternetAddress[] getRecipientsAddresses(MailDataTO struct) throws AddressException {
		// create an array which could hold all the recipients
		List<String> to = struct.getAddressees();
		InternetAddress[] address = new InternetAddress[to.size()];

		// for-each recipients, create its Internet Address.
		for (int i = 0; i < to.size(); i++) {
			address[i] = new InternetAddress(to.get(i));

		}

		return address;

	}

    /**
     * Add to the specified email message <code>message</code> an attachment
     * which is represented by the file with name <code>attachmentName</code>.
     * <p>
     * The current <code>message</code> is divided in two part, the first part will contain
     * the email's body and the second the attachment chosen for the email.
     * </p>
     * @param message email message raw structure
     * @param struct  email data needed to send the email
     * @param attachmentName the name of the chosen attachment
     * @throws MessagingException - if some errors occurs during the message construction
     * @throws IOException - if some errors occurs in creating the attachment
     */
	private static void createAttachment(MimeMessage message, MailDataTO struct, String attachmentName)
			throws MessagingException, IOException {

		// Create multi part message
		Multipart multipart = new MimeMultipart();

		// the first part will be the text
		MimeBodyPart part1 = new MimeBodyPart();
		part1.setText(struct.getMailText()); // attach the text to the first
												// part of the mail.

		MimeBodyPart part2 = new MimeBodyPart();
		// Create pdf attachment
	
		part2.attachFile(attachmentName);

		multipart.addBodyPart(part1);
		multipart.addBodyPart(part2);

		// Now add the new multipart mime message to the main message structure
		// that will be sent.
		message.setContent(multipart);

	}

    /**
     * Retrieves from the sender's email address contained in the
     * email data structure(<code>struct</code>) the specific
     * server mail.
     * <p>
     * Standing to the <a href=http://tools.ietf.org/html/rfc2822#section-3.4>RFC internet format</a> email address is based upon
     * two important part: the local part and the domain part.
     * This method grants the possibility to get from the domain part of the email address, the specific mail server
     * to which the specified email address belong to.
     * </p>
     *
     * @param struct the email data structure
     * @return the email address server mail
     * */
	private static String getServerMailName(MailDataTO struct) {
		// get the sender mail address and parse it
		String sender = struct.getFrom();

		return (sender.substring(sender.lastIndexOf('@') + 1,
				sender.lastIndexOf('.')));

	}

    /**
     * Sends an email using the data contained in the email data structure
     * <code>struct</code>. If the email requires an attachment a file with
     * the specified <code>attachmentName</code> was added to the mail.
     *
     * @param struct the email data structure
     * @param attachmentName the supplementary attachment filename
     * @throws Exception - if some errors occurs while constructing and sending the email/s
     */
	public static void sendEmail(MailDataTO struct, String attachmentName) throws Exception {
		String serverMail = getServerMailName(struct), protocol = PropertiesFactory.getProtocol(serverMail);
        Properties props = PropertiesFactory.getProperties(serverMail, struct.getFrom(), struct.getPassword());
		Session session = Session.getInstance(props, null);
		session.setDebug(false);

		// Message structure that will be send
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(struct.getFrom()));

		message.setRecipients(Message.RecipientType.TO,
				getRecipientsAddresses(struct));
		message.setSubject(struct.getObject());
		message.setSentDate(new Date());

		/* an attachment was specified, now create the ad-hoc message structure
		  for it.*/
		if (struct.isAttached()) {
			createAttachment(message,struct, attachmentName);

		} else {
			// insert only the text in the message.
			message.setText(struct.getMailText());
		}

		Transport t = session.getTransport(protocol);

		t.connect(props.getProperty("mail.smtp.user"), struct.getPassword());
		t.sendMessage(message, message.getAllRecipients());

		t.close();

	}
}
