package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.ServerResponse;

import java.io.File;

/**
 * This class grant the possibility to store in specific file the
 * generated decision tree.
 */
public class TreeSerializerExecutor implements IServerExecutor{
    /**
     * Serializes the decision tree contained in the structure specified in a specific
     * file whose name is contained in the <code>contextRequest</code> structure.
     *
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>decision tree</li>
     * <li>null</li>
     * <li>filename chosen</li>
     *
     * </ul>
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return a server response which notifies the completed operation
     * @throws Exception - if some errors in serializing the tree occurred
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        // the first attribute is the decision tree
        DecisionTree tree = (DecisionTree)contextRequest.getAttribute(0);
        ServerResponse response = new ServerResponse();

        if (tree == null) {
            throw new NullPointerException("Exception: Unable to serialize an empty decision tree.");
        } else {
            String serialPath = System
                    .getProperty("user.dir")
                    + File.separator + "dataset";
            // he third attribute is the filename chosen
            String fileName = (String)contextRequest.getAttribute(2);
            Utility.checkDirectory(serialPath);

            String filePath = Utility
                    .getAvailableName(serialPath
                            + File.separator + fileName);
            tree.saveTree(filePath);

            String msg = "The tree was correctly saved in "
                    + filePath
                    .substring(
                            filePath.lastIndexOf(File.separator) + 1,
                            filePath.length())
                    + "\n";
            response.addAttribute(null);
            response.addAttribute(msg);
        }
        return response;
    }
}
