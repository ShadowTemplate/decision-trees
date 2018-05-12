package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.ServerResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class provide a way in order to manage the file uploading done
 * by the client.
 */
public class UploadExecutor implements IServerExecutor {

   /**
     * Manages the upload request made by the user and provides a way in order to
     * save the file that the user want to upload in a specific folder of the server.
     * <p>
     * All the information are contained in the specified request which is used in order
     * to complete the uploading process in a correct way.
     *
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>decision tree</li>
     * <li>dataset</li>
     * <li>file extension</li>
     * <li>the content of the file that will be uploaded</li>
     * <li>client ip address</li>
     * </ul>
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response structure which contains the decision tree string representation and a message which displays the result
     * @throws Exception - if some errors occurred in managing the upload
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {


        ServerResponse response = new ServerResponse();
        String downDir = System.getProperty("user.dir")
                + File.separator + "download"
                + File.separator;
        String extension = (String) contextRequest.getAttribute(2), clientHost = (String) contextRequest.getAttribute(4);
        byte[] fileContent = (byte[]) contextRequest.getAttribute(3);

        Utility.checkDirectory(downDir);
        String fileSaved = writeFileContent(
                fileContent, downDir, extension, clientHost);
        try {
            Data trainingSet = new Data(fileSaved);
            DecisionTree tree = new DecisionTree(trainingSet);

            contextRequest.setAttributes(0, tree);
            contextRequest.setAttributes(1, trainingSet);

            response.addAttribute(tree.toString());
            response.addAttribute("Download of " + fileSaved
                    + " completed.\n");

            return response;
        } catch (DataException e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * Writes the file content saved in the specified byte array in a file placed in the directory
     * defined by <code>downDir</code> and which will have as extension the specified extension and as
     * filename an available one generated from the client host ip address specified.
     *
     * @param fileContent the content of the file that will be uploaded
     * @param downDir  the folder in which the file will be saved
     * @param extension the file's extension
     * @param clientHost the client that asks for the upload
     * @return The complete filepath of the uploaded file
     * @throws IOException - if some errors occurs in writing the file
     */
    private String writeFileContent(byte[] fileContent, String downDir, String extension, String clientHost) throws IOException {
        String tempName = downDir + clientHost + "." + extension;
        String fileName = Utility.getAvailableName(tempName);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
        out.write(fileContent);
        out.close();

        return fileName;
    }
}
