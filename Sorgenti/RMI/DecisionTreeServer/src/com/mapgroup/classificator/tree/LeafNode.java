package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;

public class LeafNode extends Node {
    private final String predictedClassValue;

    public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        predictedClassValue = determineMostFrequentClass(trainingSet);
    }

    public String getPredictedClassValue() {

        return predictedClassValue;
    }

    private String determineMostFrequentClass(Data trainingSet) {
        DiscreteAttribute classAttr = trainingSet.getClassAttribute();
        String maxValue = null;
        int currMax = 0;
        String currString;
        int currFrequency;

        for (int i = 0; i < classAttr.getNumOfDistinctValues(); i++) {
            currString = classAttr.getValue(i);
            currFrequency = classValueAbsoluteFrequency.get(currString);

            if (currMax < currFrequency) {
                currMax = currFrequency;
                maxValue = currString;
            }
        }

        return maxValue;
    }

    public int getNumberOfChildren() {
        return 0;
    }

    public String toString() {
        return ("[Leaf] Class Value = " + predictedClassValue + " " + super.toString());
    }
}
