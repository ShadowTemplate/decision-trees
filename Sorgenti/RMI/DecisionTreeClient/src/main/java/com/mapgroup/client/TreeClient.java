package com.mapgroup.client;

import com.mapgroup.client.utility.*;
import com.mapgroup.to.*;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Main class for the client-side of the application, by which the user can
 * query its server with an applet or a desktop application.
 * <p/>
 * This class provides all the services that the user need to communicate with
 * the server. The client can be execute both from a web page and from an
 * executable jar. It is possible to specify some parameters to properly set up
 * the connection with the server, or they can be ignored for default settings.
 * The class provides a GUI to make the interaction as intuitive and comfortable
 * as possible.
 */
public class TreeClient extends JApplet {
    /**
     * Main panel of the GUI
     */
    private JPanel predictionPanel;
    /**
     * Panel responsible of email sending services
     */
    private JPanel emailPanel;
    /**
     * Button to start the prediction
     */
    private JButton startPredictionBt;
    /**
     * Button to convert the last dataset used in a chosen format
     */
    private JButton saveAs;
    /**
     * Area where information on the tree is displayed
     */
    private JTextArea msgAreaTxt;
    /**
     * Field with the source name of the tree (e.g. database table, name of file)
     */
    private JTextField nameDataTxt;
    /**
     * Button to ask the server to build the tree for the prediction
     */
    private JButton treeConstructionBt;
    /**
     * Button to send the prediction parameters
     */
    private JButton predictBt;
    /**
     * Button to select a database table as source of the tree
     */
    private JRadioButton db;
    /**
     * Button to select a file of the server as source of the tree
     */
    private JRadioButton file;
    /**
     * Button to select a file from PC as source of the tree
     */
    private JRadioButton upload;
    /**
     * List of available dataset on the server
     */
    private JComboBox<String> availableFile;
    /**
     * Model for the list of available dataset on the server
     */
    private DefaultComboBoxModel<String> availableFileModel;
    /**
     * Button to update the list of available dataset on the server
     */
    private JButton updateFileList;
    /**
     * Label with the current selected source for the tree
     */
    private final JLabel chooserLabel = new JLabel("Table name: ");
    /**
     * Flag that states if the file chooser window can be displayed: <code>true</code> if the
     * user has chosen to upload a file, </code>false</code> otherwise
     */
    private boolean chooserFlag = false;
    /**
     * Number of the port of the server
     */
    private static int SERVER_PORT;
    /**
     * IP or DNS of the server
     */
    private static String SERVER_HOST;
    /**
     * Tracker of all the information of the last prediction
     */
    private PredSessionTO sessionTracker;
    /**
     * Flag that indicates if a dataset is present in the server: <code>false</code> if the
     * last prediction was on a server file, <code>true</code> otherwise
     */
    private boolean datasetCreated = false;
    /**
     * The remote interface exposed by the server
     */
    private IRemoteServer remoteServer;
    /**
     * The table in which are contained the selected attributes for the prediction
     */
    private JTable attributesTable;
    /**
     * The model associated to the table
     */
    private MyTableModel attributesTableModel;
    /**
     * Associative structure used in order to collect both the attributes and their values
     */
    private HashMap<String, String[]> attrValues;
    /**
     * Graphical component which represents the panel in which there are all the component needed for the prediction session
     */
    private JPanel cpPredictionQuery;
    /**
     * Graphical component which represents the panel in which is contained the attributes' table
     */
    private MyTablePanel tablePane;


