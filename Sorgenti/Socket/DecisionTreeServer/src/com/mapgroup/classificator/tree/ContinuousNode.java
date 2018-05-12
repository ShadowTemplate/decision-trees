package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.ContinuousAttribute;
import com.mapgroup.classificator.data.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node that correspond to a continuous attribute
 */

class ContinuousNode extends SplitNode {

    /**
     * Instances an object with the superclass constructor with parameter <code>attribute</code>
     *
     * @param trainingSet current training set
     * @param beginExampleIndex first extreme of the training subset
     * @param endExampleIndex last extreme of the training subset
     * @param attribute independent attribute on which the split value is defined
     */

    ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);

    }

    /**
     * Instances a <code>SlitInfo</code> objects with a continuous value relative
     * to a training subset where the split is defined
     *
     * @param trainingSet current training set
     * @param beginExampleIndex first extreme of the training subset
     * @param endExampleIndex last extreme of the training subset
     * @param attribute independent attribute on which the split value is defined
     */
    
    protected void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        //Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
        Float currentSplitValue = (Float) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        float bestInfoGain = 0;
        List<SplitInfo> bestMapSplit = null;

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Float value = (Float) trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (value.floatValue() != currentSplitValue.floatValue()) {
                if (bestMapSplit == null) {
                    bestMapSplit = new ArrayList<SplitInfo>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                } else {
                    //compute entropy=sum_i{pi*E(i)} i=1..m ;m = number of classes
                    float splitEntropy = 0;
                    float p = ((float) ((i - 1) - beginExampleIndex + 1)) / (endExampleIndex - beginExampleIndex + 1);
                    float localEntropy = new LeafNode(trainingSet, beginExampleIndex, i - 1).getEntropy();
                    splitEntropy += (p * localEntropy);
                    p = ((float) (endExampleIndex - i + 1)) / (endExampleIndex - beginExampleIndex + 1);
                    localEntropy = new LeafNode(trainingSet, i, endExampleIndex).getEntropy();
                    splitEntropy += (p * localEntropy);
                    //compute info gain
                    infoGain = entropy - splitEntropy;
                    if (bestInfoGain < infoGain) {
                        bestInfoGain = infoGain;
                        bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
                        bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    }
                }
                currentSplitValue = value;
            }
        }
        mapSplit = bestMapSplit;
        //rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)

        if (mapSplit != null && (mapSplit.get(1).getBeginIndex() == mapSplit.get(1).getEndIndex())) {
            mapSplit.remove(1);

        }

    }

    /**
     * Controls the input value with split in <code>mapSplit</code> and returns the value which passes the test
     *
     * @param value Controlled value
     * @return value value which passes the test
     * @throws UnknownValueException if a wrong value is found
     */

    /*protected int testCondition(Object value) {
        if (((Float) (value)).floatValue() <= ((Float) mapSplit.get(0).getSplitValue()).floatValue()) {
            return 0;
        } else {
            return 1;
        }
    }*/

    /**
     * Returns the string representations of node which contains all information about it
     *
     * @return the string representations of node which contains all information about it
     */
    public String toString() {
        return "[CONTINUOUS] " + super.toString();

    }
}
