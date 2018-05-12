package com.mapgroup.classificator.filemanager.writer;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.data.DiscreteAttribute;
import com.mapgroup.classificator.filemanager.RequestStruct;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Represents a way in order to generate a dataset
 * representation using the Excel documents' format.
 * <p/>
 * Each dataset will be saved in the excel document in a single
 * sheet which will be divided in this way:
 * <ul>
 * <li> attributes' name </li>
 * <li> attributes' type </li>
 * <li> dataset's content </li>
 * </ul>
 */

class XlsWriter implements IDataWriter {

    /**
     * Writes the dataset, contained in <code>rs</code>, to file with xls extension, whose
     * name is contained in the specified struct.
     *
     * @param rs structure which contains the file path and the data set that will be contain in the file
     * @throws IOException - if some errors occurred processing the file
     */

    public void write(RequestStruct rs) throws IOException {

        // retrieves file path of the xls document
        FileOutputStream out = new FileOutputStream((String) rs.getAttribute(0));

        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet("Dataset");
        HSSFRow currRow;
        HSSFCell currCell;

        // Gets data set
        Data dataSet = (Data) rs.getAttribute(1);
        LinkedList<String> type = new LinkedList<String>();

        //Writes the attributes' name and at the end the class attribute's name
        currRow = sheet.createRow(0);
        for (int col = 0; col < dataSet.getNumberOfExplanatoryAttributes(); col++) {
            currCell = currRow.createCell(col);
            currCell.setCellValue(dataSet.getExplanatoryAttribute(col).toString());
        }

        currCell = currRow.createCell(dataSet.getNumberOfExplanatoryAttributes());
        currCell.setCellValue(dataSet.getClassAttribute().toString());

        for (int i = 0; i < dataSet.getNumberOfExplanatoryAttributes(); i++) {
            if (dataSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute)
                type.add("String");
            else
                type.add("Float");
        }

        //validity check of the attributes' type
        if (dataSet.getClassAttribute() instanceof DiscreteAttribute)
            type.add("String");
        else
            type.add("Float");

        // writes the attributes' type
        currRow = sheet.createRow(1);
        for (int i = 0; i < type.size(); i++) {
            currCell = currRow.createCell(i);
            currCell.setCellValue(type.get(i));
        }

        //writes the dataset's content
        for (int row = 2; row < dataSet.getNumberOfExamples() + 2; row++) {
            currRow = sheet.createRow(row);
            for (int col = 0; col < dataSet.getNumberOfExplanatoryAttributes(); col++) {
                currCell = currRow.createCell(col);

                if (type.get(col).equals("String")) {
                    currCell.setCellValue(dataSet.getExplanatoryValue(row - 2, col).toString());
                } else {
                    currCell.setCellValue((Float) dataSet.getExplanatoryValue(row - 2, col));
                }
            }
            currCell = currRow.createCell(dataSet.getNumberOfExplanatoryAttributes());
            currCell.setCellValue(dataSet.getClassValue(row - 2));
        }

        wb.write(out);
        out.close();
    }
}