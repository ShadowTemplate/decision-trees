package com.mapgroup.classificator.utility;

import com.mapgroup.to.PredSessionTO;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Prints on a pdf file the specific data recorded during the user
 * prediction session.
 * <p>
 * In the report will be present, first of all, all the choices
 * that the user has selected during the prediction session and also
 * there will be the predicted value that come from the user's choices.
 * At the end will be reported the decision tree generated from the
 * selected dataset.
 * </p>
 * <p/>
 * <p>
 * Standing to the <a href=http://pdfbox.apache.org/apidocs/>Apache pdfbox library</a>
 * the pdf page are structured in this way:
 * <p>
 * (0, 0) is the bottom-left corner of the pdf page (0, 780) is the
 * top-left corner of the pdf page (600, 0) is the bottom-right corner of
 * the pdf page (600, 780) is the top-right corner of the pdf page.
 * </p>
 */
public class PdfCreator {
    /**
     * The maximum value for the y-axis
     */
    private static final int MAX_Y = 780; // Max value for the y-axis
    /**
     * The maximum value for the x-axis
     */
    private static final int MAX_X = 600; // Max value for the x-axis
    /**
     * Current position on the x-axis
     */
    private static int xPos;
    /**
     * Current position on the y-axis
     */
    private static int yPos;

    /**
     * Dimension of the normal text font
     */
    private static final int textDim = 11;
    /**
     * Dimension of the title font
     */
    private static final int titleDim = 15;
    /**
     * Dimension of the title for each sub-title
     */
    private static final int paragraphDim = 13;
    /**
     * Specific font for the normal text
     */
    private static final PDFont textFont = PDType1Font.TIMES_ROMAN;
    /**
     * Specific font for the title
     */
    private static final PDFont titleFont = PDType1Font.TIMES_BOLD;
    /**
     * Specific font for the subtitle
     */
    private static final PDFont paragraphFont = PDType1Font.TIMES_BOLD_ITALIC;

    /**
     * Current dimension of the printed text
     */
    private static int currFontDim = titleDim;

    /**
     * Stream of data associated with the current page of the pdf document
     */
    private static PDPageContentStream contentStream;
    /**
     * The pdf document itself
     */
    private static PDDocument document;


    private PdfCreator() {
    }

    /**
     * Controls if the current page has another printable
     * line.
     *
     * @return <code>true</code>  if there is enough space, <code>false</code> otherwise
     */
    private static boolean hasNextLine() {
        return !((yPos - currFontDim) < 20);
    }

    /**
     * Insert on the current <code>contentStream</code> a number
     * of newlines equal to <code>numLines</code> value.
     * <p>
     * If the content stream was associated to a page which hasn't
     * got enough lines, a new page was created in order to satisfy
     * the request of the user.
     * </p>
     *
     * @param numLines number of newlines that will be inserted in the page
     * @throws IOException invalid operation on the pdf document
     *
     * */
    private static void newLines(int numLines) throws IOException {
        for (int i = 0; i < numLines; i++) {
            if (!hasNextLine()) {
                contentStream.close();

                PDPage newPage = new PDPage();
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                yPos = MAX_Y - 15;
            } else {
                // goes on the next line
                yPos -= currFontDim;
            }

        }
    }

    /**
     * Writes a subtitle to the current page which has as text
     * the <code>title</code> value.
     * @param title the subtitle text
     * @throws IOException invalid operation on the pdf document
     *
     * */
    private static void addParagraphTitle(String title) throws IOException {
        currFontDim = paragraphDim;
        contentStream.beginText();
        contentStream.setFont(paragraphFont, currFontDim);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.moveTextPositionByAmount(xPos, yPos);
        contentStream.drawString(title);
        contentStream.endText();
    }

