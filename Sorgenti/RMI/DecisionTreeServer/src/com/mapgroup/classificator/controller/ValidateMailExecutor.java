package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.mail.MailAddressValidator;
import com.mapgroup.to.InvalidMailAddressException;
import com.mapgroup.to.ServerResponse;

import javax.mail.internet.AddressException;

/**
 * This class provide a way in order to check the correctness of a specific
 * mail address according to the internet address format.
 */
public class ValidateMailExecutor implements IServerExecutor{
    /**
     * Checks the correctness of the mail address contained in the specified
     * structure.
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>null</li>
     * <li>null</li>
     * <li>mail address that will be verified</li>
     *
     * </ul>

     * @param contextRequest information about the context in which the computation will be done
     * @return The server response for the current computation
     * @throws Exception - if an invalid mail address is passed to the function
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        String mailAddr = (String) contextRequest.getAttribute(2);
        try {
            MailAddressValidator.validate(mailAddr);
            return new ServerResponse("correct mail address", null);
        } catch (AddressException e) {
            throw new InvalidMailAddressException(
                    e.getMessage());
        }
    }
}
