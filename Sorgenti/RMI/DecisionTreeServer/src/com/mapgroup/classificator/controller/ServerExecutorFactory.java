package com.mapgroup.classificator.controller;

import com.mapgroup.to.ClientRequest;
import com.mapgroup.to.ServerCommand;

/**
 * According to the Factory design pattern, this class models an instrument
 * in order to instantiate the specific executor class for the specific context
 * in which we are.
 */
public class ServerExecutorFactory {
    /**
     * Creates a specific <code>IServerExecutor</code> in order to
     * satisfy the client's request specified.
     *
     * @param clientRequest the client's request
     * @return the specific executor in order to complete the required operation
     */
    public static IServerExecutor createExecutor(ClientRequest clientRequest){

        // The first attribute is the server command needed to resolve the context
        ServerCommand command = (ServerCommand)clientRequest.getAttribute(0);

        switch (command) {

            case BUILD_TREE_FROM_DB:
                return new DatabaseExecutor();
            case SERIALIZE_TREE:
                return new TreeSerializerExecutor();
            case LOAD_SERIALIZED_TREE:
                return new LoadTreeExecutor();
            case START_PREDICTION: 
        	return new PredictionExecutor();
            case SUPPORTED_EXTENSIONS:
                return new SupportedExtExecutor();
            case UPLOAD_FILE:
                return new UploadExecutor();
            case AVAILABLE_DAT_FILES:
                return new AvailableDatExecutor();
            case SEND_EMAIL:
                return new SendMailExecutor();
            case VALIDATE_EMAIL:
                return new ValidateMailExecutor();
            case CONVERT_DATASET:
                return new ConvertExecutor();
            case CLOSE_CONNECTION: 
        	return new ConnectionCloserExecutor();
            case ATTRIBUTES_INFORMATION: 
        	return new AttrInfoSenderExecutor();
        }
        return null;
    }


}
