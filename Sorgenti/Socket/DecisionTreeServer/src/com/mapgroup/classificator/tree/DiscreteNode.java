package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;

/**
 * This class models an entity of split node relative to an independent discrete attribute
 */

class DiscreteNode extends SplitNode {

    /**
     * Instances an object with the superclass constructor with parameter <code>attribute</code>
     *
     * @param trainingSet current training set
     * @param beginExampleIndex first extreme of the training subset
     * @param endExampleIndex last extreme of the training subset
     * @param attribute independent attribute on which the split value is defined
     */

    DiscreteNode(Data trainingSet, int beginExampleIndex,
	    int endExampleIndex, DiscreteAttribute attribute) {
	super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    /**
     * Instances a <code>SlitInfo</code> objects with a discrete value relative
     * to a training subset where the split is defined
     *
     * @param trainingSet current training set
     * @param beginExampleIndex first extreme of the training subset
     * @param endExampleIndex last extreme of the training subset
     * @param attribute independent attribute on which the split value is defined
     */

    protected void setSplitInfo(Data trainingSet, int beginExampleIndex,
	    int endExampleIndex, Attribute attribute) {

	Object currentSplitValue, nextSplitValue = null;

	int beginSplit = beginExampleIndex;
	int child = 0;
	// determina quando varia il valore in 'attribute'

	for (int i = beginExampleIndex; i < endExampleIndex; i++) {

	    currentSplitValue = trainingSet.getExplanatoryValue(i,
		    attribute.getIndex());
	    nextSplitValue = trainingSet.getExplanatoryValue(i + 1,
		    attribute.getIndex());

	    if (!currentSplitValue.equals(nextSplitValue)) {
		mapSplit.add(new SplitInfo(currentSplitValue, beginSplit, i,
			child));
		beginSplit = i + 1;
		child++;
	    }
	}

	mapSplit.add(new SplitInfo(nextSplitValue, beginSplit, endExampleIndex,
		child));
	//child++;
    }

    /**
     * Controls the input value with the all split in <code>mapSplit</code> and returns the value which passes the test
     *
     * @param value Controlled value
     * @return value value which passes the test
     * @throws UnknownValueException if a wrong value s found
     */

    /*public int testCondition(Object value) throws UnknownValueException {

	for (int i = 0; i < this.mapSplit.size(); i++) {
	    if (this.mapSplit.get(i).getSplitValue().equals(value) == true) {
		return i;
	    }
	}
	throw new UnknownValueException();
    }*/

    /**
     * Returns the string representations of node which contains all information about it
     *
     * @return the string representations of node which contains all information about it
     */

    public String toString() {

	return "[DISCRETE] " + super.toString();
    }
}
