package com.mapgroup.classificator.tree;

import com.mapgroup.classificator.data.Data;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Node implements Serializable {
    protected final int beginExampleIndex;
    protected final int endExampleIndex;
    protected final HashMap<String, Integer> classValueAbsoluteFrequency;
    protected float entropy;

    protected Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        classValueAbsoluteFrequency = new HashMap<String, Integer>();

        // mantiene gli indici di inizio-fine per la partizione dati
        // compresa tra beginExampleIndex e endExampleIndex
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;

        // aggiorna classValueAbsoluteFrequency in maniera che per ciascun valore distinto di classe sia mantenua la frequenza della classe nella partiziane dati
        // compresa tra beginExampleIndex e endExampleIndex
        for (int i = 0; i < trainingSet.getClassAttribute().getNumOfDistinctValues(); i++) {
            classValueAbsoluteFrequency.put(trainingSet.getClassAttribute().getValue(i), 0);
        }

        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            String classValue = trainingSet.getClassValue(i);
            classValueAbsoluteFrequency.put(classValue, classValueAbsoluteFrequency.get(classValue) + 1);
        }


        // calcola il valore di entropia per la partizione dati
        // compresa tra beginExampleIndex e endExampleIndex
        entropy = 0;

        int numberOfExamples = endExampleIndex - beginExampleIndex + 1;
        for (Integer v : classValueAbsoluteFrequency.values()) {

            if (v != 0) {
                float p = ((float) v) / numberOfExamples;
                entropy += (-p * Math.log10(p) / Math.log10(2));
            }
        }
    }

    public int getBeginExampleIndex() {
        return beginExampleIndex;
    }

    public int getEndExampleIndex() {
        return endExampleIndex;
    }

    public float getEntropy() {
        return entropy;
    }

    public abstract int getNumberOfChildren();

    public String toString() {
        return "Node: [Example:" + getBeginExampleIndex() + "-" + getEndExampleIndex() + "] entropy:" + getEntropy() + " ";
    }
}