    /**
     * Set-up the connection with server and start the client as a
     * desktop application.
     * <p/>
     * Check whether specified parameters are correct or not.
     * If they are valid starts the connection with the server and
     * provides the GUI for the user.
     * If they are incorrect a connection with the default settings
     * is started.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.err.println("Usage: java -jar DecisionTreeClient.jar [port]");
            System.exit(-1);
        }

        final TreeClient client = new TreeClient();

        if (args.length == 0) {
            client.initServerParameter("localhost", 2367);
        } else { // args.length == 1
            int userPort = Integer.parseInt(args[0]);
            if (userPort < 1024 || userPort > 49151) // Invalid port specified
            {
                System.err
                        .println("Invalid port specified. Default port will be used instead (port: 2367).");
                client.initServerParameter("localhost", 2367);
            } else {
                client.initServerParameter("localhost", userPort);
            }
        }
        client.initServerParameter("localhost", 2367);

        try {
            client.initConnection();
            JFrame clientFrame = new JFrame("Decision Tree Client");
            clientFrame.setSize(820, 550);
            clientFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            clientFrame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    client.closureRoutine();
                }
            });

            client.initGUI(clientFrame.getContentPane());
            clientFrame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to establish the connection with the server.\n"
                            + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NotBoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to establish the connection with the server.\n"
                            + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Set-up the connection with server and start the client as a
     * a web applet.
     * <p/>
     * Check whether specified parameters are correct or not.
     * If they are valid starts the connection with the server and
     * provides the GUI for the user.
     * If they are incorrect a connection with the default settings
     * is started.
     */
    public void init() {
        try {
            String port = this.getParameter("hostPort");
            if( port != null ){
                int portNumber = Integer.parseInt(port);
                if( portNumber < 1024 || portNumber > 49151 ){
                    System.err
                            .println("Invalid port specified. Default port will be used instead (port: 2367).");
                    initServerParameter("localhost", 2367);
                }else{
                    initServerParameter("localhost", portNumber);
                }
            }else{
                initServerParameter("localhost", 2367);
            }
            /*String host = this.getParameter("hostName"), port = this
                    .getParameter("hostPort");

            if (host != null && port != null) {
                int portNumber = Integer.parseInt(port);
                if (portNumber < 1024 || portNumber > 49151) // Invalid port
                // specified
                {
                    System.err
                            .println("Invalid port specified. Default port will be used instead (port: 2345).");
                    initServerParameter(host, 2367);
                } else {
                    initServerParameter(host, portNumber);
                }
            } else {
                initServerParameter("localhost", 2367);
            } */

            initConnection();
            initGUI(getContentPane());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to establish connection with server.\n "
                            + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NotBoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to establish connection with server.\n "
                            + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize some fields of the class with the parameters
     * necessary to set-up the connection with the server.
     *
     * @param host IP or DNS of the server
     * @param port number of the port for connection
     */
    private void initServerParameter(String host, int port) {
        SERVER_HOST = host;
        SERVER_PORT = port;
    }

    /**
     * Starts the connection with server, looking up for the server remote
     * object.
     *
     * @throws IOException - if occurred some errors in starting connection with server
     */
    private void initConnection() throws IOException, NotBoundException {

        remoteServer = (IRemoteServer) Naming.lookup("//" + SERVER_HOST + ":" + SERVER_PORT + "/MultiServer");
    }

    /**
     * Initialize the GUI for the user.
     *
     * @param cp Container in which all the elements will be drawn
     */
    private void initGUI(Container cp) {
        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

        predictionPanel = new JPanel();
        predictionPanel.setLayout(new BoxLayout(predictionPanel,
                BoxLayout.Y_AXIS));
        cp.add(predictionPanel);

        emailPanel = new MailPanel();
        cp.add(emailPanel);
        emailPanel.setVisible(false);

        JPanel cpTree = new JPanel();
        JPanel cpChooser = new JPanel();
        JPanel cpTreeConstruction = new JPanel();
        JPanel cpTreeInput = new JPanel();
        JPanel cpSendRequest = new JPanel();
        JPanel cpPrediction = new JPanel();
        JPanel cpStartPredicting = new JPanel();
        cpPredictionQuery = new JPanel();
        JPanel cpMessage = new JPanel();

        predictionPanel.add(cpTree);
        predictionPanel.add(cpPrediction);

        cpTree.add(cpChooser);
        cpTree.add(cpSendRequest);

        cpChooser.add(cpTreeConstruction);
        cpChooser.add(cpTreeInput);

        cpPrediction.add(cpStartPredicting);
        cpPrediction.add(cpPredictionQuery);
        cpPrediction.add(cpMessage);

        setBorderTitle(cpTree, "Tree Settings");
        setBorderTitle(cpTreeConstruction, "Tree Construction");
        setBorderTitle(cpTreeInput, "Input Parameters");
        setBorderTitle(cpPrediction, "Prediction Settings");
        setBorderTitle(cpStartPredicting, "Start prediction process");
        setBorderTitle(cpPredictionQuery, "Define the example to be predicted");
        setBorderTitle(cpMessage, "Message Area");

        cpTree.setLayout(new BoxLayout(cpTree, BoxLayout.Y_AXIS));
        cpChooser.setLayout(new FlowLayout());
        cpSendRequest.setLayout(new FlowLayout());
        cpTreeConstruction.setLayout(new BoxLayout(cpTreeConstruction,
                BoxLayout.Y_AXIS));
        cpTreeInput.setLayout(new FlowLayout());

        cpPrediction.setLayout(new BoxLayout(cpPrediction, BoxLayout.Y_AXIS));
        cpStartPredicting.setLayout(new FlowLayout());
        cpPredictionQuery.setLayout(new FlowLayout());
        cpMessage.setLayout(new FlowLayout());

        ButtonGroup group = new ButtonGroup();
        db = new JRadioButton("Learn tree from database");
        file = new JRadioButton("Learn tree from remote file");
        upload = new JRadioButton("Upload file to server");
        group.add(db);
        group.add(file);
        group.add(upload);
        db.setSelected(true);
        cpTreeConstruction.add(db);
        cpTreeConstruction.add(file);
        cpTreeConstruction.add(upload);
        RadioListener radioL = new RadioListener();
        db.addActionListener(radioL);
        file.addActionListener(radioL);
        upload.addActionListener(radioL);

        cpTreeInput.add(chooserLabel);
        nameDataTxt = new JTextField("", 20);
        nameDataTxt.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fileChooser_mouseClicked();
            }
        });
        treeConstructionBt = new JButton("Send request");
        cpTreeInput.add(nameDataTxt);
        treeConstructionBt.setIcon(new ImageIcon(loadImage("/gui/plane.png")));

