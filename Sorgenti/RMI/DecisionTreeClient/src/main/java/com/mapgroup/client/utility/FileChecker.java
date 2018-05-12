package com.mapgroup.client.utility;

import java.io.File;

/**
 * Provides utilities to check if a file can be sent to the server.
 */
public class FileChecker {
    /** Maximum size for the file: 25 MB*/
	private static final long MAXSIZE = 25000000;

    /**
     * Checks if the file exceed the maximum size.
     *
     * @param fileName Name of the file to be checked.
     * @return a message containing the result of the operation
     */
	public static String checkFile(String fileName)
	{
		File f = new File(fileName);
		
		if( f.length() > MAXSIZE)
			return "The specified file exceed the maximum file size (" + MAXSIZE/1000000 + " MB)";
		
		return "";
		
	}

}
