package com.mapgroup.classificator.data;

import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.filemanager.RequestStruct;
import com.mapgroup.classificator.filemanager.reader.DataReader;
import com.mapgroup.classificator.filemanager.reader.DataReaderFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that models the set of training examples
 */
public class Data {

    /**
     * N*M matrix of type object that contains the training set.
     * <p>The training set is organized as (number of examples) * (number of attributes)</p>
     */
    private Object data[][];

    /**
     * cardinality of the training set
     */
    private int numberOfExamples;

    /**
     * List of <code>Attribute</code> to represent the attributes
     */
    private List<Attribute> explanatorySet;

    /**
     * Class attribute is a <code>DiscreteAttribute</code>
     */
    private DiscreteAttribute classAttribute;

    /**
     * Initializes this training set using a formatted file with name fileName
     * <p>The supported extensions of file are .txt, .arff, .xls</p>
     *
     * @param fileName is the name of formatted file
     * @throws DataException is launched when there are an error to create data set
     */

    public Data(String fileName) throws DataException {

        try {
            RequestStruct rs = new RequestStruct(fileName);
            DataReader reader = DataReaderFactory.getDataReader(rs);

            reader.initPopulator(rs);
            data = reader.tupleFactory(rs);
            numberOfExamples = data.length;

            explanatorySet = new LinkedList<Attribute>();
            reader.retrieveIndependentAttribute(data, explanatorySet);
            classAttribute = reader.retrieveClassValue(data);


        } catch (Exception e) {
            throw new DataException(e.getMessage());
        }

    }

    /**
     * Constructs this object with the classical playtennis example dataset
     */

     /*public Data() {

        // data

        data = new Object[14][5];
        data[0][0] = "sunny";
        data[1][0] = "sunny";
        data[2][0] = "overcast";
        data[3][0] = "rain";
        data[4][0] = "rain";
        data[5][0] = "rain";
        data[6][0] = "overcast";
        data[7][0] = "sunny";
        data[8][0] = "sunny";
        data[9][0] = "rain";
        data[10][0] = "sunny";
        data[11][0] = "overcast";
        data[12][0] = "overcast";
        data[13][0] = "rain";

        data[0][1] = new Float(30.3);
        data[1][1] = new Float(30.3);
        data[2][1] = new Float(30);
        data[3][1] = new Float(13);
        data[4][1] = new Float(0);
        data[5][1] = new Float(0);
        data[6][1] = new Float(0.1);
        data[7][1] = new Float(13);
        data[8][1] = new Float(0.1);
        data[9][1] = new Float(12);
        data[10][1] = new Float(12.5);
        data[11][1] = new Float(12.5);
        data[12][1] = new Float(29.21);
        data[13][1] = new Float(12.5);

        data[0][2] = "high";
        data[1][2] = "high";
        data[2][2] = "high";
        data[3][2] = "high";
        data[4][2] = "normal";
        data[5][2] = "normal";
        data[6][2] = "normal";
        data[7][2] = "high";
        data[8][2] = "normal";
        data[9][2] = "normal";
        data[10][2] = "normal";
        data[11][2] = "high";
        data[12][2] = "normal";
        data[13][2] = "high";

        data[0][3] = "weak";
        data[1][3] = "strong";
        data[2][3] = "weak";
        data[3][3] = "weak";
        data[4][3] = "weak";
        data[5][3] = "strong";
        data[6][3] = "strong";
        data[7][3] = "weak";
        data[8][3] = "weak";
        data[9][3] = "weak";
        data[10][3] = "strong";
        data[11][3] = "strong";
        data[12][3] = "weak";
        data[13][3] = "strong";

        data[0][4] = "no";
        data[1][4] = "no";
        data[2][4] = "yes";
        data[3][4] = "yes";
        data[4][4] = "yes";
        data[5][4] = "no";
        data[6][4] = "yes";
        data[7][4] = "no";
        data[8][4] = "yes";
        data[9][4] = "yes";
        data[10][4] = "yes";
        data[11][4] = "yes";
        data[12][4] = "yes";
        data[13][4] = "no";

        // numberOfExamples

        numberOfExamples = 14;

        // explanatory Set

        explanatorySet = new LinkedList<Attribute>();

        String outLookValues[] = new String[3];
        outLookValues[0] = "overcast";
        outLookValues[1] = "rain";
        outLookValues[2] = "sunny";
        explanatorySet.add(new DiscreteAttribute("Outlook", 0, outLookValues));

        explanatorySet.add(new ContinuousAttribute("Temperature", 1));

        String umidityValues[] = new String[2];
        umidityValues[0] = "high";
        umidityValues[1] = "normal";
        explanatorySet.add(new DiscreteAttribute("Umidity", 2, umidityValues));

        String windValues[] = new String[2];
        windValues[0] = "strong";
        windValues[1] = "weak";
        explanatorySet.add(new DiscreteAttribute("Wind", 3, windValues));

        // classAttribute

        String playValues[] = new String[2];
        playValues[0] = "no";
        playValues[1] = "yes";
        classAttribute = new DiscreteAttribute("PlayTennis", 4, playValues);

    }*/

