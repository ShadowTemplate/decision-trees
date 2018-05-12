package com.mapgroup.classificator.filemanager.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import com.mapgroup.classificator.filemanager.RequestStruct;

import java.util.List;
import java.util.ArrayList;

/**
 * This class grant the possibility to read the dataset
 * from a textual file(extension '.txt').
 * <p>
 * The read file must be in the following format:
 * <ul>
 * <li>attributes names on the first line </li>
 * <li>type name on the second line</li>
 * <li>dataset data on the rest of the file</li>
 * </ul>
 * </p>
 * */
class TxtReader extends DataReader {

    /**
     * Reads all the information about the attributes' type
     * and names from the specified textual file whose name is
     * contained in the passed parameter, <code>rs</code>.
     *
     * @param rs specific information about the current request
     * @throws Exception - if an invalid file is specified
     */
    public void initPopulator(RequestStruct rs) throws Exception {
        //typeList attributesNames
        BufferedReader in = new BufferedReader(new FileReader((String) rs.getAttribute(0)));

        // first line attributes names
        attributeNames.addAll(Arrays.asList(in.readLine().split(" ")));

        // second line type name
        typeList.addAll(Arrays.asList(in.readLine().split(" ")));
        
        for (String t : typeList) {
            boolean errorFlag = true;
            for (String legalType : legalTypes) {
                if (legalType.equalsIgnoreCase(t))
                    errorFlag = false;
            }
            if (errorFlag) {
                in.close();
                throw new IllegalTypeException("( " + t + " ) is considered an invalid dataset for the TxtReader.");
            }
        }


        in.close();
    }


    /**
     * Creates the structured dataset format from the specified file.
     * <p>
     * During the reading process empty lines will be ignored and all tuples'
     * values must be divided by a comma.
     * </p>
     *
     * @param rs specific information about the current request
     * @return structured dataset in matrix format
     * @throws Exception - if some errors occurred in the reading process
     */
    public Object[][] tupleFactory(RequestStruct rs) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader((String) rs.getAttribute(0)));
        List<String> lines = new ArrayList<String>();

        // ignore first two lines
        in.readLine();
        in.readLine();
        String currLine;

        while ((currLine = in.readLine()) != null) {
            if (!currLine.equals("")) {
                lines.add(currLine);
            }
        }

        // create the data matrix
        Object[][] data = new Object[lines.size()][typeList.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] thisLine = lines.get(i).split(",");
            for (int j = 0; j < thisLine.length; j++) {
                if (typeList.get(j).equals("String")) {
                    data[i][j] = thisLine[j];
                } else {
                    data[i][j] = Float.parseFloat(thisLine[j]);
                }
            }
        }

        in.close();

        return data;
    }

}
