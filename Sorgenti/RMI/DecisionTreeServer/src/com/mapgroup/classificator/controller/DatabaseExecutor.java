package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.to.ServerResponse;

/**
 * This class grants the possibility to load from a database table
 * the dataset that will be used in order to start the decision tree's learning
 * process.
 */
public class DatabaseExecutor implements IServerExecutor {


    /**
     * Generates the decision tree from the dataset saved in a database table whose name is specified by the user.
     * All the information needed to complete the task are stored in the specified request, such as
     * the decision tree that will be created, the dataset that will be read and the table's name.
     * <p>
     *
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>tree</li>
     * <li>dataset</li>
     * <li>table's name</li>
     *
     * </ul>
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response which contains the decision tree string representation and the message which notify the result
     * @throws Exception - if some errors occurs in the creation of the decision tree or in reading from the database
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        try {
            String tableName = (String)contextRequest.getAttribute(2);

            Data trainingSet = new Data(tableName);

            DecisionTree tree = new DecisionTree(trainingSet);

            contextRequest.setAttributes(0,tree);
            contextRequest.setAttributes(1,trainingSet);

            String resultMsg = "Correctly loaded the database table "
                    + tableName + "\n";
            return new ServerResponse(tree.toString(), resultMsg );

        } catch (DataException e) {
            System.err.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

}
