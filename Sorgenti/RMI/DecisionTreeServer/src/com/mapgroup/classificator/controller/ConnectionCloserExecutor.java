package com.mapgroup.classificator.controller;

import com.mapgroup.to.ServerResponse;

import javax.swing.DefaultListModel;
import javax.swing.JList;


/**
 * This class provides a way in order to ask to the server to close the connection
 */
public class ConnectionCloserExecutor implements IServerExecutor{

    /**
     * Sending a specific context structure which contains the current context information, asks to the
     * server to close the current connection with the specified host and prints all the result
     * of the operation on a specific panel.
     *
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>null</li>
     * <li>null</li>
     * <li>the panel in which are listed all the connected clients</li>
     * <li>the model associated to the panel</li>
     * <li>client's ip address</li>
     * </ul>

     *
     * @param contextRequest information about the context in which the computation will be done
     * @return Server response with a specific message that is printed when the operation is completed
     * @throws Exception - if some errors in closing the connection occurred
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        JList clientArea = (JList)contextRequest.getAttribute(2);
        @SuppressWarnings("unchecked") DefaultListModel<String> listModel = (DefaultListModel<String>)contextRequest.getAttribute(3);
        String clientHost = (String)contextRequest.getAttribute(4);
        ServerResponse response = new ServerResponse();
        response.addAttribute(null);
        if(listModel.contains(clientHost)){
            listModel.removeElement(clientHost);

            clientArea.setModel(listModel);
            response.addAttribute("Client host " + clientHost + " correctly disconnected.");
        }else{
            response.addAttribute("Client host " + clientHost + " wasn't correctly disconnected.");
        }

        return response;
    }
}
