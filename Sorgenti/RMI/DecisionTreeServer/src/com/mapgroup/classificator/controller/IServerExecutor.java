package com.mapgroup.classificator.controller;

import com.mapgroup.to.ServerResponse;

/**
 * According to the "Command" design pattern, this interface represents
 * the way in order to execute a specific operation to satisfy a specific request.
 */
public interface IServerExecutor {
    /**
     * Returns the response resulting from the computation on the specified
     * data contained in the structure which represents the context in which the operation
     * will be done.
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return computation's result
     * @throws Exception  - if some errors occurred during the computation
     */
     public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception;

}
