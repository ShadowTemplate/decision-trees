package com.mapgroup.classificator.boundary;

import com.mapgroup.classificator.controller.ContextRequest;
import com.mapgroup.classificator.controller.IServerExecutor;
import com.mapgroup.classificator.controller.ServerExecutorFactory;
import com.mapgroup.classificator.data.Data;
import com.mapgroup.classificator.database.dao.DbAccess;
import com.mapgroup.classificator.tree.DecisionTree;
import com.mapgroup.classificator.utility.Utility;
import com.mapgroup.to.ClientRequest;
import com.mapgroup.to.IRemoteServer;
import com.mapgroup.to.ServerCommand;
import com.mapgroup.to.ServerResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;


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
public class MultiServer extends UnicastRemoteObject implements IRemoteServer {

    /** graphical panel which contains the list of all connected clients*/
    private JList<String> clientArea;
    /** specific model for the <code>clientArea</code> which defines its structure*/
    private DefaultListModel<String> listModel;
    /**
     *  graphical panel which contains all the detail about the operation that the server does
        during its activity
     * */
    private JTextArea logArea;
    /** the generated decision tree*/
    private DecisionTree tree;
    private Data trainingSet;

    /**
     * MultiServer constructor which loads the server on the
     * specified <code>port</code> and create its graphical interface.
     *
     * @throws RemoteException - if some errors occurred while initializing the remote object
     * */
    public MultiServer() throws RemoteException {
        Utility.checkMainFolder();
        Utility.cleanFolder("download");
        Utility.cleanFolder("report");
        Utility.cleanFolder("conversion");
        initAndShowGUI();

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

        logArea.append("Server is waiting for connection...\n");
    }

    /**
     * Generates a specific structure according to the client's request which
     * contains all the information for the current context in which the request
     * is done.
     * <p>
     * The context structure contains all the information about the context which is
     * determined from the specific service that the client needs from the server.
     * For each task, specific context information are needed in order to complete
     * the task. All this kind of information are saved in the generated context structure.
     *
     * @param clientRequest  the client's request
     * @return current context information
     */
    private ContextRequest generateContextRequest(ClientRequest clientRequest){
        // The first attribute is the server command needed to resolve the context
        ServerCommand command = (ServerCommand)clientRequest.getAttribute(0);

        switch (command) {

            case BUILD_TREE_FROM_DB:
                return new ContextRequest(tree, trainingSet, clientRequest.getAttribute(1));
            case SERIALIZE_TREE:
                return new ContextRequest(tree, null, clientRequest.getAttribute(1));
            case LOAD_SERIALIZED_TREE:
                return new ContextRequest(tree, null, clientRequest.getAttribute(1));
            case START_PREDICTION: return new ContextRequest(tree, null, clientRequest.getAttribute(1));
            case SUPPORTED_EXTENSIONS:
                return new ContextRequest(); // No context information needed
            case UPLOAD_FILE:
                try {
                    return new ContextRequest(tree, trainingSet, clientRequest.getAttribute(1), clientRequest.getAttribute(2), getClientHost());
                } catch (ServerNotActiveException e) {
                    System.err.println("ERROR: " + e.getCause() + " -- " + e.getMessage());
                }
            case AVAILABLE_DAT_FILES:
                return new ContextRequest(); // No context information needed
            case SEND_EMAIL:
                try {
                    return new ContextRequest(null, null, clientRequest.getAttribute(1), getClientHost());
                } catch (ServerNotActiveException e) {
                    System.err.println("ERROR: " + e.getCause() + " -- " + e.getMessage());
                }
            case VALIDATE_EMAIL:
                return new ContextRequest(null, null, clientRequest.getAttribute(1));
            case CONVERT_DATASET:
                try {
                    return new ContextRequest(null, trainingSet, clientRequest.getAttribute(1), getClientHost(), logArea);
                } catch (ServerNotActiveException e) {
                    System.err.println("ERROR: " + e.getCause() + " -- " + e.getMessage());
                }
            case CLOSE_CONNECTION:
                try {
                    return new ContextRequest(null, null, clientArea, listModel, getClientHost());
                } catch (ServerNotActiveException e) {
                    System.err.println("ERROR: " + e.getCause() + " -- " + e.getMessage());
                }
            case ATTRIBUTES_INFORMATION: return new ContextRequest(tree, null);
        }

         return null;
    }

    /**
     * Remote method used in order to satisfy the specified request
     * made by the current client. It returns a specific response which contains
     * all the information resulting from the server's computation.
     *
     * @param clientRequest the client's request
     * @return structure which contains the result of the current operation
     * @throws Exception - if some errors occurred while processing the specified client's request
     */
    public ServerResponse satisfyRequest(ClientRequest clientRequest) throws Exception {
        addClient(getClientHost());
        ContextRequest context = generateContextRequest(clientRequest);
        IServerExecutor executor = ServerExecutorFactory.createExecutor(clientRequest);

        ServerResponse response = executor.executeCommand(context);
        updateServerData(context);
        displayOperationResult(response);

        return response;

    }

    /**
     * Displays on the logging area of the server the information about
     * the last computation done, retrieving those information from the specified
     * response structure.
     *
     * @param response the structure which contains the results
     */
    private void displayOperationResult(ServerResponse response){
         Object result = response.getAttribute(1);
        if( result != null ){
            logArea.append(result + "");
        }
    }

    /**
     * Adds the specified client's host to the list of connected client.
     *
     * @param clientHost the current client host
     */
    private void addClient(String clientHost) {
        if(!listModel.contains(clientHost)){
            listModel.addElement(clientHost);
            clientArea.setModel(listModel);
        }
    }

    /**
     * Updates the server main data according to the specified context
     * structure resulting from the latest computation.
     *
     * @param context the current context structure
     */
    private void updateServerData(ContextRequest context){
        if (!context.isEmpty()) {
            if (context.getAttribute(0) != null)
                tree = (DecisionTree) context.getAttribute(0);

            if (context.getAttribute(1) != null)
                trainingSet = (Data) context.getAttribute(1);
        }
    }


}
