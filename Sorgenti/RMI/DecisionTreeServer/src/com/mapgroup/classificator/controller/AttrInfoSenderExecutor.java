package com.mapgroup.classificator.controller;

import com.mapgroup.classificator.tree.ContinuousNode;
import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.tree.DiscreteNode;
import com.mapgroup.classificator.tree.LeafNode;
import com.mapgroup.classificator.tree.Node;
import com.mapgroup.classificator.tree.SplitNode;
import com.mapgroup.to.ServerResponse;

import java.util.HashMap;

/**
 * This class grants the possibility to send to the client
 * the information about the attributes' values needed for the
 * prediction session.
 */
public class AttrInfoSenderExecutor implements IServerExecutor {
    /**
     * Generates all the information about the attributes and save them in a specific
     * structure that is added to the server response returned by the method.
     * <p>
     * The context request structure must have these specific parameters in the order:
     * <ul>
     * <li>decision tree</li>
     * <li>null</li>
     *
     * </ul>

     * @param contextRequest information about the context in which the computation will be done
     * @return the response which contains the attributes' information
     * @throws Exception - if some errors occurred in traversing the decision tree
     */
    public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        // the first attribute of the structure is always the decision tree
        DecisionTree tree = (DecisionTree)contextRequest.getAttribute(0);


        HashMap<String, String[]> attributeInfo = new HashMap<String, String[]>();

        getAttributeInformation(tree, attributeInfo);

        return new ServerResponse(attributeInfo, null);
    }

    /**
     * Retrieves all the attributes information from the decision tree specified
     * and saves them in the specified structure.
     * <p>
     *  Each pair of the <code>attributeInfo</code> will contain as a key, the attribute name and
     *  as value the possible values for the specific attribute.
     *
     * @param tree the decision tree that will be traversed
     * @param attributeInfo structure that will contain the attributes' information
     */
    private void getAttributeInformation(DecisionTree tree, HashMap<String, String[]> attributeInfo){
        Node node = tree.getRoot();
        if( !(node instanceof LeafNode)){
            getCurrNodeInformation((SplitNode)node, attributeInfo);
            for(int i = 0; i < tree.getNumberOfChildren(); i++){
                getAttributeInformation(tree.subTree(i), attributeInfo);
            }

        }

    }

    /**
     * Acquires all the information of the specified node and saves it as
     * an entry of the specified data structure <code>attributeInfo</code>
     * @param splitNode the current node that will be analyzed
     * @param attributeInfo the structure that collect attributes' information
     */
    private void getCurrNodeInformation(SplitNode splitNode, HashMap<String, String[]> attributeInfo){
        // Gets current node information
        String[] values = new String[splitNode.getNumberOfChildren()];

        if( splitNode instanceof DiscreteNode ){

            for(int i = 0; i < splitNode.getNumberOfChildren(); i++){
                values[i] = (String)splitNode.getSplitInfo(i).getSplitValue();
            }
        } else{
            String[] tempString = ((ContinuousNode)splitNode).formulateQuery().split("\n");

            for( int i = 0; i < tempString.length-1; i++ ){
                String comparator = ((ContinuousNode)splitNode).getSplitInfo(i).getComparator();
                // returns the string which contains the order relationship operator and the attribute's value
                values[i] = tempString[i].substring(tempString[i].indexOf(comparator.charAt(0)), tempString[i].length());
            }
        }

        attributeInfo.put(splitNode.getAttribute().getName(), values);
    }
}
