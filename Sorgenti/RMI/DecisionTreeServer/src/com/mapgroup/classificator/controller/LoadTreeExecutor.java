package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.to.ServerResponse;

import java.io.File;

/**
 * This class provide functionality in order to deserialize and load
 * a specific decision tree that is previously saved on a file.
 */
public class LoadTreeExecutor implements IServerExecutor {
    /**
     * Loads from a file, whose name is contained in the specified structure, a serialized
     * tree and starts from it the learning process. After that, returns a string representation of the
     * decision tree as a result of the operation.
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>decision tree</li>
     * <li>null</li>
     * <li>filename of the file in which is saved the decision tree</li>
     *
     * </ul>
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the decision tree's string representation and a message which displays the state of the operation
     * @throws Exception - if some errors occurred during the loading process
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {

        String fileName = (String) contextRequest.getAttribute(2);
        String filePath = System
                .getProperty("user.dir")
                + File.separator
                + "dataset"
                + File.separator + fileName;
        DecisionTree tree = DecisionTree.loadTree(filePath);
        contextRequest.setAttributes(0, tree);
        return new ServerResponse(tree.toString(), "The file " + fileName
                + " was correctly loaded.\n");
    }
}
