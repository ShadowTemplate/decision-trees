package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.ContinuousAttribute;
import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;

import java.io.*;
import java.util.TreeSet;

/**
 * This class represents the decision tree as a set of sub-trees.
 */
public class DecisionTree implements Serializable {

    /**
     * Root node of this sub-tree
     */
    private Node root;
    /**
     * Array of sub-trees that belong to the node identified by <code>root</code>.
     * <p>
     * Exists an array for each level of depth of the tree
     * </p>
     */
    private DecisionTree childTree[];

    /**
     * Returns the subtree of this decision tree in position
     * <code> child </code> of the array.
     *
     * @param child subtree index position
     * @return subtree in position <code>child</code>
     */
    public DecisionTree subTree(int child) {
        return childTree[child];

    }

    /**
     * Returns the root node of this decision tree.
     *
     * @return decision's tree root node, <code>null</code> if the tree isn't created
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Serializes this decision tree in a file with the specified
     * name <code>fileName</code>.
     *
     * @param fileName the filename in which the decision tree will be saved
     * @throws IOException - if some errors occurred working with the file
     */
    public void saveTree(String fileName) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));

        out.writeObject(this);
        out.close();

    }

    /**
     * Load the serialized decision tree contained in a file with the specified
     * name <code>fileName</code>.
     *
     * @param fileName filename in which the decision is serialized
     * @return the loaded decision tree
     * @throws IOException            - if some errors occurred working with the file
     * @throws ClassNotFoundException - if an invalid class was read
     */
    public static DecisionTree loadTree(String fileName) throws IOException, ClassNotFoundException {
        DecisionTree tree;

        ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
        tree = (DecisionTree) input.readObject();
        input.close();


        return tree;
    }

    /**
     * Constructs an empty decision tree
     */
    public DecisionTree() {
    }

    /**
     * Constructs a subtree of the whole decision tree and starts the tree's induction
     * from the training examples in input.
     *
     * @param trainingSet whole dataset used
     */
    public DecisionTree(Data trainingSet) {

        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, trainingSet.getNumberOfExamples() * 10 / 100);
    }

    /**
     * Generates a sub-tree with the subset specified in input creating a leaf node
     * or a split node. In this case determines the best node for the specified subset.
     * <p>
     * For this node associates a subtree having as root the selected node and a number of
     * subtree equals to the number of child generated from the split operation.
     * </p>
     *
     * <p>
     * Recursively, for each decision tree in <code>childTree</code> this method will be invoked in order
     * to start the learning operation on a reduced training subset of the current traning set.
     * </p>
     *
     * <p>
     * In the learning process, if the split node doesn't create any children, this node will become
     * a leaf node.
     * </p>
     *
     * @param trainingSet             whole training set
     * @param begin                   starter index of the training subset
     * @param end                     last index of the training subset
     * @param numberOfExamplesPerLeaf maximum number of children for the node
     */
    private void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        // A leaf node was generated
        if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {

            root = new LeafNode(trainingSet, begin, end);
        } else // a split node was generated
        {
            try {
                // determines the most frequent class in the current partition
                root = determineBestSplitNode(trainingSet, begin, end);

                childTree = new DecisionTree[root.getNumberOfChildren()];
                for (int i = 0; i < root.getNumberOfChildren(); i++) {
                    childTree[i] = new DecisionTree();

                    childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).getBeginIndex(), ((SplitNode) root).getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
                }
            } catch (NoSplitException e) {
                root = new LeafNode(trainingSet, begin, end);
            }

        }
    }

    /**
     * Checks if the current subset could be associated to a leaf node verifying if the
     * subset's cardinality is lower than the minimum value and the class attribute values' frequency.
     *
     * @param trainingSet             whole training set
     * @param begin                   starter index of the training subset
     * @param end                     last index of the training subset
     * @param numberOfExamplesPerLeaf maximum number of children for the node
     * @return <code>true</code> if the current subset verify the leaf node condition, <code>false</code> otherwise
     */
    private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
        /*
          A node becomes leaf if and only if:
          1) the number of training examples which are present in the subset is lower
          than numberOfExamplePerLeaf
          2)all the training set examples which are present in the subset belong
          to the same class

          If the node doesn't grant to us to obtain more information it becomes a leaf node.
         */


        if ((end - begin) + 1 < numberOfExamplesPerLeaf) {
            return true;
        } else {
            String currValue = trainingSet.getClassValue(begin);

            for (int i = begin + 1; i <= end; i++) {
                if (!currValue.equals(trainingSet.getClassValue(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * This function allows you to get one that, relative to the sub-set of training
     * and the training set currently analyzed, turns out to be the node that allows you
     * to have more information gain. In this way there is the possibility of generating a split
     * which provides a gain of information than the other,
     * thereby improving the process of construction of the decision tree.
     *
     * @param trainingSet full data Set
     * @param begin       first extreme of the training subset
     * @param end         last extreme of the training subset
     * @return a node with the highest information gain
     * @throws NoSplitException
     */
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) throws NoSplitException {
        TreeSet<SplitNode> ts = new TreeSet<SplitNode>();


        //for each independent variable(at the moment there are only discrete variable)
        //make an instance of DiscreteNode for this variable and set currentNode with this instance
        //update bestNode thereby to save the node with the best information gain

        if (trainingSet.getNumberOfExplanatoryAttributes() == 0) {
            throw new NoSplitException();    // Aggiungere eccezione diversa()
        }
        if (trainingSet.getExplanatoryAttribute(0) instanceof DiscreteAttribute) {
            ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) trainingSet.getExplanatoryAttribute(0)));
        } else {
            ts.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) trainingSet.getExplanatoryAttribute(0)));


        }

        for (int i = 1; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            if (trainingSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute) {
                ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute)
                        trainingSet.getExplanatoryAttribute(i)));

            } else {
                ts.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i)));

            }

        }

        SplitNode bestNode = ts.first();
        if (bestNode.getNumberOfChildren() == 1) {
            throw new NoSplitException();
        }

        // do an order with the split type
        trainingSet.sort(bestNode.getAttribute(), begin, end);

        return bestNode;

    }

    /**
     * Prints the decision tree
     *
     * @return the <code>String</code> which represents decision tree
     */
    public String toString() {
        String tree = root.toString() + "\n";

        if ( !(root instanceof LeafNode) ) {
            for (DecisionTree aChildTree : childTree) {
                tree += aChildTree;
            }
        }
        return tree;
    }

    /**
     * Returns the number of subtree of this decision tree.
     *
     * @return number of subtree
     *
     * */

    public int getNumberOfChildren(){
        return childTree.length;
    }

    /**
     * Prints the information of the decision tree
     *
    public void printTree() {
        System.out.println("********* TREE **********\n");
        System.out.println(toString());
        System.out.println("*************************\n");
    }

    /**
     * Prints the information of boolean expression resulted
     * by the construction of decision tree generated from input data set
     *
    public void printRules() {
        String current;
        System.out.println("********* RULES **********\n");
        if (root instanceof LeafNode) {
            current = "CLASS= " + ((LeafNode) root).getPredictedClassValue();
            System.out.println(current);
        } else {
            for (int i = 0; i < childTree.length; i++) {
                if (root instanceof DiscreteNode) {
                    current = ((SplitNode) root).getAttribute().getName() + ((SplitNode) root).getSplitInfo(i).getComparator() + ((DiscreteNode) root).getSplitInfo(i).getSplitValue();
                } else {
                    current = ((SplitNode) root).getAttribute().getName() + ((SplitNode) root).getSplitInfo(i).getComparator() + ((ContinuousNode) root).getSplitInfo(i).getSplitValue();
                }
                childTree[i].printRules(current);
            }
        }
        System.out.println("*************************\n");
    }

    /**
     * Supplementary method used by printRules {@link #printRules()}
     *
     * @param current analyzed rule
     *

    private void printRules(String current) {
        String s = current;
        if (root instanceof LeafNode) {
            System.out.println("if (" + current + ") then predicted value is " + ((LeafNode) root).getPredictedClassValue());
        } else {
            for (int i = 0; i < childTree.length; i++) {

                current += " AND ";
                current += ((SplitNode) root).getAttribute().getName() + ((SplitNode) root).getSplitInfo(i).getComparator() + ((SplitNode) root).getSplitInfo(i).getSplitValue();
                childTree[i].printRules(current);
                current = s;
            }
        }
    }*/

}