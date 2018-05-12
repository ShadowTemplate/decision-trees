package com.mapgroup.classificator.boundary;

import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.database.IncorrectLoginException;
import com.mapgroup.classificator.database.SupportedDBMS;
import com.mapgroup.classificator.database.dao.DbAccess;
import com.mapgroup.classificator.database.dao.DbAccessFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * This class represents the login panel of the server application
 * in which the operator will insert his SQL DBMS credentials.
 * <p>
 * As SQL DBMS credentials was intended the username and password
 * for the root user of the specific DBMS that was selected in the dialog.
 * For example MySQL adopt this specific credentials to grant root access:
 * <code>
 * <ul>
 * <li>Username: root</li>
 * <li>Password: the_root_password</li>
 * </ul>
 * </code>
 * </p>
 */
public class LoginForm {
    /**
     * Text box in which the user was able to insert his username
     */
    private static JTextField user;
    /**
     * Text box in which the user was able to insert his password
     */
    private static JPasswordField password;
    /**
     * Contains the DBMS that the server supports
     */
    private static JComboBox<String> DBMSBox;
    /**
     * A flag that grant to check if the user has inserted correct credentials
     */
    private boolean correctUser = false;
    /**
     * The graphical component which contains the login panel
     */
    private final JFrame cp = new JFrame("Admin access");

    /**
     * <code>main</code> method for this class.
     * Starts the program and check for the correctness of the server
     * parameters.
     *
     * @param args Command line arguments
     * */
    public static void main(String[] args) {
        int port = 2367;
        new LoginForm(port);
        /*if (args.length != 0) {
            int userPort = Integer.parseInt(args[0]);
            if (userPort < 1024 || userPort > 49151) // Invalid port specified
            {
                System.err
                        .println("Invalid port specified. Default port will be used instead.");
                new LoginForm(port);
            } else
                new LoginForm(userPort);
        } else
            new LoginForm(port); */

    }

    /**
     * Initializes all the graphical interface of the login panel.
     * @param port the port on which the server will be started
     *
     * */
    public LoginForm(final int port) {
        cp.setResizable(false);
        cp.setSize(300, 250);
        cp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cp.setLocationRelativeTo(null); // Centra il JFrame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 5));
        panel.setBorder(BorderFactory
                .createTitledBorder("Database information"));

        panel.add(new JLabel("DBMS:"));
  
        //A specific model for the DBMSBox
        DefaultComboBoxModel<String> DBMSModel = new DefaultComboBoxModel<String>(
                SupportedDBMS.getSupportedDBMSName());
        DBMSBox = new JComboBox<String>(DBMSModel);
        panel.add(DBMSBox);

        panel.add(new JLabel("User:"));
        user = new JTextField();
        user.setMaximumSize(new Dimension(300, 20));
        panel.add(user);
        panel.add(new JLabel("Password:"));
        password = new JPasswordField();
        password.setMaximumSize(new Dimension(300, 20));
        panel.add(password);
        password.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    tryLogin(port);
                }
            }
        });

        JButton login = new JButton("Login");
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryLogin(port);
            }
        });
        login.setAlignmentX(JButton.CENTER_ALIGNMENT);
        login.setIcon(new ImageIcon("key.png"));
        login.setIconTextGap(-5);

        login.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    tryLogin(port);
                }
            }
        });

        panel.add(login);
        cp.add(panel);
        cp.setVisible(true);
    }

    /**
     * Returns the boolean value of <code>correctUser</code>
     *
     * @return <code>true</code> if the user insert the correct credentials, <code>false</code> otherwise
     *
     * */
    private boolean isCorrectUser() {
        return correctUser;
    }

    /**
     *  Gets all the data from the graphical components in order to
     *  try the login to the specified DBMS and start the server
     *  on the specified port.
     *
     *  @param port the port on which the server will be started
     * */
    private void tryLogin(int port) {
        String passText = new String(password.getPassword());

        /*
            Initializes the database connection using
            username and password inserted in the specified boxes.
        */
        try {
            DbAccess databaseConn = DbAccessFactory
                    .createDbAccess((String) DBMSBox.getSelectedItem());
            databaseConn.initConnection(user.getText(), passText);//
            correctUser = true;
        }
        catch (DataException e) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Unable to connect to the database.\n" + e.getMessage(),
                            "ERROR", JOptionPane.ERROR_MESSAGE);

            password.setText("");
        } catch (IncorrectLoginException e) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Unable to connect to the database.\nWrong combination of username and password.",
                            "Authentication failure", JOptionPane.ERROR_MESSAGE);

            password.setText("");
        }

        // if the inserted credentials are correct, starts the server on the specified port.
        if (isCorrectUser()) {
            try {
                cp.dispose();
                //System.getProperty();
                MultiServer multi = new MultiServer();
                LocateRegistry.createRegistry(port);
                Naming.bind("//localhost:" + port + "/MultiServer", multi);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Some errors were occurred in initializing the server: "
                                + e.getMessage(), "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            } catch (AlreadyBoundException e) {
                System.err.println("ERROR: " + e.getMessage());
                
            }
        }
    }
}
