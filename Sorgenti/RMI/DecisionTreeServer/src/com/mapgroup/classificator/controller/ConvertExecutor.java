package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.filemanager.RequestStruct;
import com.mapgroup.classificator.filemanager.writer.DataWriterFactory;
import com.mapgroup.classificator.filemanager.writer.IDataWriter;
import com.mapgroup.classificator.utility.FileSender;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.ServerResponse;


import java.io.File;
import javax.swing.JTextArea;

/**
 * This class models the executor used to convert a specific
 * saved dataset in a specified format available.
 */
public class ConvertExecutor implements IServerExecutor {
    /**
     * Converts the dataset contained in the specified request in the specific
     * dataset format specified by the user.
     *
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>null</li>
     * <li>dataset</li>
     * <li>an array which contains the file's name and its extension</li>
     * <li>log panel in which are printed all the result information</li>
     * </ul>

     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response with the converted file and a message with the result of the operation
     * @throws Exception - if some errors occurred in converting the file
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        // reads the filename and extension
        String[] data = (String[]) contextRequest.getAttribute(2);
        // now convert the current dataset in the extension
        // selected.
        IDataWriter datasetWriter = DataWriterFactory
                .createDataWriter(data[1]);
        String convDir = System.getProperty("user.dir")
                + File.separator + "conversion";

        Utility.checkDirectory(convDir);

        // the 4th attribute is the client ip address
        String tempName = convDir + File.separator
                +  contextRequest.getAttribute(3) + "."
                + data[1], completeFileName = Utility.getAvailableName(tempName);
        // the second attribute is the training set
        datasetWriter.write(new RequestStruct(
                completeFileName, contextRequest.getAttribute(1), data[0]));
        // the 5th attribute is the text area in which are printed the message
        byte[] fileContent = new FileSender(completeFileName,(JTextArea)contextRequest.getAttribute(4)).getTransferredFile();
        return new ServerResponse(fileContent, "The file " + completeFileName
                + " was correctly converted.\n");


    }
}
