package com.mapgroup.classificator.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class grants to the server to receive a specific file from the
 * client using sockets.
 *
 * */
public class FileReceiver {

	private FileReceiver() {
	}

    /**
     * Grants to receive the file sent by the client.&nbsp;
     * <p>
     * The received file will have as extension the value contained in <code> extension</code> and will be
     * saved in the directory <code>downDir</code>.
     * <p>
     * The resulting file name was generated using the information about the client like the Ip address
     * and the port on which it is connected, which are contained in the socket <code>s</code>.
     * They are used in order to avoid some collisions while the server is saving the file.
     * The file format will be:
     * #CLIENT-IP(#CLIENT-PORT).#FILE-EXTENSION
     * </p>
     * </p>
     *
     * @param s the client's socket of the client which is sending the file
     * @param downDir the directory in which the file will be saved
     * @param extension the extension of the file that the server is receiving
     * @return the complete pathname of the file received
     * @throws IOException if there are some errors in receiving the file
     */
	public static String downloadFile(Socket s, String downDir, String extension) throws IOException {
	
	    String fileName = downDir + s.getInetAddress().getHostAddress() + "("
			+ s.getPort() + ")" + "." + extension; 

		DataInputStream in = new DataInputStream(s.getInputStream());
        // first read the file size.
        int fileSize = in.readInt();

		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				fileName));
		byte[] buffer = new byte[1024 * 4];

		int n;
		int totCount = 0;

		while (totCount != fileSize) {
			n = in.read(buffer);
			out.write(buffer, 0, n);
			totCount += n;
		}

		out.close();
		
		return fileName;
	}
}
