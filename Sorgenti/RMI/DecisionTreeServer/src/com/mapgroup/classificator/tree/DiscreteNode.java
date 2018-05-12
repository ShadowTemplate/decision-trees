package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;

public class DiscreteNode extends SplitNode {
    public DiscreteNode(Data trainingSet, int beginExampleIndex,
                        int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

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

    }

    public int testCondition(Object value) throws UnknownValueException {

        for (int i = 0; i < this.mapSplit.size(); i++) {
            if (this.mapSplit.get(i).getSplitValue().equals(value)) {
                return i;
            }
        }
        throw new UnknownValueException();
    }

    public String toString() {

        return "[DISCRETE] " + super.toString();
    }
}
