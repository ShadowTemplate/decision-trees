package com.mapgroup.classificator.boundary;

import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.database.dao.DbAccess;
import com.mapgroup.classificator.filemanager.RequestStruct;
import com.mapgroup.classificator.filemanager.writer.DataWriterFactory;
import com.mapgroup.classificator.filemanager.writer.IDataWriter;
import com.mapgroup.classificator.mail.MailAddressValidator;
import com.mapgroup.classificator.mail.MailSender;
import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.tree.UnknownValueException;
import com.mapgroup.classificator.tree.NoTreeExists;
import com.mapgroup.classificator.tree.SplitNode;
import com.mapgroup.classificator.tree.LeafNode;
import com.mapgroup.classificator.utility.FileReceiver;
import com.mapgroup.classificator.utility.FileSender;
import com.mapgroup.classificator.utility.PdfCreator;
import com.mapgroup.classificator.utility.SupportedExtensions;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.InvalidMailAddressException;
import com.mapgroup.to.MailDataTO;
import com.mapgroup.to.ServerCommand;

import javax.mail.internet.AddressException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.Container;
import java.awt.FlowLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents all the main server capabilities.&nbsp;
 * The server grant the possibility to the user to learn
 * <a href=http://en.wikipedia.org/wiki/Decision_tree>Decision tree</a>
 * from a specified dataset.
 *
 * <p>
 * The server, using the multi-threading Java libraries, is able
 * to manage multiple clients in the same moment and grants to them
 * to ask for the services that has at its disposal.
 * </p>
 *
 * <p>
 * Other important functionality that the server grant to the user are:
 * <ul>
 * <li> Report of the predicition session in pdf format</li>
 * <li> Upload of specific dataset in different format (txt,arff,xls)</li>
 * <li> Mail sending to multiple addressees with attachments </li>
 * <li> Dataset creation and manipulation from the supported dataset format </li>
 * <p/>
 * </ul>
 *
 */
class MultiServer implements Runnable {

    /** graphical panel which contains the list of all connected clients*/
    private JList<String> clientArea;
    /** specific model for the <code>clientArea</code> which defines its structure*/
    private DefaultListModel<String> listModel;
    /**
     *  graphical panel which contains all the detail about the operation that the server does
        during its activity
     * */
    private JTextArea logArea;
    /** the main server socket */
    private ServerSocket serverSocket = null;
    /** the port on which the server is started */
    private static int serverPort;
    /** the client socket that needs to be closed */
    private Socket closingSocket = null;

