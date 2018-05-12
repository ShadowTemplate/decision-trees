package com.mapgroup.classificator.utility;

import java.io.File;

/**
 * Supplementary class which contains some useful methods
 * used to control and manage files and directories.
 */
public class Utility {

    private Utility() {
    }

    /**
     * Checks if already exists a file with name <code>fileName</code>.
     * <p>
     * There is the possibility to pass to the function
     * a single file name or the whole file path and the function
     * will try to control if the specified file already exists or not.
     * </p>
     *
     * @param fileName the file path that will be checked
     * @return <code>true</code> if the file exists, <code>false</code> otherwise
     */
    private static boolean nonExistsFile(String fileName) {
        File f = new File(fileName);
        return !f.exists();
    }

    /**
     * Checks if the directory with name <code>currDir</code>
     * already exists or not. If the specified directory doesn't exist
     * it creates it.
     *
     * @param currDir directory name that will be checked
     */
    public static void checkDirectory(String currDir) {
        File dir = new File(currDir);
        if (!dir.exists())
            dir.mkdir();
    }

    /**
     * Checks if the specified file name, <code>fileName</code>, was
     * already present and in this case it retrieves another valid
     * name in the same directory.
     * <p>
     * If the specified file name doesn't exist the same file name was
     * returned by the function.
     * </p>
     * <p/>
     * <p>
     * If there are some other occurrences of the <code>fileName</code>
     * in its directory, the file name generation will follow this rule:
     * #OLD_FILE_NAME(#NUMBER_OF_OCCURRENCES)
     * </p>
     * <p/>
     * <p>For example if we have this specific directory organization:
     * <pre>
     *  + myDir/
     *      - myFile.dat
     *      - prova.dat
     *      - test.dat
     *      - myFile(1).dat
     * </pre>
     * A call to getAvailableName("C:/myDir/prova.dat") will return "C:/myDir/prova(1).dat";
     * Instead, getAvailableName("C:/myDir/myFile.dat") will return "C:/myDir/myFile(2).dat";
     * <p/>
     * <p>
     * The specified <code>fileName</code> must be an absolute path in order to
     * obtain the desired result.
     * </p>
     *
     * @param fileName the old path of the file
     * @return the available file name in the specific directory
     */
    public static String getAvailableName(String fileName) {
        if (nonExistsFile(fileName))
            return fileName;

        String noExtName = fileName.substring(0, fileName.length() - 4);
        String extension = fileName.substring(fileName.length() - 3,
                fileName.length());
        int counter = 1;
        String newName;
        while (true) {
            newName = noExtName + "(" + counter + ")." + extension;
            if (nonExistsFile(newName))
                break;
            counter++;
        }

        return newName;
    }

    /**
     * Removes all the file contained in the folder with name <code>folderName</code>
     * present in the main directory of the server.
     * <p>
     * Obviously, if the directory doesn't exists nothing was done.
     * </p>
     *
     * */
    public static void cleanFolder(String folderName) {
        File folderPath = new File(System.getProperty("user.dir")
                + File.separator + folderName);
        if (folderPath.exists()) {
            File[] dirFiles = folderPath.listFiles();
            if (dirFiles != null) {
                for (File f : dirFiles) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Checks if the server's main directory exist and if they aren't,
     * it creates them.
     * <p>
     * The main directory that will be checked are:
     * <ul>
     *     <li>/download</li>
     *     <li>/conversion</li>
     *     <li>/report</li>
     *     <li>/dataset</li>
     * </ul>
     * </p>
     *
     * <p>
     * If one of the specified directory already exists, no operation on it
     * will be done.
     * </p>
     *
     * */
    public static void checkMainFolder() {
        String path = System.getProperty("user.dir") + File.separator;

        File dir = new File(path + "dataset");
        if (!dir.exists())
            dir.mkdir();

        dir = new File(path + "download");
        if (!dir.exists())
            dir.mkdir();

        dir = new File(path + "report");
        if (!dir.exists())
            dir.mkdir();

        dir = new File(path + "conversion");
        if (!dir.exists())
            dir.mkdir();
    }

    /**
     * Generates an encrypted string starting from
     * the ip address provided to the method.
     * <code>
     * A specific mapping function will be used in order
     * to convert each character that compounds the
     * ip address to a specific letter.
     * </code>
     *
     * @param ip ip address that will be converted
     * @return a string which contains only letters
     *
     * */
    public static String encryptIP(String ip) {

        String encrypted = "";
        for (int i = 0; i < ip.length(); i++)
            encrypted += mapChar(ip.charAt(i));

        return encrypted;
    }

    /**
     * Converts the specified character <code>temp</code>
     * to a specific letter.
     * @param temp the character to convert
     * @return returns the specific mapped character
     *
     * */
    private static char mapChar(char temp) {

        switch (temp) {
            case '0':
                return 't';
            case '1':
                return 'A';
            case '2':
                return 'r';
            case '3':
                return 'd';
            case '4':
                return 'I';
            case '5':
                return 'n';
            case '6':
                return 'Q';
            case '7':
                return 'x';
            case '8':
                return 'l';
            case '9':
                return 'h';
            case '(':
                return 'U';
            case ')':
                return 'W';
            case '.':
                return 'J';
            case ' ':
                return 'S';
            default:
                return 'Z';
        }
    }
}