    /**
     * Returns the cardinality of the training set
     *
     * @return number of examples
     */

    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    /**
     * Returns the cardinality of the attributes set
     *
     * @return number of explanatory attribute
     */

    public int getNumberOfExplanatoryAttributes() {
        return explanatorySet.size();
    }

    /**
     * Returns the value of the class <code>Attribute</code> for the example indexed by input
     *
     * @param exampleIndex row index for the matrix <code>data</code> to a specific example
     * @return value of the class attribute for the exampleIndex
     */

    public String getClassValue(int exampleIndex) {
        return (String) data[exampleIndex][classAttribute.getIndex()];
    }

    /**
     * Returns the value of attribute indexed by <code>attributeIndex</code> for the example <code>exampleIndex</code>
     *
     * @param exampleIndex   row index for the matrix <code>data</code> to a specific example
     * @param attributeIndex index of specific attribute indexed by it in explanatory set
     * @return object associated to independent attribute for the example indexed by input
     */

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
        return data[exampleIndex][explanatorySet.get(attributeIndex).getIndex()];
    }

    /**
     * Returns the attribute's value indexed by index in explanatorySet
     *
     * @param index index of explanatorySet for a specific independent attribute
     * @return object <code> Attribute </code> indexed by index
     */

    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet.get(index);
    }

    /**
     * Returns the object that correspond to class <code>Attribute</code>
     *
     * @return object <code>DiscreteAttribute</code> associated to class attribute
     */

    public DiscreteAttribute getClassAttribute() {
        return classAttribute;
    }

    /**
     * Takes a values of all attribute for each example in <code>data</code> and put them in a <code>String</code>
     *
     * @return the String that represent the values of attributes
     */

    @Override
    public String toString() {
        String value = "";
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < explanatorySet.size(); j++) {
                value += data[i][j] + ",";
            }

            value += data[i][explanatorySet.size()] + "\n";
        }
        return value;

    }

    /**
     * Orders the subset of examples in the range [<code>beginExampleIndex</code>][<code>endExampleIndex</code>] on <code>data</code>
     * with respect to the specific attribute
     *
     * @param attribute         attribute values ​​which have to be ordered
     * @param beginExampleIndex value of example where the order start
     * @param endExampleIndex   value of example where the order end
     */

    public void sort(Attribute attribute, int beginExampleIndex,
                     int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }


    /**
     * Quick sort algorithm for ordering the tuples contained in the subset(<code>inf</code>, <code>sup</code>)
     * using the proper order relationship of the specified attribute, <code>attribute</code>
     *
     * @param attribute attribute values ​​which have to be ordered
     * @param inf       value of example where the order start
     * @param sup       value of example where the order end
     */

    private void quicksort(Attribute attribute, int inf, int sup) {

        if (sup >= inf) {

            int pos;
            if (attribute instanceof DiscreteAttribute) {
                pos = partition((DiscreteAttribute) attribute, inf, sup);
            } else {
                pos = partition((ContinuousAttribute) attribute, inf, sup);
            }

            if ((pos - inf) < (sup - pos + 1)) {
                quicksort(attribute, inf, pos - 1);
                quicksort(attribute, pos + 1, sup);
            } else {
                quicksort(attribute, pos + 1, sup);
                quicksort(attribute, inf, pos - 1);
            }
        }
    }

    /**
     * Partitions the tuples with discrete value contained in the subset(<code>inf</code>, <code>sup</code>) and returns the point of separation
     *
     * @param attribute attribute values ​​which have to be ordered
     * @param inf       value of example where the order start
     * @param sup       value of example where the order end
     * @return the index of the pivotal element
     */

    private int partition(DiscreteAttribute attribute, int inf, int sup) {
        int i, j;

        i = inf;
        j = sup;
        int med = (inf + sup) / 2;
        String x = (String) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {
            while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0)
                i++;

            while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0)
                j--;

            if (i < j)
                swap(i, j);
            else
                break;

        }

        swap(inf, j);
        return j;

    }

    /**
     * Partitions the tuples with continuous value contained in the subset(<code>inf</code>, <code>sup</code>) and returns the point of separation
     *
     * @param attribute The attribute on which the sorting is being performed
     * @param inf Lower bound of the partition
     * @param sup Upper bound of the partition
     * @return the index of the pivotal element
     */

    private int partition(ContinuousAttribute attribute, int inf, int sup) {
        int i, j;

        i = inf;
        j = sup;
        int med = (inf + sup) / 2;
        Float x = (Float) getExplanatoryValue(med, attribute.getIndex());
        swap(inf, med);

        while (true) {

            while (i <= sup
                    && ((Float) getExplanatoryValue(i, attribute.getIndex())) <= x) {
                i++;

            }

            while (((Float) getExplanatoryValue(j, attribute.getIndex())) > x) {
                j--;

            }

            if (i < j) {
                swap(i, j);
            } else {
                break;
            }
        }
        swap(inf, j);
        return j;

    }

    /**
     * Swapping of param <code>i</code> and <code>j</code>
     *
     * @param i first param
     * @param j second param
     */

    private void swap(int i, int j) {
        Object temp;
        for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++) {
            temp = data[i][k];
            data[i][k] = data[j][k];
            data[j][k] = temp;
        }

    }

	/*
     * public static void main(String args[]) { Data trainingSet = new Data();
	 * System.out.println(trainingSet);
	 * 
	 * System.out.println("ORDER BY " + trainingSet.getExplanatoryAttribute(0));
	 * trainingSet.quicksort(trainingSet.getExplanatoryAttribute(0), 0,
	 * trainingSet.getNumberOfExamples() - 1); System.out.println(trainingSet);
	 * 
	 * System.out.println("ORDER BY " + trainingSet.getExplanatoryAttribute(1));
	 * trainingSet.quicksort(trainingSet.getExplanatoryAttribute(1), 0,
	 * trainingSet.getNumberOfExamples() - 1); System.out.println(trainingSet);
	 * 
	 * System.out.println("ORDER BY " + trainingSet.getExplanatoryAttribute(2));
	 * trainingSet.quicksort(trainingSet.getExplanatoryAttribute(2), 0,
	 * trainingSet.getNumberOfExamples() - 1); System.out.println(trainingSet);
	 * 
	 * System.out.println("ORDER BY " + trainingSet.getExplanatoryAttribute(3));
	 * trainingSet.quicksort(trainingSet.getExplanatoryAttribute(3), 0,
	 * trainingSet.getNumberOfExamples() - 1); System.out.println(trainingSet);
	 * 
	 * 
	 * }
	 */
}
