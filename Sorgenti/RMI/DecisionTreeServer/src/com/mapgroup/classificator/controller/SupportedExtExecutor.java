package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.utility.SupportedExtensions;
import com.mapgroup.to.ServerResponse;

/**
 * This class grants the possibility to get the list of all
 * supported file extension that can be used in order to save the
 * loaded dataset.
 */
public class SupportedExtExecutor implements IServerExecutor {
    /**
     * Returns a server response which contains the list of all the available
     * file extension which are supported by the program, that are used in order to save
     * the dataset loaded in memory.
     * <p>
     * The context request structure doesn't contain any attribute because they are not
     * needed for this session.
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response with the list of supported file extension
     * @throws Exception - if some errors occurred while retrieving the extension list
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        SupportedExtensions filter = new SupportedExtensions();
        return new ServerResponse(filter.getSupportedExtension(), null);
    }
}
