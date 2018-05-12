package com.mapgroup.client.utility;

import java.io.*;

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
	public static void checkDownloadDir(String downDir){
	    File dir = new File(downDir);
	    if( !dir.exists() )
		dir.mkdir();		
	    
	}

    /**
     * Retrieves the file content of the download file and saves it in a file
     * whose name is represented by the specified value.
     *
     * @param fileContent the downloaded file content
     * @param fileName the downloaded file name
     * @throws IOException - if some errors occurred while saving the file
     */
	public static void downloadFile(byte[] fileContent, String fileName) throws IOException {
		// first read the file size.
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));

        out.write(fileContent);

		out.close();
	}

	
}
