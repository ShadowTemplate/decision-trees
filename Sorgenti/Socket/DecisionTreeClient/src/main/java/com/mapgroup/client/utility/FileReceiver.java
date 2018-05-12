package com.mapgroup.client.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Provides utilities to receive from the server a file in whatever format.
 */
public class FileReceiver {

    private FileReceiver() {
    }

    /**
     * Checks is the download directory exists.
     * 
     * @param downDir Name of the directory
     */
    public static void checkDownloadDir(String downDir) {
	File dir = new File(downDir);
	if (!dir.exists())
	    dir.mkdir();

    }

    /**
     * Downloads a file from a socket.
     * 
     * @param s Socket that is the source of the file
     * @param fileName Name of the file to be downloaded
     * @throws IOException if some errors occur
     */
    public static void downloadFile(Socket s, String fileName)
	    throws IOException {
	// first read the file size.
	DataOutputStream out = null;

	try {
	    DataInputStream in = new DataInputStream(s.getInputStream());
	    int fileSize = in.readInt();

	    out = new DataOutputStream(new FileOutputStream(fileName));
	    byte[] buffer = new byte[1024 * 4];

	    int n;
	    int totCount = 0;

	    while (totCount != fileSize) {
		n = in.read(buffer);
		out.write(buffer, 0, n);
		totCount += n;
	    }

	} catch (IOException e) {
	    if (out != null)
		out.close();
	    throw e;
	} finally {
	    if (out != null)
		out.close();
	}
    }

}
