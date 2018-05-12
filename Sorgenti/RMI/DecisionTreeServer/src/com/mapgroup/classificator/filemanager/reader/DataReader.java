package com.mapgroup.classificator.filemanager.reader;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import com.mapgroup.classificator.data.Attribute;
import com.mapgroup.classificator.data.ContinuousAttribute;
import com.mapgroup.classificator.data.DiscreteAttribute;
import com.mapgroup.classificator.filemanager.RequestStruct;

/**
 * This class represents the way to populate and create the whole
 * system to manage the reading dataset.
 * <p>
 * This class is an abstract class which shares some  methods which are
 * fundamental in order to retrieve all the main aspects of the learning tree
 * process such as the independent attribute and the dependent attribute, so called
 * class attribute.
 * </p>
 *
 * <p>
 * From the formatted dataset file, this class provide a way to collect all the types and
 * values for each the independent and dependent attribute.
 * </p>
 *
 */
public abstract class DataReader {
    /** attribute's type list */
    protected final List<String> typeList = new LinkedList<String>();
    /** attribute's name list */
    protected final List<String> attributeNames = new LinkedList<String>();
    /** list of all the supported and valid attribute's type */
    protected static final String[] legalTypes = new String[]{"Float", "String"};

    /**
     * Initializes all the main instruments needed to this class in order to start the
     * dataset reading process.
     * <p>
     * A supplementary  parameter like <code>rs</code> was needed in order to get all the information about
     * the file that contains the dataset.
     * </p>
     *
     * @param rs specific information about the current request
     * @throws Exception - if some errors occurred in reading the file or in initializing the type's list
     */
    public abstract void initPopulator(RequestStruct rs) throws Exception;

    /**
     * Reads from the formatted file that contains the dataset all the information and
     * stores them in a proper way within a bidimensional structure.
     * <p>
     * The structure <code>rs</code> also in this case, represents all the information
     * about the request that is done and the context in which this one is done.
     * </p>
     *
     * @param rs specific information about the current request
     * @return dataset read
     * @throws Exception - if some errors occurred during the dataset reading process
     */
    public abstract Object[][] tupleFactory(RequestStruct rs) throws Exception;

    /**
     * Creates the class attribute property starting from the dataset generated
     * contained in the bidimensional structure passed to the method.
     *
     * @param data structured dataset
     * @return the discrete class attribute
     */
    public final DiscreteAttribute retrieveClassValue(Object[][] data) {
        TreeSet<String> values = new TreeSet<String>();

        for (Object[] aData : data) {
            values.add((String) aData[data[0].length - 1]);
        }

        return new DiscreteAttribute(
                attributeNames.get(attributeNames.size() - 1),
                data[0].length - 1, values.toArray(new String[values.size()]));
    }

    /**
     * Retrieves from the structured dataset passed to the method,
     * all the information and property of all the independent attributes
     * that will be used in the learning process.
     *
     * <p>
     * The list of attributes could be composed of both discrete attribute
     * or continuous attribute depending on the information contained in the
     * <code>typeList</code>.
     * </p>
     *
     * @param data the structured dataset
     * @param explanatorySet list of all the independent attributes
     */
    public void retrieveIndependentAttribute(Object[][] data,
                                             List<Attribute> explanatorySet) {

        // foreach attribute in the dataset.
        for (int col = 0; col < data[0].length - 1; col++) {
            if (isDiscreteType(typeList.get(col))) {
                TreeSet<String> values = new TreeSet<String>();

                for (Object[] aData : data) {
                    values.add((String) aData[col]);
                }
                explanatorySet.add(new DiscreteAttribute(attributeNames
                        .get(col), col, values.toArray(new String[values.size()])));

            } else {
                explanatorySet.add(new ContinuousAttribute(attributeNames
                        .get(col), col));
            }
        }
    }

    /**
     * Controls if the specified attribute type,<code>typeName</code>
     * is a discrete attribute type or not.
     *
     * <p>
     * Conventionally, a discrete attribute represents an attribute which
     * can assume only a specific number of possible values.
     * </p>
     * <p>
     * A continuous attribute is represented by an attribute whose values
     * are real numbers.
     * </p>
     *
     * @param typeName the current attribute type
     * @return <code>true</code> if the <code>typeName</code> is associated to a discrete attribute, <code>false</code> otherwise
     */
    private boolean isDiscreteType(String typeName) {
        return (typeName.equals("String"));
    }
}
