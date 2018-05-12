package com.mapgroup.to;

import java.io.Serializable;
import java.util.List;

/**
 * This class transmit information from the client to the server with the
 * information the server needs to properly send one or more email.
 */
public class MailDataTO implements Serializable {
    
    /** List of the addressees of the mail */
    private List<String> addressees;
    /** Information on the prediction (needed for the attachment)*/
    private PredSessionTO track;
    /** email address of the sender */
    private String from;
    /** Password of the sender */
    private String password;
    /** Text of the email */
    private String mailText;
    /** Object of the email */
    private String object;
    /** Flag necessary to know whether the mail has an attachment or not */
    private boolean attachFlag;
    
    /**
     * Says if the email has an attachment or not.
     * 
     * @return the flag
     */
    public boolean isAttached(){
    	return attachFlag;
    }
    
    /** 
     * Sets the presence of the attachment.
     * 
     * @param flag Value of the flag
     */

    public void setAttach(boolean flag){
    	attachFlag = flag;
    }
    
    /**
     * Returns the value of the object of the email.
     * 
     * @return the object field
     * */
    public String getObject() {
        return object;
    }

    /**
     * Sets the value of the object field of the mail.
     * 
     * @param object The email's object
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * Returns the value of the email's text field. 
     * 
     * @return the text field
     */
    public String getMailText() {
        return mailText;
    }

    /**
     * Sets the value of the email's text.
     * 
     * @param mailText The email's text
     */
    public void setMailText(String mailText) {
        this.mailText = mailText;
    }

    /**
     * Returns the value of the user's password. 
     * 
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the user's password.
     * 
     * @param password The user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the value of the email's sender. 
     * 
     * @return the email's sender
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the email's sender.
     * 
     * @param from The email's sender
     */
    public void setFrom(String from) {
        this.from = from;
    }
    
    /**
     * Returns the value of the prediction tracker. 
     * 
     * @return the prediction tracker
     */
    public PredSessionTO getSessionTO(){
    	return this.track;
    }
    
    /**
     * Sets the predicition session's information tracker
     * with the specified one
     * 
     * @param track prediction session's tracker
     */
    public void setSessionTO(PredSessionTO track){
    	this.track = track;
    }

    /**
     * Returns the list of the addressees. 
     * 
     * @return the list of the addressees
     */
    public List<String> getAddressees() {
        return addressees;
    }

    /**
     * Sets the value of the email's addressees.
     * 
     * @param addressees The email's addressees
     */
    public void setAddressees(List<String> addressees) {
        this.addressees = addressees;
    }

}
