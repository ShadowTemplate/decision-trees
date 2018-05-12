package com.mapgroup.classificator.boundary;

import com.mapgroup.classificator.database.DataException;
import com.mapgroup.classificator.database.IncorrectLoginException;
import com.mapgroup.classificator.database.SupportedDBMS;
import com.mapgroup.classificator.database.dao.DbAccess;
import com.mapgroup.classificator.database.dao.DbAccessFactory;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
class LoginForm {
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
        int port = 2345;
        if (args.length != 0) {
            int userPort = Integer.parseInt(args[0]);
            if (userPort < 1024 || userPort > 49151) // Invalid port specified
            {
                System.err
                        .println("Invalid port specified. Default port will be used instead.");
                new LoginForm(port);
            } else
                new LoginForm(userPort);
        } else
            new LoginForm(port);
    }

    /**
     * Initializes all the graphical interface of the login panel.
     * @param port the port on which the server will be started
     *
     * */
    private LoginForm(final int port) {
        cp.setResizable(false);
        cp.setSize(300, 250);
        cp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cp.setLocationRelativeTo(null); // Centra il JFrame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 5));
        panel.setBorder(BorderFactory
                .createTitledBorder("Database information"));

        panel.add(new JLabel("DBMS:"));
        /*
      A specific model for the <code>DBMSBox</code>
     */
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
                cp.dispose();
                new MultiServer(port);
        }
    }
}
