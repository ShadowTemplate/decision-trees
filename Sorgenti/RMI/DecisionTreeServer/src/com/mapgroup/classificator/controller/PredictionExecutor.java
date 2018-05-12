package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.tree.LeafNode;
import com.mapgroup.classificator.tree.NoTreeExists;
import com.mapgroup.classificator.tree.SplitNode;
import com.mapgroup.classificator.tree.UnknownValueException;
import com.mapgroup.to.ServerResponse;

import java.util.HashMap;

/**
 * This class grants to predict a specific class value starting from
 * specific requests made by the user.
 */
public class PredictionExecutor implements IServerExecutor {
    /** the user choices for each attribute */
    private HashMap<String,Integer> choices;

    /**
     * Satisfies the user's request of predicting a class value using the structure which contains
     * all the user's choices and uses them to predict the correct class value contained in the decision
     * tree specified in the context structure passed to the method.
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>decision tree</li>
     * <li>null</li>
     * <li>the data structure which contains the user choices</li>
     *
     * </ul>
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response which contains the predicted value generated from the user's choices
     * @throws Exception
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        choices = (HashMap<String, Integer>)contextRequest.getAttribute(2);
        DecisionTree tree = (DecisionTree)contextRequest.getAttribute(0);

        String predictedValue = predictClass(tree);

        return new ServerResponse(predictedValue, null);
    }

    /**
     * Inspects the decision tree constructed following the options
     * chosen by the user in order to get the predicted value.
     *
     * @param tree the current decision tree
     * @return the predicted value for this prediction session
     * @throws NoTreeExists           if no available tree was present
     * @throws UnknownValueException  if an incorrect choice was selected by the user
      */
    private String predictClass(DecisionTree tree) throws NoTreeExists,
            UnknownValueException {
        if (tree == null)
            throw new NoTreeExists();

        if (tree.getRoot() instanceof LeafNode)
            return ((LeafNode) tree.getRoot()).getPredictedClassValue();
        else {
            int risp = choices.get(((SplitNode)tree.getRoot()).getAttribute().getName());

            if (risp == -1 || risp >= tree.getRoot().getNumberOfChildren()) {
                throw new UnknownValueException();
            } else {
                return predictClass(tree.subTree(risp));
            }

        }
    }
}
