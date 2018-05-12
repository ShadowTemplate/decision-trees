package com.mapgroup.to;

import java.io.Serializable;

/**
 * Type that represents all the possible commands that can be sent
 * by the client to the server
 */
public enum ServerCommand implements Serializable{
    
    /** Loads a dataset from a table on a DBMS */
    BUILD_TREE_FROM_DB(1),
    
    /** Writes the current current tree on a .dat file*/
    SERIALIZE_TREE (2),
    
    /** Loads a tree from a .dat file */
    LOAD_SERIALIZED_TREE (3),
    
    /** Starts a prediction on the current tree */
    START_PREDICTION (4),
    
    /** Closes the connection between the server and the clients that sends the command */
    CLOSE_CONNECTION (5),
    
    /** Asks for the list of currently supported format for upload */
    SUPPORTED_EXTENSIONS (6),
    
    /** Uploads a file from the client to the server */
    UPLOAD_FILE (7),
    
    /** Asks for the list of currently available .dat files */
    AVAILABLE_DAT_FILES (8),
    
    /** Sends one or more email */
    SEND_EMAIL (9),
    
    /** Check if an address is a valid email */
    VALIDATE_EMAIL (10),
    
    /** Converts a dataset from a format to another */
    CONVERT_DATASET (11);
    

    /** Value of the command */
    private final int id;

    /** Builds the command assigning it a value*/
    private ServerCommand(int id){
        this.id = id;
    }
}