    /**
     * Writes a string on the current <code>contentStream</code> equals to
     * <code>msg</code> using as font <code>f</code> and color <code>c</code>
     *
     *
     * @param c text color
     * @param msg the text that will be printed
     * @throws IOException  if some invalid operations were done on the <code>contentStream</code>
     */
    private static void writeText(Color c, String msg)
            throws IOException {
        contentStream.beginText();
        contentStream.setFont(PdfCreator.textFont, currFontDim);
        contentStream.setNonStrokingColor(c);
        contentStream.moveTextPositionByAmount(xPos, yPos);
        contentStream.drawString(msg);
        contentStream.endText();
    }

    /**
     * Creates a pdf report of the prediction session information, which are contained in the
     * <code>tracker</code> structure.
     * The resulting pdf document will be saved in a file with name <code>pdfFileName</code>.
     *
     * @param tracker   user prediction session information
     * @param pdfFileName complete pdf filename
     * @throws IOException if some invalid operations were done on the <code>contentStream</code>
     * @throws COSVisitorException  if something gone wrong when visiting a PDF object.
     */
    public static void createReport(PredSessionTO tracker, String pdfFileName)
            throws IOException, COSVisitorException {
        xPos = 10;
        yPos = MAX_Y - 15;

        // Create a new pdf document model and add a page to it
        document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        /*
         Start a content stream, which will "hold" what will be written in the
         pdf
        */
        contentStream = new PDPageContentStream(document, page);

        // Add the title
        contentStream.beginText();
        contentStream.setFont(titleFont, currFontDim);
        contentStream.setNonStrokingColor(Color.RED);
        // Center the title
        contentStream.moveTextPositionByAmount(MAX_X / 2 - 38, yPos);
        contentStream.drawString("Prediction Report");
        contentStream.endText();

        // Add the first paragraph title
        newLines(3);
        addParagraphTitle("User choices:");

        // Add the first paragraph body
        newLines(2);
        currFontDim = textDim;
        HashMap<String, Integer> map = tracker.getTracker();
        Set<String> var = map.keySet();
        String[] keys = var.toArray(new String[var.size()]);
        for (String key : keys) {
            contentStream.beginText();
            contentStream.setFont(textFont, currFontDim);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.moveTextPositionByAmount(xPos, yPos);

            // Controls if the keys[i] length exceed the page width
            if (key.length() < 53) {
                contentStream.drawString("Options: " + key
                        + " // Choice = " + map.get(key));
                contentStream.endText();
            } else {  // Split the current string on multiple lines
                String[] lines = key.split("(?<=\\G.{53})");

                contentStream.drawString("Options:");
                contentStream.endText();
                newLines(1);
                writeText(Color.BLACK, lines[0]);

                for (int j = 1; j < lines.length - 1; j++) {
                    newLines(1);
                    writeText(Color.BLACK, lines[j]);
                }

                if (lines.length > 1) {
                    newLines(1);
                    writeText(Color.BLACK, lines[lines.length - 1]);
                }

                newLines(1);
                writeText(Color.BLACK,
                        "// Choice = " + map.get(key));
            }

            newLines(1);
        }

        // Add the second paragraph title
        newLines(3);
        addParagraphTitle("Predicted value:");

        // Add the second paragraph body
        newLines(2);
        currFontDim = textDim;
        writeText(Color.BLUE, tracker.getPredictedValue());

        // Add the third paragraph title
        newLines(4);
        addParagraphTitle("Decision tree:");

        // Add the third paragraph body
        newLines(2);
        currFontDim = textDim;
        String[] treeStr = tracker.getTree().split("\n");
        for (String s : treeStr) {
            if (s.length() < 115) {
                writeText(Color.BLACK, s);
                newLines(1);
            } else {
                String[] lines = s.split("(?<=\\G.{115})");

                writeText(Color.BLACK, lines[0]);
                newLines(1);

                for (int j = 1; j < lines.length - 1; j++) {
                    writeText(Color.BLACK, lines[j]);
                    newLines(1);
                }

                if (lines.length > 1) {
                    writeText(Color.BLACK, lines[lines.length - 1]);
                    newLines(1);
                }
            }
        }

        // Make sure that the content stream is closed:
        contentStream.close();

        // Save the results and ensure that the document is properly closed:
        document.save(pdfFileName);
        document.close();

    }
}
