package com.mapgroup.to;

import java.rmi.Remote;

/**
 * Remote interface for the server which grants to the client
 * to ask for some resources.
 */
public interface IRemoteServer extends Remote {
    /**
     * Returns the appropriate response according to the specific
     * client's request specified.
     *
     * @param clientRequest the client's request specified for this operation
     * @return the result of the computation
     * @throws Exception - if some errors occurred in the computation
     */
    public ServerResponse satisfyRequest(ClientRequest clientRequest) throws Exception;
}
