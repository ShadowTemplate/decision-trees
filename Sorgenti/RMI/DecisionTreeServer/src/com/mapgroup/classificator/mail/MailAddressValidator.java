package com.mapgroup.classificator.mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * This class grants to validate the email address according
 * to the internet mail address format.
 * <p>
 * The email validation functionality was provided by the class
 * <a href=http://docs.oracle.com/javaee/6/api/javax/mail/internet/InternetAddress.html>InternetAddress</a>
 * of the javax.mail library.
 * </p>
 * */
public class MailAddressValidator {
    private MailAddressValidator(){

    }

    /**
     * Checks if the specified mail address <code>mailAddress</code>
     * is in the specific internet address format.
     * <p>
     * An InternetAddress object was created from the specified
     * email address and the email address validation was provided
     * by these class's functionality.
     * </p>
     *
     * @param mailAddress the email address
     * @throws AddressException - if the specified mail address is not in the correct format
     *
     * */
    public static void validate(String mailAddress) throws AddressException{
        InternetAddress emailAddress = new InternetAddress(mailAddress);
        emailAddress.validate();

    }

}
