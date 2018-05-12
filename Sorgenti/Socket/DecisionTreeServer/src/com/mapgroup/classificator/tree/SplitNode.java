package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.Data;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Models the split node abstract entity extended its superclass Node.
 * Within it is present the SplitInfo class which contains all the information
 * which concerns a split node.
 */
public abstract class SplitNode extends Node implements Comparable<SplitNode> {

    /**
     * Defines an order relationship on the split nodes based
     * upon the information gain that they had.
     *
     * @param s the node that will be compared with this split node
     * @return a negative integer, zero, or a positive integer as this split node has an information gain less than, equal to, or greater than the specified split node.
     */
    public int compareTo(SplitNode s) {

        if (this.getInformationGain() == s.getInformationGain())
            return 0;
        else if (s.getInformationGain() < this.getInformationGain())
            return -1;
        else
            return 1;
    }

    /**
     * This class contains all the information about the split node.
     */

    class SplitInfo implements Serializable {
        /**
         * Independent attribute value which defines a split
         */
        private final Object splitValue;
        /**
         * starter index of the subset defined by the split node
         */
        private final int beginIndex;
        /**
         * last index of the subset defined by the split node
         */
        private final int endIndex;
        /**
         * Number of split node(child nodes) generated from this split node
         */
        private final int numberChild;
        /**
         * Order relationship defined on discrete attribute
         */
        private String comparator = "=";

        /**
         * Constructs this split node initializing all its attributes.
         * <p>
         * This specific constructor is used only for discrete attributes
         * because the default order relationship is used.
         * </p>
         *
         * @param splitValue  attribute's value that defines the split
         * @param beginIndex  starter index of the partition
         * @param endIndex    last index of the partition
         * @param numberChild number of child node defined
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }

        /**
         * Constructs this split node initializing all its attributes and
         * specifying an order relationship for them.
         *
         * @param splitValue  attribute's value that defines the split
         * @param beginIndex  starter index of the partition
         * @param endIndex    last index of the partition
         * @param numberChild number of child node defined
         * @param comparator  order relationship specified
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }

        /**
         * Returns the starter index of the dataset's subset defined by this
         * split node.
         *
         * @return starter index of the partition
         */
        int getBeginIndex() {
            return beginIndex;
        }

        /**
         * Returns the last index of the dataset's subset defined by this
         * split node.
         *
         * @return starter index of the partition
         */
        int getEndIndex() {
            return endIndex;
        }

        /**
         * Returns the value associated to this split node.
         *
         * @return split node's value
         */
        private Object getSplitValue() {
            return splitValue;
        }

        /**
         * Returns the string representation of this split node which
         * displays all the information contained in it, such as,
         * the number of its child nodes, the order relationship defined on
         * it and its value.
         *
         * @return string representation of this split node
         */
        public String toString() {
            return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-" + endIndex + "]";
        }

        /**
         * Returns the order relationship defined for this split node.
         *
         * @return split node's order relationship
         */
        private String getComparator() {
            return comparator;
        }
    }

    /**
     * Independent attribute on which the split is generated
     */
    private final Attribute attribute;
    /**
     * List of all the possible split values for this independent attribute
     */
    protected List<SplitInfo> mapSplit;
    /**
     * attribute's information gain
     */
    protected float infoGain;

    /**
     * Abstract method which is used in order to generate the information
     * needed for each candidate split node.
     *
     * @param trainingSet       the whole training set
     * @param beginExampleIndex starter index of the training subset
     * @param endExampleIndex   last index of the training subset
     * @param attribute         independent attribute on which the split is defined
     */
    protected abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);

    
    /**
     * Abstract method used to model the test condition for this
     * split node.
     *
     * @param value attribute value that needs to be tested
     * @return test condition result
     * @throws UnknownValueException
     */
    //protected abstract int testCondition(Object value) throws UnknownValueException;

    /**
     * Constructs a split node with the specified information
     * <p>
     * Invokes the superclass' constructor, orders the input attribute values for the
     * examples contained in the training subset identified by <code>beginExampleIndex</code>
     * and <code>endExampleIndex</code> and uses this order in order to determine
     * the possible split value and populate <code>mapSplit</code>, computes the attribute's
     * entropy and determines its information gain.
     * </p>
     *
     * @param trainingSet       the whole training set
     * @param beginExampleIndex starter index of the training subset
     * @param endExampleIndex   last index of the training subset
     * @param attribute         independent attribute on which the split is defined
     */
    SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order

        mapSplit = new ArrayList<SplitInfo>();

        // constructs mapSplit attribute
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

        if (mapSplit != null) {
            // compute entropy=sum_i{pi*E(i)} i=1..m ;m = number of classes
            float splitEntropy = 0;
            for (SplitInfo s : mapSplit) {
                float p = ((float) (s.getEndIndex() - s.getBeginIndex() + 1)) / (endExampleIndex - beginExampleIndex + 1);
                float localEntropy = new LeafNode(trainingSet, s.getBeginIndex(), s.getEndIndex()).getEntropy();
                splitEntropy += (p * localEntropy);
            }

            // compute info gain
            infoGain = entropy - splitEntropy;
        } else {  // no split value for the node. The information gain is zero
            infoGain = 0;
        }
    }

    /**
     * Returns the attribute used in the split operation.
     *
     * @return attribute object
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Returns the information associated
     * to this split node.
     *
     * @return this split node's information gain
     */
    float getInformationGain() {
        return infoGain;
    }

    /**
     * Returns the number of split generated
     * by this split node.
     *
     * @return returns <code>mapSplit</code>'s size
     */
    public int getNumberOfChildren() {
        return mapSplit.size();
    }


    /**
     * Returns the SplitInfo object associated
     * to a specific child of this split node, identified
     * by the specified integer value <code>child</code>.
     *
     * @param child identifier for the split
     * @return information about the specified child
     */
    SplitInfo getSplitInfo(int child) {
        return mapSplit.get(child);
    }

    /**
     * Generates all the information of each test in a single string
     * that will be displayed during the prediction session.
     *
     * @return formatted information about each single test
     */
    public String formulateQuery() {

        String query = "";
        for (int i = 0; i < mapSplit.size(); i++) {
            query += ("(" + i + ") " + attribute + " " + mapSplit.get(i).getComparator() + " " + mapSplit.get(i).getSplitValue()) + "\n ";
        }

        return query;
    }

    /**
     * Returns a formatted string representation of this split node
     * which contains all the information about each test, such as
     * the training subset analyzed, the information gain value and the entropy
     * value.
     *
     * @return string representation for this split node
     */
    public String toString() {
        String v = "DISCRETE SPLIT : attribute=" + attribute + " " + super.toString() + "Info Gain: " + getInformationGain() + "\n";

        for (SplitInfo s : mapSplit) {
            v += "\t" + s + "\n";
        }

        return v;
    }
}