    /**
     * MultiServer constructor which loads the server on the
     * specified <code>port</code> and create its graphical interface.
     *
     * */
    public MultiServer(int port){
        serverPort = port;
        Utility.checkMainFolder();
        Utility.cleanFolder("download");
        Utility.cleanFolder("report");
        Utility.cleanFolder("conversion");
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Initializes and creates the graphical interface for the
     * main panel of the server on which will be displayed all the
     * operation that the clients will do.
     * */
    private void initAndShowGUI() {
        JFrame frame = new JFrame("Decision Tree Server");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(950, 600));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (DbAccess.getConnection() != null)
                    DbAccess.closeConnection();
                // removes the elements of all the server's main folders
                Utility.cleanFolder("download");
                Utility.cleanFolder("report");
                Utility.cleanFolder("conversion");
                // close correctly the server socket
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException ex) {
                        System.err.println("Error: " + ex.getMessage());
                    }
                }
            }

        });
        frame.setLocationRelativeTo(null);
        Container c = frame.getContentPane();
        c.setLayout(new FlowLayout());

        JPanel clientPanel = new JPanel();
        JPanel logPanel = new JPanel();
        c.add(clientPanel);
        c.add(logPanel);

        clientPanel.setBorder(BorderFactory
                .createTitledBorder("Clients connected"));
        clientPanel.setPreferredSize(new Dimension(300, 520));
        clientPanel.setLayout(new FlowLayout());

        listModel = new DefaultListModel<String>();
        clientArea = new JList<String>(listModel);
        clientArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        /*
         * Associates to the clientArea panel an event in order to be able
         * to close the connection with the selected client.
         * */
        clientArea.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (listModel.getSize() != 0 && !clientArea.isSelectionEmpty()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Object[] options = new String[]{"Yes", "No"};

                        int res = JOptionPane
                                .showOptionDialog(
                                        null,
                                        "The operation is irreversible.\nDo you really want to disconnect the client?",
                                        "Disconnecting client",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null,
                                        options, options[0]);

                        if (res == JOptionPane.YES_OPTION) {
                            if (closingSocket != null) {
                                String rem = getSocketInfo(closingSocket);
                                closeSocket();
                                listModel.removeElement(rem);
                                clientArea.setModel(listModel);
                            } else
                                logArea.append("Unable to retrieve the socket to be closed.\n");
                        }
                    }
                }
            }
        });

        clientArea.setLayoutOrientation(JList.VERTICAL);
        clientArea.setVisibleRowCount(10);
        JScrollPane scrollClient = new JScrollPane(clientArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollClient.setPreferredSize(new Dimension(290, 490));
        clientPanel.add(scrollClient);
        clientArea.setBackground(frame.getBackground());

        logPanel.setBorder(BorderFactory.createTitledBorder("Log panel"));
        logPanel.setLayout(new FlowLayout());
        logArea = new JTextArea(30, 50);
        logArea.setBackground(frame.getBackground());
        logArea.setEditable(false);

        /*
         * Associates to the logArea an event, by which,when the user
         * click with the right button of the mouse on the logArea, he will
         * be able to remove all the contents of the log panel.
         *
         * */
        logArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object[] options = new String[]{"Yes", "No"};
                    int res = JOptionPane
                            .showOptionDialog(
                                    null,
                                    "The operation is irreversible.\nDo you really want to clean the log panel?",
                                    "Cleaning log panel",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null,
                                    options, options[0]);
                    if (res == JOptionPane.YES_OPTION)
                        logArea.setText("");

                }
            }
        });
        JScrollPane scrollLog = new JScrollPane(logArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        logPanel.add(scrollLog);

        frame.setVisible(true);
    }

    /**
     * Closes the communication with the client socket represented by <code>closingSocket</code>
     *
     * */
    private void closeSocket() {

        try {
            closingSocket.close();
            logArea.append("Client "
                    + closingSocket.getInetAddress().getHostName()
                    + " was killed.\n");
        } catch (IOException e) {
            logArea.append("Unable to close socket: " + e.getMessage());
        }
        closingSocket = null;
    }

    /**
     * Adds the specified client information to the <code>clientArea</code>
     * The client's information will be shown following the format returned by the
     * {@link #getSocketInfo(java.net.Socket)} method
     *
     * @param s the new connected client
     * */
    private void addClient(Socket s) {
        listModel.addElement(getSocketInfo(s));
        clientArea.setModel(listModel);
    }

    /**
     * Returns a string which represents the information about
     * the client formatted using the format:
     * #HOSTNAME:#LOCAL_PORT(#PORT)
     * @param s the client socket
     * @return the socket information in the specified format
     * */
    private String getSocketInfo(Socket s) {
        return s.getInetAddress().getHostName() + ": " + s.getLocalPort()
                + " (" + s.getPort() + ")";
    }

    /**
     * Removes the information about the specified client from the <code>clientArea</code>
     * @param s the client that will be removed
     * */
    private void removeClient(Socket s) {
        listModel.removeElement(getSocketInfo(s));
        clientArea.setModel(listModel);
    }

    /**
     * Starts the process with which the server's graphical interface
     * was initialized and grant to it to wait until some clients ask to
     * him something.
     * */
    public void run() {

        try {
            serverSocket = new ServerSocket(serverPort);

            initAndShowGUI();

            logArea.setText("Server is waiting for connection...\n");

            while (true) {
                Socket s = serverSocket.accept();
                try {
                    new ServeOneClient(s);

                } catch (IOException e) {
                    if (s != null) {
                        s.close();
                    }
                }

            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Server is already running...",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * This class contains all the functionality to manage the
     * request of a single client
     * */
    private class ServeOneClient extends Thread {
        /** a reference to the client socket */
        private final Socket socket;
        /** the communication stream used to receive information from the client */
        private ObjectInputStream in;
        /** the communication stream used to send information from the client */
        private ObjectOutputStream out;
        /** the generated decision tree*/
        private DecisionTree tree;

        /**
         * The constructor of this class which uses the
         * specified socket object in order to initialize the connection
         * with the specific client.
         *
         * @param s new connected client
         * @throws IOException an error in the creation of the stream occurs
         * */
        public ServeOneClient(Socket s) throws IOException {
            socket = s;
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());

            start();
        }

        /**
         * Starts the communication with the client and it waits
         * until the client ask to the server to close the connection or
         * some errors occurred while the connection is started.
         * */
        public void run() {
            clientArea.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (getSocketInfo(ServeOneClient.this.socket).equals(
                                clientArea.getSelectedValue()))
                            closingSocket = ServeOneClient.this.socket;

                    }
                }
            });

            logArea.append("New client connected: "
                    + socket.getInetAddress().getHostName() + "\n");
            addClient(socket);

            Data trainingSet = null;
            boolean flag = true;
            try {
                while (flag) {
                    try {
                        ServerCommand command = ((ServerCommand) in.readObject());
                        switch (command) {
                            // Build a decision tree from a database table
                            case BUILD_TREE_FROM_DB:
                                try {
                                    String table = (String) in.readObject();
                                    trainingSet = new Data(table);
                                    tree = new DecisionTree(trainingSet);
                                    out.writeObject(tree.toString());
                                    logArea.append("Correctly loaded the database table "
                                            + table + "\n");

                                } catch (DataException e) {
                                    System.err.println(e.getMessage());
                                    out.writeObject(new Exception(e.getMessage()));
                                }

                                break;
                            /*
                            * Serializes the current decision tree on file with
                            * extension ".dat"
                            *
                            * */
                            case SERIALIZE_TREE:
                                String fileName = (String) in.readObject();
                                try {
                                    if (tree == null) {
                                        logArea.append("Unable to save "
                                                + fileName
                                                + ".\nNo decision tree currently loaded in memory.\n");

                                        out.writeObject(new NullPointerException());

                                    } else {
                                        String serialPath = System
                                                .getProperty("user.dir")
                                                + File.separator + "dataset";

                                        Utility.checkDirectory(serialPath);

                                        String filePath = Utility
                                                .getAvailableName(serialPath
                                                        + File.separator + fileName);
                                        tree.saveTree(filePath);
                                        String msg = "The tree was correctly saved in "
                                                + filePath
                                                .substring(
                                                        filePath.lastIndexOf(File.separator) + 1,
                                                        filePath.length())
                                                + "\n";
                                        logArea.append(msg);
                                        out.writeObject(msg);
                                    }

                                } catch (IOException e) {
                                    System.err.println(e.getMessage());
                                    out.writeObject(e);

                                }
                                break;
                            /*
                            * Loads a decision tree from a specific file with
                            * extension ".dat" available on the server
                            * */
                            case LOAD_SERIALIZED_TREE:
                                fileName = (String) in.readObject();
                                try {
                                    String filePath = System
                                            .getProperty("user.dir")
                                            + File.separator
                                            + "dataset"
                                            + File.separator + fileName;

                                    tree = DecisionTree.loadTree(filePath);
                                    logArea.append("The file " + fileName
                                            + " was correctly loaded.\n");
                                    out.writeObject(tree.toString());
                                } catch (IOException e) {

                                    logArea.append(e.getMessage() + "\n");
                                    out.writeObject(e);

                                } catch (ClassNotFoundException e) {
                                    logArea.append(e.getMessage() + "\n");

                                    out.writeObject(e);

                                }
                                break;
                            // Uses the current decision tree to make a prediction
                            case START_PREDICTION:
                                boolean successFlag = true;
                                while (successFlag) {
                                    try {
                                        String classValue = predictClass(tree);
                                        out.writeObject("Transmitting class ...");
                                        out.writeObject(classValue);
                                        successFlag = false;
                                    } catch (NoTreeExists e) {

                                        logArea.append("Impossible to predict the value of the dependet attribute.\n"
                                                + e.getMessage() + "\n");

                                        out.writeObject(new Exception(
                                                "No tree was loaded"));
                                    } catch (UnknownValueException e) {
                                        logArea.append("Incorrect choice inserted.\n");

                                        out.writeObject(new Exception(
                                                "An incorrect choice was been selected."));
                                    }
                                }
                                break;
                            // The client has asked to the server to close the connection
                            case CLOSE_CONNECTION:
                                flag = false;
                                logArea.append("Client "
                                        + socket.getInetAddress().getHostName()
                                        + " has disconnected.\n");
                                removeClient(socket);
                                break;
                            // Sends to the client the supported dataset file format extensions
                            case SUPPORTED_EXTENSIONS:
                                SupportedExtensions filter = new SupportedExtensions();
                                out.writeObject(filter.getSupportedExtension());

                                break;
                            // Generates a decision tree from a dataset uploaded by the client
                            case UPLOAD_FILE:
                                try {
                                    String downDir = System.getProperty("user.dir")
                                            + File.separator + "download"
                                            + File.separator;
                                    String extension = (String) in.readObject();
                                    Utility.checkDirectory(downDir);
                                    String fileSaved = FileReceiver.downloadFile(
                                            socket, downDir, extension);
                                    logArea.append("Download of " + fileSaved
                                            + " completed.\n");

                                    trainingSet = new Data(fileSaved);
                                    tree = new DecisionTree(trainingSet);
                                    out.writeObject(tree.toString());
                                } catch (IOException e) {
                                    out.writeObject(e);
									logArea.append("Error: " + e.getMessage()+ "\n");
                                }
                                break;
                            // Sends the list of all the ".dat" files available on the server for the client
                            case AVAILABLE_DAT_FILES:
                                File currDir = new File(
                                        System.getProperty("user.dir")
                                                + File.separator + "dataset");
                                out.writeObject(currDir.list(new FilenameFilter() {
                                    public boolean accept(File dir, String s) {
                                        File currFile = new File(s);
                                        if (currFile.isDirectory())
                                            return false;

                                        String fileName = currFile.getName(), extension = fileName.substring(
                                                fileName.lastIndexOf(".") + 1,
                                                fileName.length());
                                        return (extension.equals("dat"));
                                    }

                                }));

                                break;
                            // Sends an email with the current user session result
                            case SEND_EMAIL:
                                MailDataTO mailData = (MailDataTO) in.readObject();
                                try {
                                    String reportPath = System
                                            .getProperty("user.dir")
                                            + File.separator + "report";

                                    Utility.checkDirectory(reportPath);

                                    String encryptedName = "report_" + Utility.encryptIP(socket.getInetAddress()
                                            .getHostAddress() + "("
                                            + socket.getPort() + ")") + "." + "pdf";
                                    String pdfFileName = reportPath
                                            + File.separator
                                            + encryptedName;

                                    PdfCreator.createReport(
                                            mailData.getSessionTO(), pdfFileName);
                                    MailSender.sendEmail(mailData, pdfFileName);

                                    String msg;
                                    if (mailData.isAttached())
                                        msg = "email with attachment\n"
                                                + encryptedName + "\nwas sent to\n"
                                                + mailData.getAddressees()
                                                + " correctly.";
                                    else
                                        msg = "email was sent to"
                                                + mailData.getAddressees()
                                                + " correctly.";

                                    logArea.append(msg + "\n");
                                    out.writeObject("email correctly sent");
                                } catch (IllegalArgumentException e) {
                                    logArea.append("Unsupported sender provider\n");
                                    out.writeObject(e);
                                } catch (Exception e) {
                                    logArea.append(e.getMessage() + "\n");
                                    out.writeObject(new Exception(e.getMessage()));
                                }

                                break;
                            // Checks the email address validity
                            case VALIDATE_EMAIL:
                                String mailAddr = (String) in.readObject();
                                try {
                                    MailAddressValidator.validate(mailAddr);
                                    out.writeObject("Correct mail address.");
                                } catch (AddressException e) {
                                    out.writeObject(new InvalidMailAddressException(
                                            e.getMessage()));
                                }
                                break;
                            // Converts a dataset in a specific format requested by the client
                            case CONVERT_DATASET:
                                // reads the filename and extension
                                String[] data = (String[]) in.readObject();
                                // now convert the current dataset in the extension
                                // selected.
                                IDataWriter datasetWriter = DataWriterFactory
                                        .createDataWriter(data[1]);
                                String convDir = System.getProperty("user.dir")
                                        + File.separator + "conversion";

                                Utility.checkDirectory(convDir);

                                String completeFileName = convDir + File.separator
                                        + socket.getInetAddress().getHostAddress()
                                        + "(" + socket.getPort() + ")" + "."
                                        + data[1];
                                datasetWriter.write(new RequestStruct(
                                        completeFileName, trainingSet, data[0]));
                                try {
                                    new FileSender(socket, completeFileName,
                                            logArea);
                                    Object response = in.readObject();
                                    if (response instanceof String)
                                        logArea.append("The file "
                                                + completeFileName
                                                + " was correctly converted.\n");
                                    else
                                        logArea.append("Error in converting file.\n"
                                                + ((Exception) response).getCause()
                                                + " "
                                                + ((Exception) response)
                                                .getMessage() + "\n");
                                } catch (Exception e) {
                                    logArea.append("Error in sending file: "
                                            + e.getCause() + " " + e.getMessage()
                                            + "\n");
                                    out.writeObject(e);
                                }

                                break;

                            default:
                                logArea.append("Incorrect command.\n");
                                out.writeObject("Incorrect command.\n");

                        }// END SWITCH
                    } catch (ClassCastException e) {
                	logArea.append("Error in communicating with client.\n");
                    } catch (IOException e) {

                	if (e.getMessage() == null) {
                	    logArea.append("Error: connection with client is closed.\n");
                	    removeClient(socket);
                	} else if (e.getMessage().equals("socket closed")) {
                            removeClient(socket);
                        }
                        else{
                        logArea.append("Error: " + e.getMessage() + "\n");
                        removeClient(socket);
                    }
                	
                        flag = false;
                    } catch (ClassNotFoundException e) {

                        logArea.append("Error: " + e.getMessage() + "\n");

                        flag = false;
                    } catch (DataException e) {

                        logArea.append("Error: " + e.getMessage() + "\n");

                        try {
                            out.writeObject(e);
                        } catch (IOException e1) {

                            logArea.append("Error: " + e.getMessage() + "\n");
                        }
                    } 
                }

            } finally {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException e) {
                    logArea.append("Error: " + e.getMessage() + "\n");
                }
            }
        }

        /**
         * Inspects the decision tree constructed following the options
         *  chosen by the user in order to get the predicted value.
         *
         * @param tree the current decision tree
         * @return the predicted value for this prediction session
         * @throws NoTreeExists if no available tree was present
         * @throws UnknownValueException if an incorrect choice was selected by the user
         * @throws ClassNotFoundException unable to read object from the client
         * @throws IOException error in communicating with the client
         */
        private String predictClass(DecisionTree tree) throws NoTreeExists,
                UnknownValueException, ClassNotFoundException, IOException {
            if (tree == null)
                throw new NoTreeExists();

            if (tree.getRoot() instanceof LeafNode)
                return ((LeafNode) tree.getRoot()).getPredictedClassValue();
            else {
                int risp;
                out.writeObject((((SplitNode) tree.getRoot()).formulateQuery()));
                risp = ((Integer) in.readObject());
                if (risp == -1 || risp >= tree.getRoot().getNumberOfChildren()) {
                    throw new UnknownValueException();
                } else {
                    return predictClass(tree.subTree(risp));
                }

            }
        }

    }

}