        availableFileModel = new DefaultComboBoxModel<String>(getFileList());
        availableFile = new JComboBox<String>(availableFileModel);
        availableFile.setVisible(false);
        updateFileList = new JButton("Update list");
        updateFileList.setVisible(false);
        updateFileList.setIcon(new ImageIcon(loadImage("/gui/refresh.png")));
        updateFileList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] files = getFileList();
                availableFileModel.removeAllElements();
                for (String s : files) {
                    availableFileModel.addElement(s);
                }
                availableFile.setModel(availableFileModel);
            }
        });
        cpTreeInput.add(availableFile);
        cpTreeInput.add(updateFileList);

        cpSendRequest.add(treeConstructionBt);

        startPredictionBt = new JButton("Start Prediction");
        startPredictionBt.setEnabled(false);
        startPredictionBt.setIcon(new ImageIcon(loadImage("/gui/start.png")));
        cpStartPredicting.add(startPredictionBt);
        cpStartPredicting.add(new Box.Filler(new Dimension(10, 1), new Dimension(10, 1), new Dimension(10, 1)));
        saveAs = new JButton("Convert dataset in...");
        saveAs.setEnabled(false);
        saveAs.setIcon(new ImageIcon(loadImage("/gui/convert.png")));
        cpStartPredicting.add(saveAs);
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new FileConverter();
            }
        });

        tablePane = new MyTablePanel();
        predictBt = new JButton("Predict");
        predictBt.setEnabled(false);
        predictBt.setIcon(new ImageIcon(loadImage("/gui/continue.png")));

        //cpPredictionQuery.add(tablePane);
        cpPredictionQuery.add(predictBt);

        msgAreaTxt = new JTextArea(10, 70);
        msgAreaTxt.setEditable(false);
        JScrollPane scroll = new JScrollPane(msgAreaTxt,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cpMessage.add(scroll);

        treeConstructionBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeConstructionBt_mouseClicked();
            }
        });

        startPredictionBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startPredictionBt_mouseClicked();
            }
        });

        predictBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                predictBt_mouseClicked();
            }
        });

    }

    /**
     * Adds a border around a panel, inserting a title.
     *
     * @param panel The panel to which border will be add
     * @param title The title of the border
     */
    private void setBorderTitle(JPanel panel, String title) {
        Border borderTree = BorderFactory.createTitledBorder(title);
        panel.setBorder(borderTree);
    }

    /**
     * This class represents a model for the table adopted to represent
     * all the attributes for a specific dataset and their values.
     */
    class MyTableModel extends AbstractTableModel {
        /** the table's column names */
        private final String[] columnNames = {"Attribute", "Value"};
        /** the matrix which collects all the values */
        private Object[][] data = new Object[5][2];

        /**
         * Initializes the table model with the values
         * retrieved from the <code>attrValues</code>
         * data structure which contains all the information
         * about the attributes
         */
        public void initTableModel() {
            data = new Object[attrValues.size()][2];
            Iterator<String> iter = attrValues.keySet().iterator();
            int rowCount = 0;

            while (iter.hasNext()) {
                data[rowCount][0] = iter.next();
                data[rowCount][1] = "";
                rowCount++;
            }

        }

        /**
         * Removes all the values contained in the model
         */
        public void clearModel(){
            data = new Object[5][2];
        }

        /**
         * Returns the number of column of the table
         * @return table's number of column
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Returns the table's number of row, which represents
         * the number of entries that are present in the table.
         *
         * @return table's number of row
         */
        public int getRowCount() {
            return data.length;
        }

        /**
         * Returns the column's name at the specified position.
         *
         * @param col column's position
         * @return the name of the column selected
         */
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         * Returns the value contained in the table's cell
         * represented by the specified position.
         *
         * @param row row index of the table
         * @param col column index of the table
         * @return value contained in the position (row, col)
         */
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /**
         * Returns the Class object for the value contained in the first row
         * of the table in the specified column.
         *
         * <p>
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         *
         * @param c column index
         * @return the class object of the selected value
         *
         */
        public Class getColumnClass(int c) {
            Object o = getValueAt(0, c);
            if (o != null)
                return o.getClass();
            else
                return Object.class;
        }

        /**
         * Defines the rules in order to modify the table.
         * <p>
         * In this context only the column in which there are the values
         * is editable.
         *
         * @param row  row index
         * @param col  column index
         * @return <code>true</code> if the selected column is the second one, <code>false</code> otherwise
         */
        public boolean isCellEditable(int row, int col) {
            return (col == 1);
        }

        /**
         * Changes the value in the position of the table defined
         * by the specified indexes with the specified value.
         *
         * @param value the new values for the table's cell
         * @param row row index of the cell
         * @param col column index of the cell
         */
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    /**
     * This class models the panel in which will be contained
     * the attributes' table representation needed for the predicting process
     */
    private class MyTablePanel extends JPanel {
        /** current selected row's index */
        private int itsRow = 0;
        /** model for the graphical component which contains the attributes' values */
        private final DefaultComboBoxModel<String> modelJCB = new DefaultComboBoxModel<String>();
        /** graphical component which will show the attributes' values*/
        private final JComboBox<String> jcb = new JComboBox<String>(modelJCB);

        /**
         * Constructs the main panel and draws the graphical component which
         * belong to it.
         */
        public MyTablePanel() {
            Container cp = getContentPane();
            cp.setLayout(new FlowLayout());

            attributesTableModel = new MyTableModel();
            attributesTable = new JTable(attributesTableModel);

            attributesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            attributesTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
            attributesTable.setFillsViewportHeight(true);

            attributesTable.getColumnModel().getColumn(0)
                    .setCellRenderer(new DefaultTableCellRenderer());
            attributesTable.getColumnModel().getColumn(1)
                    .setCellEditor(new DefaultCellEditor(jcb));

            // Set up column sizes.
            initColumnSizes(attributesTable);

            attributesTable.getSelectionModel().addListSelectionListener(new RowListener());

            // Create the scroll pane and add the table to it.
            JScrollPane scrollPane = new JScrollPane(attributesTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            attributesTable.setVisible(true);
            add(scrollPane);

        }

        /**
         * This class models a listener associated to the table in order
         * to update correctly the values of the selected row.
         */
        private class RowListener implements ListSelectionListener {
            /**
             * Updates the values of the combobox of the row
             * selected by the user
             *
             * @param event event connected with this listener
             */
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    modelJCB.removeAllElements();
                    jcb.setModel(modelJCB);
                    return;
                }
                itsRow = attributesTable.getSelectedRow();
                updateJcb();

            }
        }

        /**
         * Correctly resizes the table's column in order to correct
         * their size.
         *
         * @param attributesTable the table that will be adjusted
         */
        private void initColumnSizes(JTable attributesTable) {
            for (int i = 0; i < 2; i++)
                attributesTable.getColumnModel().getColumn(i).sizeWidthToFit();
        }

        /**
         * Updates the current selected JComboBox with the
         * selected attribute's values.
         *
         */
        public void updateJcb() {
            String attribute = (String) attributesTable.getValueAt(itsRow, 0);
            modelJCB.removeAllElements();
            for (int i = 0; i < attrValues.get(attribute).length; i++) {
                modelJCB.addElement(attrValues.get(attribute)[i]);

            }
            jcb.setModel(modelJCB);
        }

    }

    /**
     * Hides and re-initializes the table panel when it is needed
     * anymore
     */
    void resetTablePanel(){
        attributesTableModel.clearModel();
        attributesTable.setModel(attributesTableModel);
        tablePane.setVisible(false);
    }

    /**
     * Class to manage events on the radio buttons in response of user's choice.
     * <p/>
     * It is responsible of setting labels and of hiding/showing buttons.
     */
    private class RadioListener implements ActionListener {
        /**
         * Method activated when user select one of the radio buttons.
         *
         * @param e Event happened
         */
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == db) {
                chooserLabel.setText("Table name: ");
                chooserFlag = false;
                nameDataTxt.setVisible(true);
                nameDataTxt.setEditable(true);
                nameDataTxt.setText("");
                availableFile.setVisible(false);
                updateFileList.setVisible(false);
            } else if (source == file) {
                chooserLabel.setText("File name: ");
                chooserFlag = false;
                nameDataTxt.setVisible(false);
                nameDataTxt.setEditable(false);
                nameDataTxt.setText("");
                availableFile.setVisible(true);
                updateFileList.setVisible(true);
            } else if (source == upload) {
                chooserLabel.setText("File name: ");
                chooserFlag = true;
                nameDataTxt.setVisible(true);
                nameDataTxt.setEditable(false);
                nameDataTxt.setText("Click here to choose a file...");
                availableFile.setVisible(false);
                updateFileList.setVisible(false);
            }
        }
    }

    /**
     * Class that provides all the services needed to convert a dataset
     * from a format to another.
     * <p/>
     * The conversion can be performed only if the last prediction has
     * created a dataset in the server (if a .dat file was loaded then
     * it will not be possible to convert the dataset).
     * <p/>
     * A request is made to the server to know which extensions are supported.
     * The supported extensions may change during application life cycle.
     */
    private class FileConverter extends JDialog {
        /**
         * Name chosen by the user to save the new file
         */
        private final JTextField chosenName;
        /**
         * List of valid extensions
         */
        private final JComboBox<String> validExt;
        /**
         * Model for the list of valid extensions
         */
        private final DefaultComboBoxModel<String> validExtModel;

        /**
         * Collects information on how files should be converted (name, format, etc...)
         * opening a JDialog.
         */
        public FileConverter() {
            Container c = getContentPane();
            setModal(true);
            setTitle("Insert information");
            setSize(280, 130);
            setLocationRelativeTo(null);
            c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
            JPanel fileInfo = new JPanel();
            fileInfo.setLayout(new FlowLayout());
            fileInfo.add(new JLabel("File name: "));
            chosenName = new JTextField(15);
            fileInfo.add(chosenName);
            JPanel extInfo = new JPanel();
            extInfo.setLayout(new FlowLayout());
            extInfo.add(new JLabel("Format: "));
            validExt = new JComboBox<String>();
            validExtModel = new DefaultComboBoxModel<String>();
            try {

                ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.SUPPORTED_EXTENSIONS));

                @SuppressWarnings("unchecked")
                ArrayList<String> elem = (ArrayList<String>) response.getAttribute(0);
                for (String s : elem) {
                    validExtModel.addElement(s);
                }
                validExtModel.addElement("script");
                validExt.setModel(validExtModel);
                extInfo.add(validExt);
                extInfo.add(new Box.Filler(new Dimension(60, 1), new Dimension(60, 1), new Dimension(60, 1)));
                JButton startConversion = new JButton("OK");
                startConversion.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        startConversion_clicked();
                    }
                });
                extInfo.add(startConversion);
                c.add(fileInfo);
                c.add(extInfo);
                setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Unable to convert dataset.", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Checks if a string can be a file name or not.
         * <p/>
         * Checks if some of the forbidden symbols are present in the string.
         *
         * @param file Candidate file name
         * @return <code>true</code> if the name is valid, <code>false</code> otherwise
         */
        private boolean isFilenameValid(String file) {
            char[] invalid = {'/', '\\', '>', '<', '?', '|', '\0'};
            for (int j = 0; j < file.length(); j++)
                for (char anInvalid : invalid)
                    if (file.charAt(j) == anInvalid)
                        return false;

            File f = new File(file);
            try {
                f.getCanonicalPath();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        /**
         * Starts the conversion of the dataset in the format selected
         * by the user.
         * <p/>
         * Checks if the chosen name is correct, sends the information to the
         * server and finally downloads the new dataset created by the server.
         */
        private void startConversion_clicked() {
            String currFileName = chosenName.getText(), currExt;
            String errMsg = "";

            if (currFileName.equals("")) {
                errMsg += "No file name specified.\n";
            }

            if (!isFilenameValid(currFileName)) {
                errMsg += "Invalid file name.\n";
            }

            currExt = (String) validExt.getSelectedItem();

            if (errMsg.equals("")) { // no errors occurred
                try {
                    String[] data = new String[2];
                    data[0] = currFileName;
                    data[1] = currExt;
                    ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.CONVERT_DATASET,
                            data));

                    String conversionPath = System.getProperty("user.home")
                            + File.separator + "ConvertedDataset";
                    FileReceiver.checkDownloadDir(conversionPath);
                    String completeFileName = conversionPath + File.separator
                            + currFileName + "." + currExt;
                    try {
                        FileReceiver.downloadFile((byte[]) response.getAttribute(0),
                                completeFileName);
                        JOptionPane.showMessageDialog(null, "File "
                                + completeFileName
                                + " was correctly converted.",
                                "Conversion complete",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null,
                                "Error in receiving file.", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            e.getCause() + " " + e.getMessage(), "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(null, errMsg, "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }

            dispose();
        }

    }

    /**
     * Checks which source was selected by the user and starts
     * the appropriate session.
     */
    private void treeConstructionBt_mouseClicked() {
        sessionTracker = new PredSessionTO();
        String treeSrc = nameDataTxt.getText();
        resetTablePanel();
        predictBt.setEnabled(false);

        if (db.isSelected()) {
            dbPredictionSession(treeSrc);
        } else if (file.isSelected()) {
            filePredictionSession();
        } else { // Upload option is selected
            uploadPredictionSession(treeSrc);
        }
    }

    /**
     * Starts a prediction loading the tree from a database table.
     * <p/>
     * If the tree has been correctly loaded the user will be asked to
     * save it on a file on the server, for faster future interrogations.
     */
    private void dbPredictionSession(String treeSrc) {
        if (treeSrc.equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Please first choose a table to load.", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.BUILD_TREE_FROM_DB, treeSrc + ".db"));


            String inputVal = (String) response.getAttribute(0);
            msgAreaTxt.setText(inputVal);
            sessionTracker.setTree(inputVal);
            startPredictionBt.setEnabled(true);
            saveAs.setEnabled(true);
            datasetCreated = true;

            Object[] values = {"Yes, please", "No, thanks"};

            int returnVal = JOptionPane
                    .showOptionDialog(
                            null,
                            "By saving the tree on a file you'll be able to load it faster in the future\n(by choosing to load your remote file instead of the database table)",
                            "Save the tree on server?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, values,
                            values[0]);
            if (returnVal == JOptionPane.YES_OPTION) {
                try {
                    response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.SERIALIZE_TREE, treeSrc + ".dat"));
                    JOptionPane.showMessageDialog(null, response.getAttribute(1),
                            "File saved",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "Unable to save the tree on the server.",
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "No tree will be saved.",
                                "Operation skipped", JOptionPane.INFORMATION_MESSAGE);
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to read the specified table from the server.\n " + e.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Starts a prediction loading the tree from a server's dataset.
     */
    private void filePredictionSession() {
        if (availableFile.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null,
                    "Please first choose a file to load.", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.LOAD_SERIALIZED_TREE, availableFile.getSelectedItem()));
            String responseMsg = (String) response.getAttribute(0);
            msgAreaTxt.setText(responseMsg);
            sessionTracker.setTree(responseMsg);
            startPredictionBt.setEnabled(true);
            saveAs.setEnabled(false);
            datasetCreated = false;
            nameDataTxt.setEditable(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to read the remote file from the server.\n"
                            + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks whether the file selected by the user is valid or not.
     * If it is valid then asks for a captcha confirmation and send the file
     * to the server.
     * <p/>
     * If the tree has been correctly built the user will be asked to
     * save it on a file on the server, for faster future interrogations.
     */
    private void uploadPredictionSession(String treeSrc) {
        // Se non e' stato selezionato alcun file da caricare
        if (treeSrc.equals("Click here to choose a file...")) {
            JOptionPane.showMessageDialog(null,
                    "Please first choose a file to upload.", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tempMessage = FileChecker.checkFile(treeSrc);

        if (!tempMessage.equals("")) {
            JOptionPane.showMessageDialog(null, tempMessage, "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            nameDataTxt.setText("Click here to choose a file...");
            return;
        }

        while (true) {
            String captcha = CaptchaGenerator.generateCaptcha();
            String answer = JOptionPane.showInputDialog(null, "Please insert: "
                    + captcha, "Captcha confirm",
                    JOptionPane.INFORMATION_MESSAGE);
            if (captcha.equals(answer))
                break;
        }

        try {
            byte[] fileContent;
            String resultMsg;
            String extension = treeSrc.substring(treeSrc.lastIndexOf(".") + 1,
                    treeSrc.length());

            FileSender sender = new FileSender(treeSrc);
            fileContent  = sender.getTransferredFile();
            resultMsg = sender.getResultMessage();
            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.UPLOAD_FILE,extension, fileContent));
            JOptionPane.showMessageDialog(null, resultMsg, "Upload completed", JOptionPane.INFORMATION_MESSAGE);

            String result = (String) response.getAttribute(0); // tree form representation
            msgAreaTxt.setText(result);
            sessionTracker.setTree(result);
            Object options[] = {"Yes, please", "No, thanks"};
            int choice = JOptionPane
                    .showOptionDialog(
                            null,
                            "By saving the file you'll be able to make another prediction"
                                    + "\nin the future without having to reupload the file.",
                            "Save the tree on the server?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.SERIALIZE_TREE, treeSrc.substring(treeSrc.lastIndexOf(File.separator), treeSrc.lastIndexOf('.')) + ".dat"));

                    JOptionPane.showMessageDialog(null, response.getAttribute(1),
                            "File saved",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "Unable to save the tree on the server.",
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "No file will be saved", "Operation skipped",
                        JOptionPane.INFORMATION_MESSAGE);
            }


            startPredictionBt.setEnabled(true);
            saveAs.setEnabled(true);
            datasetCreated = true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error in uploading file" + e.getMessage() + "\n",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Starts the prediction asking the server the first options
     * to show to the user.
     */
    private void startPredictionBt_mouseClicked() {

        try {
            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.ATTRIBUTES_INFORMATION));

            attrValues = (HashMap<String, String[]>) response.getAttribute(0);

            Component[] components = cpPredictionQuery.getComponents();
            boolean flag = (components.length != 2);
            if( flag){
                cpPredictionQuery.removeAll();
            }

            attributesTableModel.initTableModel();
            attributesTable.setModel(attributesTableModel);

            if( flag ){
                cpPredictionQuery.add(tablePane);
                cpPredictionQuery.add(components[0]);
            }
            predictBt.setEnabled(true);
            //tablePane.updateJcb();
            tablePane.setVisible(true);

            repaint();
            revalidate();

        } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Unable to get attributes' information in order to fill the attributes' table.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkTableFields(){
        for(int i = 0; i < attributesTable.getRowCount(); i++){
            Object val = attributesTable.getValueAt(i, 1);
            if( val == null || val.equals("") )
                return false;
        }

        return true;
    }

    /**
     * Resets some buttons and labels in the GUI to start a prediction.
     */
    private void initPredictionState() {

        predictBt.setEnabled(false);
        startPredictionBt.setEnabled(true);
        if (datasetCreated)
            saveAs.setEnabled(true);
        treeConstructionBt.setEnabled(true);
        if (db.isSelected())
            nameDataTxt.setEditable(true);
    }

    /**
     * Sends to the server the user's choices for the available attributes.
     * <p/>
     * When the prediction is over then shows a message to the user with
     * the predicted value, otherwise asks for further options.
     */
    private void predictBt_mouseClicked() {

        if( checkTableFields() ){
            // Takes all the user choices  and update the session tracker
            HashMap<String, Integer> userChoices = new HashMap<String, Integer>();
           for(int i = 0; i < attributesTable.getRowCount(); i++){
               String key = (String)attributesTable.getValueAt(i,0), value = (String)attributesTable.getValueAt(i,1);

               int optionElem = Arrays.asList(attrValues.get(key)).indexOf(value);
               sessionTracker.addEntry(key, value);
               userChoices.put(key, optionElem);
           }

           // Send to the server to make the prediction
            try {
                ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.START_PREDICTION,userChoices));
                String outputVal = (String) response.getAttribute(0); // first attribute the predicted value
                sessionTracker.setPredictedValue(outputVal);

                Object[] choice = new Object[] { "Make another prediction",
                        "Send the result via mail" };
                int val = JOptionPane.showOptionDialog(null, "Prediction: "
                        + outputVal, "Operation completed",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                        loadImage("/gui/flag.png")), choice, choice[0]);
                if (val == JOptionPane.NO_OPTION) {
                    predictionPanel.setVisible(false);
                    emailPanel.setVisible(true);
                } else {
                    initPredictionState();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error in the prediction session.\n" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Please fill all the table's fields before start the prediction", "ERROR", JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * Disables all the text fields in a component (and in its sub-components).
     *
     * @param comp The component in which fields must be disabled
     */
    private void disableTextField(Component[] comp) {
        for (Component aComp : comp) {
            if (aComp instanceof JPanel)
                disableTextField(((JPanel) aComp).getComponents());
            else if (aComp instanceof JTextField) {
                ((JTextField) aComp).setEditable(false);
                return;
            }
        }
    }


    /**
     * Displays a file chooser where the user can select which file
     * upload to the server.
     * <p/>
     * A request is made to the server to know which extensions are supported.
     * The supported extensions may change during application life cycle.
     */
    @SuppressWarnings("unchecked")
    private void fileChooser_mouseClicked() {
        if (!chooserFlag) {
            return;
        }
        try {
            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.SUPPORTED_EXTENSIONS));
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            FileFilter customFilter = new ExtensionFilter(
                    (ArrayList<String>) response.getAttribute(0));
            // File chooser displays the user's home directory
            JFileChooser fileChooser = new JFileChooser(
                    System.getProperty("user.home"));
            // The user is only able to select a file from the directory.
            disableTextField(fileChooser.getComponents());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(customFilter);
            int rVal = fileChooser.showOpenDialog(TreeClient.this);
            if (rVal == JFileChooser.APPROVE_OPTION)
                nameDataTxt.setText(fileChooser.getSelectedFile().toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to send any file",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method automatically invoked when the applet has to be closed.
     */
    public void destroy() {
        closureRoutine();
        super.destroy();
    }

    /**
     * Closes the connection with the server
     */
    private void closureRoutine() {
        try {
            remoteServer.satisfyRequest(new ClientRequest(ServerCommand.CLOSE_CONNECTION));
        } catch (Exception e) {
            System.err
                    .println("Unable to correctly close current session with server (connection already closed).");
        }
    }

    /**
     * Asks the server for the list of available files that can
     * be chosen by the user.
     *
     * @return the list of available files
     */
    private String[] getFileList() {
        String[] file = null;

        try {
            ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.AVAILABLE_DAT_FILES));
            file = (String[]) response.getAttribute(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to load file list...\nTry pressing 'Update list'",
                    "ERROR", JOptionPane.ERROR_MESSAGE);

            System.err.println(e.getMessage());
        }

        if (file == null)
            file = new String[1];

        return file;
    }

    /**
     * Loads an image which has as name the specified filename, <code>fileName</code>
     * from the resources folder.
     *
     * @param fileName the image's filename(including extension)
     * @return The image loaded
     */
    BufferedImage loadImage(String fileName) {

        try {

            return ImageIO.read(getClass().getResourceAsStream(fileName));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to load icons", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }


    /**
     * Provides all the services to send an email to one or
     * many addressees with the report of the last prediction.
     */
    public class MailPanel extends JPanel {
        /**
         * Sender email
         */
        private final JTextField fromField;
        /**
         * Last addressee email
         */
        private JTextField toField;
        /**
         * List of addressees
         */
        private final JComboBox<String> addrList;
        /**
         * Model for the list of addressees
         */
        private final DefaultComboBoxModel<String> cbModel;
        /**
         * Object of the email
         */
        private JTextField objField;
        /**
         * Body of the email
         */
        private JTextArea mailBody;
        /**
         * checkbox used in order to understand if the email needs an attachment
         */
        private final JCheckBox attachFlag;

        /**
         * Draws the GUI of the email panel.
         */
        @SuppressWarnings("unchecked")
        public MailPanel() {
            this.setBorder(BorderFactory.createTitledBorder("Mail sender"));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            JPanel attachPanel = new JPanel();
            attachPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            JPanel sendPanel = new JPanel();
            sendPanel.setLayout(new BorderLayout());

            infoPanel.setAlignmentX(LEFT_ALIGNMENT);
            attachPanel.setAlignmentX(LEFT_ALIGNMENT);
            labelPanel.setAlignmentX(LEFT_ALIGNMENT);
            textPanel.setAlignmentX(LEFT_ALIGNMENT);
            sendPanel.setAlignmentX(LEFT_ALIGNMENT);

            JPanel senderPanel = new JPanel();
            senderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            senderPanel.add(new JLabel("From:    "));
            fromField = new JTextField(20);
            /* Disables standard policy for grabbing focus.
	        * Pressing TAB button will move cursor to the next text field,
	        * allowing the user to rapidly fill in the information. */
            fromField.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                    Collections.EMPTY_SET);
            fromField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB)
                        toField.grabFocus();
                }
            });
            senderPanel.add(fromField);
            infoPanel.add(senderPanel);

            JPanel addresseesPanel = new JPanel();
            addresseesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            addresseesPanel.add(new JLabel("To:         "));
            toField = new JTextField(20);
            /* Disables standard policy for grabbing focus.
	        * Pressing TAB button will move cursor to the next text field,
	        * allowing the user to rapidly fill in the information. */
            toField.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                    Collections.EMPTY_SET);
            toField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        addAddress_clicked();
                    else if (e.getKeyCode() == KeyEvent.VK_TAB)
                        objField.grabFocus();
                }
            });
            addresseesPanel.add(toField);
            JButton addAddress = new JButton(new ImageIcon(
                    loadImage("/gui/plus.png")));
            addAddress.setToolTipText("Add the specified addressee to the box");
            addAddress.setContentAreaFilled(false);
            addAddress.setOpaque(false);
            addAddress.setBorderPainted(false);
            addAddress.setFocusPainted(false);
            addAddress.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addAddress_clicked();
                }
            });

            addresseesPanel.add(addAddress);
            JLabel temp = new JLabel("  ");
            temp.setIcon(new ImageIcon(loadImage("/gui/addresses.png")));
            addresseesPanel.add(temp);
            cbModel = new DefaultComboBoxModel<String>();
            addrList = new JComboBox<String>(cbModel);
            addresseesPanel.add(addrList);
            JButton remAddress = new JButton(new ImageIcon(
                    loadImage("/gui/minus.png")));
            remAddress
                    .setToolTipText("Remove the currently selected addressee in the box");
            remAddress.setContentAreaFilled(false);
            remAddress.setOpaque(false);
            remAddress.setBorderPainted(false);
            remAddress.setFocusPainted(false);
            remAddress.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    remAddress_clicked();
                }
            });
            addresseesPanel.add(remAddress);
            infoPanel.add(addresseesPanel);

            JPanel objectPanel = new JPanel();
            objectPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            objectPanel.add(new JLabel("Object:  "));
            objField = new JTextField(35);
            /* Disables standard policy for grabbing focus.
	        * Pressing TAB button will move cursor to the next text field,
	        * allowing the user to rapidly fill in the information. */
            objField.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                    Collections.EMPTY_SET);
            objField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB)
                        mailBody.grabFocus();
                }
            });
            objectPanel.add(objField);

            infoPanel.add(objectPanel);

            JLabel attachIcon = new JLabel();
            attachIcon.setIcon(new ImageIcon(loadImage("/gui/attach.png")));
            attachPanel.add(attachIcon);
            attachPanel.add(new Box.Filler(new Dimension(16, 1), new Dimension(16, 1), new Dimension(16, 1)));
            JTextField reportAttach = new JTextField(" report.pdf", 8);
            reportAttach.setEditable(false);
            attachPanel.add(reportAttach);

            attachFlag = new JCheckBox();
            attachFlag.setSelected(true);
            attachPanel.add(attachFlag);

            labelPanel.add(new JLabel("Text:", JLabel.LEFT));

            mailBody = new JTextArea(10, 10);
            JScrollPane scrollBar = new JScrollPane(mailBody,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollBar.setPreferredSize(new Dimension(100, 300));
            textPanel.add(scrollBar);

            JButton goBack = new JButton(new ImageIcon(
                    loadImage("/gui/undo.png")));
            goBack.setToolTipText("Go back to prediction menu");
            goBack.setContentAreaFilled(false);
            goBack.setOpaque(false);
            goBack.setBorderPainted(false);
            goBack.setFocusPainted(false);
            goBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    goBack_clicked();
                }
            });
            sendPanel.add(goBack, BorderLayout.LINE_START);

            JButton sendMail = new JButton(new ImageIcon(
                    loadImage("/gui/send.png")));
            sendMail.setToolTipText("Send email(s)");
            sendMail.setContentAreaFilled(false);
            sendMail.setOpaque(false);
            sendMail.setBorderPainted(false);
            sendMail.setFocusPainted(false);
            sendMail.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendMail_clicked();
                }
            });
            sendPanel.add(sendMail, BorderLayout.LINE_END);

            this.add(infoPanel);
            this.add(attachPanel);
            this.add(labelPanel);
            this.add(textPanel);
            this.add(sendPanel);
        }

        /**
         * Returns the list of addressees inserted by the user.
         *
         * @return List of addressees in the box
         */
        private List<String> getAddressees() {
            List<String> addr = new ArrayList<String>();

            for (int i = 0; i < cbModel.getSize(); i++) {
                addr.add(cbModel.getElementAt(i));
            }

            return addr;

        }

        /***
		* This class represents a simple dialog which is displayed 
		* while the client is sending the request of sending the email.
		*
		**/
        private class Task extends JDialog implements Runnable {
            /** The supplementary thread which grant to display the dialog */
			final Thread t;

			/**
			* Construct this object and starts the thread
			* in order to grant to it to complete its task.
			*/
            public Task() {
                t = new Thread(this);
                t.start();
            }

			/**
			* Draws the dialog on the screen
			*/
            public void run() {
                setSize(new Dimension(300, 50));
                setLocationRelativeTo(null);
                setTitle("Sending email(s)...");
                setModal(true);
                setVisible(true);
            }
        }

        /**
         * Sends the email to the addressee(s) specified by the user.
         * <p/>
         * Checks if the email service of the sender is supported,
         * and shows a little window while email are being sent.
         */
        private void sendMail_clicked() {
            String from = fromField.getText(), mailText = mailBody.getText(), object = objField
                    .getText();
            List<String> addressees = getAddressees();
            String errMsg = "";

            if (from.equals(""))
                errMsg += "No sender address specified.\n";

            Object result = validateMailAddress(from);
            if (result instanceof InvalidMailAddressException)
                errMsg += "Invalid sender email address.\n"
                        + ((InvalidMailAddressException) result).getMessage();
            else if (result instanceof Exception)
                errMsg += "Connection with server lost.\n";

            if (addressees.isEmpty())
                errMsg += "No addressee present.\n";

            if (!errMsg.equals("")) {
                JOptionPane.showMessageDialog(null, errMsg, "Operation failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                MailDataTO to = new MailDataTO();
                to.setAddressees(addressees);
                to.setSessionTO(sessionTracker);
                to.setFrom(from);
                to.setMailText(mailText);
                to.setObject(object);
                to.setAttach(attachFlag.isSelected());
                Task temp = null;

                JPasswordField pf = new JPasswordField();

                simulateShifTab();
                int answer = JOptionPane.showConfirmDialog(null, pf,
                        "Insert email password",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (answer != JOptionPane.OK_OPTION) {
                    return;
                }
                try {
                    to.setPassword(new String(pf.getPassword()));

                    temp = new Task();
                    ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.SEND_EMAIL, to));
                    temp.dispose();

                    JOptionPane.showMessageDialog(null, response.getAttribute(0)
                            + " to \n" + to.getAddressees(),
                            "Sending complete",
                            JOptionPane.INFORMATION_MESSAGE);

                    goBack_clicked();


                } catch (IllegalArgumentException e) {
                    if (temp != null) {
                        temp.dispose();
                    }
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "Error in sending email.\nUnsupported sender provider.",
                                    "Failed in sending email",
                                    JOptionPane.ERROR_MESSAGE);

                } catch (Exception e) {
                    temp.dispose();
                    JOptionPane.showMessageDialog(null,
                            "Error in sending email.\n"
                                    + e.getMessage(),
                            "Failed in sending email",
                            JOptionPane.ERROR_MESSAGE);


                }
            }
        }

        /**
         * Removes the currently displayed addressee from the list of
         * addressees inserted by the user.
         */
        private void remAddress_clicked() {
            int rowIndex = addrList.getSelectedIndex();

            if (rowIndex != -1) {
                cbModel.removeElementAt(rowIndex);
                addrList.setModel(cbModel);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Please select an addressee to delete",
                        "Invalid operation", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Adds the inserted addressee to the list of
         * addressees inserted by the user.
         */
        private void addAddress_clicked() {
            String toFieldStr = toField.getText();
            if (toFieldStr.equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Please specify an addressee to add",
                        "Invalid operation", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object response = validateMailAddress(toFieldStr);
            if (response instanceof InvalidMailAddressException) {
                JOptionPane.showMessageDialog(null,
                        "Incorrect addressee inserted.\n"
                                + ((Exception) response).getMessage(),
                        "Invalid operation", JOptionPane.ERROR_MESSAGE);
            } else if (response instanceof Exception) {
                JOptionPane.showMessageDialog(null,
                        "Connection with server lost.\n" + ((Exception)response).getMessage(), "Invalid operation",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                // The current email address is not present in the list.
                if (cbModel.getIndexOf(toFieldStr) == -1) {
                    cbModel.addElement(toField.getText());
                    addrList.setModel(cbModel);
                    toField.setText("");
                } else {
                    JOptionPane
                            .showMessageDialog(
                                    null,
                                    "The email address that you've specified is already present in the list.",
                                    "Invalid operation",
                                    JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
         * Checks if the specified email address is in the regular
         * internet address format.
         *
         * @param mailAddress The string to be checked
         * @return The response of the server (it may be an exception)
         */
        private Object validateMailAddress(String mailAddress) {

            try {
                ServerResponse response = remoteServer.satisfyRequest(new ClientRequest(ServerCommand.VALIDATE_EMAIL, mailAddress));

                return response.getAttribute(0);

            } catch (Exception e) {
                return e;
            }
        }

        /**
         * Updates the GUI to let the user come back to the
         * prediction menu.
         */
        private void goBack_clicked() {
            cleanEmailFields();
            predictionPanel.setVisible(true);
            emailPanel.setVisible(false);
            sessionTracker.clearTracker();
            resetTablePanel();
            predictBt.setEnabled(false);
            startPredictionBt.setEnabled(true);
            if (datasetCreated)
                saveAs.setEnabled(true);
            treeConstructionBt.setEnabled(true);
        }


        /**
         * Cleans all the email fields, for future sendings.
         */
        private void cleanEmailFields() {
            fromField.setText("");
            toField.setText("");
            cbModel.removeAllElements();
            addrList.setModel(cbModel);
            objField.setText("");
            mailBody.setText("");
            attachFlag.setSelected(true);
        }


        /**
         * Simulates the pressure of Shift + TAB keys.
         */
        private void simulateShifTab() {
            try {
                Robot r = new Robot();
                r.keyPress(KeyEvent.VK_SHIFT);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_SHIFT);
            } catch (AWTException e) {
                System.err.println("Error: " + e.getCause() + " -- "
                        + e.getMessage());
            }
        }

    }
}
