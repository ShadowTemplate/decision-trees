package com.mapgroup.client.utility;

import com.mapgroup.to.ServerCommand;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * This class grants to the server to send a file to the client
 */
public class FileSender extends JDialog implements Runnable,
        PropertyChangeListener {

    /**
     * a specific stream needed to transfer some specific data to the server
     */
    private final ObjectOutputStream outStream;
    /**
     * the complete filename of the file that will be sent
     */
    private final String fileName;
    /**
     * the socket which grants the communication with the client
     */
    private final Socket s;
    /**
     * a graphical component which displays download progress
     */
    private final JProgressBar progressMonitor;
    /**
     * A background task that was executed in order to send the file
     */
    private Task task;
    /**
     * exception that maybe be generated in the file sending
     */
    private Exception currException;

    /**
     * File sender constructor which initializes the graphical interface
     * of the file sending windows and starts the file sending.
     *
     * @param s        the client socket that will receive the file
     * @param fileName the filename of the file that will be sent
     * @param out      object stream used to communicate with the server
     * @throws Exception if some errors occurred in the file sending process
     */
    public FileSender(Socket s, ObjectOutputStream out, String fileName)
            throws Exception {
        setTitle("Upload progress");
        setModal(true);
        this.outStream = out;
        this.fileName = fileName;
        this.s = s;
        progressMonitor = new JProgressBar(0, 100);
        progressMonitor.setStringPainted(true);
        Container c = getContentPane();
        this.setLayout(new GridLayout(2, 1, 5, 10));

        c.add(new JLabel("Uploading " + fileName + "\n"));

        c.add(progressMonitor);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                firePropertyChange("closing", null, null);
            }
        });

        setResizable(false);

        sendSuppData();
        /*
      A thread associated with a specific task
     */
        Thread t = new Thread(this);
        t.start();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        while (!task.isDone()) {
        }

        if (currException != null)
            throw currException;
    }

    /**
     * Sends some supplementary data to the server like the specific
     * operation that will be done and the file's extension.
     *
     * @throws IOException - if some error occurred while sending the data
     */
    private void sendSuppData() throws IOException {
        outStream.writeObject(ServerCommand.UPLOAD_FILE);

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());
        outStream.writeObject(extension);
    }

    /**
     * Secondary thread which starts a background operation
     * which grants to send the file.
     */
    public void run() {
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setValue(progress);
        } else if (evt.getPropertyName().equals("done-exception")) {
            task.cancel(true);
            currException = ((Exception) evt.getNewValue());
            JOptionPane.showMessageDialog(null,
                    "Unexpected errors in the communication with the server.\n"
                            + currException.getMessage(), "Upload failed",
                    JOptionPane.ERROR_MESSAGE);

            dispose();
        } else if (evt.getPropertyName().equals("closing")) {
            task.cancel(true);
            JOptionPane.showMessageDialog(null, "Operation canceled.",
                    "Upload failed", JOptionPane.ERROR_MESSAGE);
            dispose();
        } else if (evt.getPropertyName().equals("completed")) {
            JOptionPane.showMessageDialog(null, "File correctly uploaded.",
                    "Operation completed", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    /**
     * This class starts the real file sending process
     * in a separated thread using the SwingWorker facilities.
     */
    private class Task extends SwingWorker<Boolean, Void> {
        /**
         * Opens all the stream of communication and sends the file
         * to the client setting up correctly the progress bar in the
         * windows.
         *
         * @return <code>true</code> when the sending process finishes
         * @throws Exception if some errors occurs in the sending process
         */
        public Boolean doInBackground() throws Exception {

            byte[] buffer = new byte[1024 * 4];

            int n;
            int progress = 0;
            // Initialize progress property.
            setProgress(0);
            DataInputStream input = null;
            try {
                input = new DataInputStream(new FileInputStream(fileName));

                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                int totFileSize = (int) new File(fileName).length();

                output.writeInt(totFileSize); // send to the client the fileSize

                while ((n = input.read(buffer)) != -1 && !isCancelled()) {
                    output.write(buffer, 0, n);
                    progress += n;

                    setProgress((progress * 100) / totFileSize);
                }
                output.flush();


            } catch (IOException e) {
                if (input != null)
                    input.close();
                throw e;
            } finally{
                if (input != null)
                    input.close();
            }
            return true;
        }

        /**
         * A method that was automatically called when the {@link #doInBackground()} method
         * completes its execution.
         * It controls the status of the process and notify a specific change in the process
         * execution to the other graphical components.
         */
        public void done() {
            try {
                Boolean res = get();
                if (res != null && res)
                    firePropertyChange("completed", null, null);
            } catch (ExecutionException e) {
                firePropertyChange("done-exception", null, e);

            } catch (InterruptedException e) {
                firePropertyChange("done-exception", null, e);

            } catch (Exception e) {
                firePropertyChange("done-exception", null, e);
            }
        }
    }
}