package com.mapgroup.classificator.controller;

import com.mapgroup.to.ServerResponse;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This class grants to obtain the list of all the serialized
 * tree that the server has generated.
 */
public class AvailableDatExecutor implements IServerExecutor {
    /**
     * Retrieves all the files's name in which are saved the serialized decision tree and
     * insert them in the server response structure which is returned to the client.
     *
     * <p>
     * The context will not contain any attribute because they aren't needed for this operation.
     *
     * @param contextRequest information about the context in which the computation will be done
     * @return the server response which contains the list of available files
     * @throws Exception - if some errors occurred in retrieving the file list
     */
     public ServerResponse executeCommand(ContextRequest contextRequest) throws Exception {
        File currDir = new File(
                System.getProperty("user.dir")
                        + File.separator + "dataset");
        ServerResponse fileList = new ServerResponse();
        fileList.addAttribute(currDir.list(new FilenameFilter() {
            public boolean accept(File dir, String s) {
                File currFile = new File(s);
                if (currFile.isDirectory())
                    return false;

                String fileName = currFile.getName(), extension = fileName.substring(
                        fileName.lastIndexOf(".") + 1,
                        fileName.length());
                return (extension.equals("dat"));
            }}));

        fileList.addAttribute(null);

        return fileList;
    }
}
