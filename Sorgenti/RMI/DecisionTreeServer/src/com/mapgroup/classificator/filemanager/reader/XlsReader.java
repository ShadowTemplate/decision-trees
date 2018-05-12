package com.mapgroup.classificator.filemanager.reader;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.mapgroup.classificator.filemanager.RequestStruct;

/**
 * This class grant the possibility to read the dataset information
 * from an Excel file thanks to the <a href=http://poi.apache.org/apidocs/>Apache POI</a>  library functionality.
 */
class XlsReader extends DataReader {

    /**
     * Initializes the list of attribute's names and types from the
     * specified excel file contained in the parameter passed to the method.
     *
     * @param rs specific information about the current request
     * @throws Exception - if some errors occurred while reading the excel file
     */
    public void initPopulator(RequestStruct rs) throws Exception { // rs
        FileInputStream in = new FileInputStream((String) rs.getAttribute(0));
        HSSFWorkbook wb = new HSSFWorkbook(in);

        // Parse only the first sheet of the file
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFRow currRow;
        HSSFCell currCell;

        // Get the name of the attributes
        currRow = sheet.getRow(0);

        for (int col = 0; col < currRow.getPhysicalNumberOfCells(); col++) {
            currCell = currRow.getCell(col);
            attributeNames.add(currCell.getStringCellValue());
        }

        // Get the name of the types
        currRow = sheet.getRow(1);

        for (int col = 0; col < currRow.getPhysicalNumberOfCells(); col++) {
            currCell = currRow.getCell(col);
            typeList.add(currCell.getStringCellValue());
        }

        for (String t : typeList) {
            boolean errorFlag = true;
            for (String legalType : legalTypes) {
                if (legalType.equalsIgnoreCase(t))
                    errorFlag = false;
            }
            if (errorFlag) {
                throw new IllegalTypeException();
            }
        }

        in.close();

    }

    /**
     * Creates the structured format for the dataset read from the specified excel file
     * contained in the parameter passed to the method.
     *
     * <p>
     * Only the first sheet of the excel file will be analyzed during the reading process.
     * The specific format of the Excel is described here:
     * <ul>
     * <li> the first row will contain the attributes' name</li>
     * <li> the second row will contain the attributes' type</li>
     * <li> the rest of the file will contain all the dataset values</li>
     * </ul>
     * </p>
     *
     * @param rs specific information about the current request
     * @return the list of tuples
     * @throws Exception
     */
    public Object[][] tupleFactory(RequestStruct rs) throws Exception {

        FileInputStream in = new FileInputStream((String) rs.getAttribute(0));
        HSSFWorkbook wb = new HSSFWorkbook(in);

        HSSFSheet sheet = wb.getSheetAt(0); // Parse only the first sheet of the
        // file
        HSSFRow currRow;
        HSSFCell currCell;

        int numberOfRows = sheet.getPhysicalNumberOfRows();
        int numberOfColumns = sheet.getRow(2).getPhysicalNumberOfCells();
        Object[][] data = new Object[numberOfRows - 2][numberOfColumns];

        // The 1st row contains the name of the attributes;
        // the 2nd row contains the type of the attributes
        for (int row = 2; row < numberOfRows; row++) {
            currRow = sheet.getRow(row); // read the current row
            if (currRow == null)
                continue; // skip null rows
            int numberOfCell = currRow.getPhysicalNumberOfCells();

            for (int col = 0; col < numberOfCell; col++) {
                currCell = currRow.getCell(col); // read the current cell

                switch (currCell.getCellType()) {
                    case HSSFCell.CELL_TYPE_STRING:
                        data[row - 2][col] = currCell.getStringCellValue();
                        break;
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        data[row - 2][col] = (float) currCell.getNumericCellValue();
                        break;
                    default:
                        throw new IllegalTypeException(
                                "Not valid value found in cell (" + row + ") ("
                                        + col + ")");
                }
            }
        }

        in.close();
        return data;
    }
}
