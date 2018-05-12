package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.mail.MailSender;
import com.mapgroup.classificator.utility.PdfCreator;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.MailDataTO;
import com.mapgroup.to.ServerResponse;

import java.io.File;

/**
 * This class grants to the client to send a mail report
 * of the current prediction session to multiple email addressees and attach to
 * this report also a pdf document which contains the result of all the user's operation
 * that he has done during the prediction session.
 */
public class SendMailExecutor implements IServerExecutor {

    /**
     * Sends an email to the addressees present in a specific structure contained in the specified
     * structure <code>contextRequest</code>, from the user's email address with the report of the prediction
     * session.
     * <p>
     * The user is also able to specify if he wants to sends a pdf document as attachment. This document
     * contains within it, all the user's choices made during the prediction session and the string
     * representation of the decision tree.
     * <p>
     * The structure which contains all the information about the email, is contained in a specific structure
     * called <code>MailDataTO</code> which grants to the server to obtain all the needed information to
     * send correctly the email from the client's email address, such as the username for the email address, the email address's
     * password.
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>null</li>
     * <li>null</li>
     * <li>mail data structure (information about the email that will be sent)</li>
     * <li>client ip address</li>
     * </ul>
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response with a message which represents the correct execution of the process
     * @throws Exception - if some errors in sending the email or creating the pdf occurred
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        try {
            MailDataTO mailData = (MailDataTO) contextRequest.getAttribute(2);
            String reportPath = System
                    .getProperty("user.dir")
                    + File.separator + "report",
                    clientHost = (String) contextRequest.getAttribute(3);
            Utility.checkDirectory(reportPath);

            String encryptName = "report_" + Utility.encryptIP(clientHost) + "." + "pdf";
            String pdfFileName = reportPath
                    + File.separator
                    + encryptName;

            PdfCreator.createReport(
                    mailData.getSessionTO(), pdfFileName);
            MailSender.sendEmail(mailData, pdfFileName);

            String msg;
            if (mailData.isAttached())
                msg = "email with attachment\n"
                        + encryptName + "\nwas sent to\n"
                        + mailData.getAddressees()
                        + " correctly.\n";
            else
                msg = "email was sent to "
                        + mailData.getAddressees()
                        + " correctly.\n";

            return new ServerResponse("email correctly send", msg);
        }catch(IllegalArgumentException e ){
            throw e;
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
