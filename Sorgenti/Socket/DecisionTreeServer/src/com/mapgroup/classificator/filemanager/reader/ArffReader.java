package com.mapgroup.classificator.filemanager.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.mapgroup.classificator.filemanager.RequestStruct;

/**
 * This class represents a specific way to read ARFF formatted file.
 */
class ArffReader extends DataReader {

    /**
     * Retrieves all the information about the attributes type
     * and the attributes name from the formatted ARFF file.
     * <p>
     * The @relation tag was simply ignored during the reading process
     * because was not useful to the learning tree process.
     * </p>
     * <p/>
     * <p>
     * For the continuos attribute no curly braces are specified
     * when they are described, in fact only the specific type was needed.
     * In this context 'numeric', 'float', and 'real' type will be mapped with
     * the 'Float' java type.
     * </p>
     *
     * @param rs specific information about the current request
     * @throws Exception
     */
    public void initPopulator(RequestStruct rs) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(
                (String) rs.getAttribute(0)));

        String str = in.readLine();
        while (!((str.contains("@attribute") || str.contains("@ATTRIBUTE")) && str
                .charAt(0) == '@')) {
            str = in.readLine();
        }

        String attrParam[];

        do {
            if (!str.equals("")) {
                // splits the current string considering as a separator all the whitespace characters
                attrParam = str.split("\\s+");
                attributeNames.add(attrParam[1]);

                String arffType = getAttributeType(attrParam[2]);
                String validType = (arffType.equalsIgnoreCase("numeric") || arffType.equalsIgnoreCase("REAL")) ? "Float" : "String";
                typeList.add(validType);
            }
            str = in.readLine();
        } while (!str.contains("@data") && !str.contains("@DATA"));

        for (String t : typeList) {
            boolean errorFlag = true;
            for (String legalType : legalTypes) {
                if (legalType.equalsIgnoreCase(t))
                    errorFlag = false;
            }
            if (errorFlag) {
                in.close();
                throw new IllegalTypeException("( " + t + " ) is considered an invalid dataset for the ArffReader.");
            }
        }

        in.close();
    }

    /**
     * Retrives the specific type name for the current
     * attribute analyzed.
     *
     * @param rawAttr raw type representation for the attribute
     * @return simple type name without any other useless character
     */
    private String getAttributeType(String rawAttr) {
        String type = "";
        char[] invalid = {' ', '{', '}'};
        for (int i = 0; i < rawAttr.length(); i++) {
            boolean skipChar = false;
            for (int j = 0; j < invalid.length && !skipChar; j++) {
                if (rawAttr.charAt(i) == invalid[j])
                    skipChar = true;
            }

            if (!skipChar) {
                type += rawAttr.charAt(i);
            }
        }

        return type;
    }

    /**
     * Generates the structured dataset parsing all the text which follows
     * the <code>@data</code> tag of the arff file.
     *
     * <p>
     * The method automatically skip possible empty lines and got only the
     * correct and formatted lines needed to construct the dataset.
     * </p>
     *
     * <p>
     * At the moment the '?' parameter that could be specified in order
     * to represent a non specified and arbitrary value for that attribute was
     * always replaced with '0'.
     * Aren't considered this kind of type in the learning process implemented.
     * </p>
     *
     * @param rs specific information about the current request
     * @return read dataset structured in a proper way
     * @throws Exception - if some errors ocurred in the reading process
     */
    public Object[][] tupleFactory(RequestStruct rs) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(
                (String) rs.getAttribute(0)));
        ArrayList<String> rawDataset = new ArrayList<String>();

        String str;

        // skips all the text which comes before the @data tag
        while (!((str = in.readLine()).equals("@data") || str.equals("@DATA")));

        String buffer;

        while ((buffer = in.readLine()) != null) {
            // ignores empty lines
            if (buffer.contains((","))) {
                rawDataset.add(buffer);
            }

        }
        // Number of attributes in the first row
        int numberOfAttributes = rawDataset.get(0).split(",").length;

        Object[][] data = new Object[rawDataset.size()][numberOfAttributes];
        // stores all the values in the matrix
        for (int i = 0; i < rawDataset.size(); i++) {
            String[] values = rawDataset.get(i).split(",");
            for (int j = 0; j < values.length; j++) {
                if (typeList.get(j).equals("Float")) {
                    if (values[j].equals("?")) {
                	in.close();
                	throw new Exception("Unsupported jolly character '?'");
                    }
                    data[i][j] = Float.parseFloat(values[j]);
                } else {
                    if (values[j].equals("?")){
                	in.close();
                	throw new Exception("Unsupported jolly character '?'");
                    }
                    data[i][j] = values[j];
                }
            }
        }

        in.close();

        return data;
    }

}
