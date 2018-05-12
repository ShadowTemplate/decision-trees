package com.mapgroup.client.utility;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

/**
 * This class grants to the server to send a file to the client
 */
public class FileSender extends JDialog implements Runnable, PropertyChangeListener {

    /**
     * the complete filename of the file that will be sent
     */
    private final String fileName;
    /**
     * result string message which contains information about the current operation
     */
    private String resultMsg;
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
     * file content as a byte array
     */
    private byte[] fileContent;

    /**
     * File sender constructor which initializes the graphical interface
     * of the file sending windows and starts the file sending.
     *
     * @param fileName the filename of the file that will be sent
     * @throws Exception if some errors occurred in the file sending process
     */
    public FileSender(String fileName) throws Exception {
        setTitle("Upload progress");
        setModal(true);
        this.fileName = fileName;
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
        Thread t = new Thread(this);
        t.start();
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // wait until the conversion process ends
        while (!task.isDone()) {
        }
        if (currException != null)
            throw currException;

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
     * Returns the file content represented as a byte array, a comfortable
     * format with which the file will be sent to the server.
     *
     * @return the file content as a byte array
     */
    public byte[] getTransferredFile() {
        return fileContent;
    }

    /**
     * Returns the message associated to the completed operation.
     * <p>
     * The message will be displayed when the process ends in a proper
     * way by the client's graphical interface.
     *
     * @return the message that will be displayed
     */
    public String getResultMessage() {
        return resultMsg;
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
            resultMsg = "Unexpected errors in the comunication with the client.\n"
                    + ((Exception) evt.getNewValue()).getMessage();
            currException = (Exception) evt.getNewValue();

            dispose();
        } else if (evt.getPropertyName().equals("closing")) {
            task.cancel(true);
            resultMsg = "File " + fileName + " not uploaded.\n";
            dispose();

        } else if (evt.getPropertyName().equals("completed")) {

            resultMsg = "File " + fileName + " correctly uploaded.\n";
            dispose();

        }

    }

    /**
     * This class starts the real file sending process
     * in a separated thread using the SwingWorker facilities.
     */
    private class Task extends SwingWorker<Boolean, Void> {
        /**
         * Reads the file that will be uploaded and sets up correctly the progress bar in the
         * windows in order to display the upload state.
         *
         * @return <code>true</code> when the sending process finishes
         * @throws Exception - if some errors occurs in the sending process
         */
        public Boolean doInBackground() throws Exception {

            byte[] buffer = new byte[1024 * 4];

            int n;
            int progress = 0;
            // Initialize progress property.
            setProgress(0);


            int totFileSize = (int) new File(fileName).length();
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileName));
            ByteArrayOutputStream out = new ByteArrayOutputStream(totFileSize);

            while ((n = input.read(buffer)) != -1 && !isCancelled()) {
                out.write(buffer, 0, n);
                progress += n;
                setProgress((progress * 100) / totFileSize);
            }

            fileContent = out.toByteArray();

            out.close();
            input.close();

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