/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.network;

import cz.cuni.mff.bc.api.main.IServer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import org.cojen.dirmi.Pipe;

/**
 * Class used to download projects from the server
 *
 * @author Jakub Hava
 */
public class Downloader implements IUpDown {

    private boolean hasStarted = false;
    private IServer remoteService;
    private int downloadProgress;
    private long bytesReaded;
    private String projectName;
    private String clientName;
    private File downloadFile;
    private long downloadFileLength;

    /**
     * Constructor
     *
     * @param remoteService remote interface implementation
     * @param clientName client's name
     * @param projectName project name
     * @param destination path where file will be downloaded
     */
    public Downloader(IServer remoteService, String clientName, String projectName, Path destination) {
        this.remoteService = remoteService;
        this.projectName = projectName;
        this.clientName = clientName;
        this.downloadProgress = 0;
        this.bytesReaded = 0;
        this.downloadFile = destination.toFile();
    }

    @Override
    public Object call() throws Exception {
        try {
            downloadFileLength = remoteService.getProjectFileSize(clientName, projectName);
        } catch (RemoteException e) {
            throw new RemoteException("File length couldn't be find out due to network error");
        }
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(downloadFile));
                Pipe pipe = remoteService.downloadProject(clientName, projectName, null)) {

            int n;
            byte[] buffer = new byte[8192];
            hasStarted = true;
            while ((n = pipe.read(buffer)) > -1) {
                out.write(buffer, 0, n);
                bytesReaded = bytesReaded + n;
                downloadProgress = (int) Math.ceil(100 / (float) downloadFileLength * bytesReaded);
            }

            pipe.close();
            return null;
        } catch (IOException e) {
            throw new IOException("Problem durring accessing project file: " + projectName);
        }
    }

    @Override
    public int getProgress() {
        return this.downloadProgress;
    }

    @Override
    public boolean hasStarted() {
        return hasStarted;

    }

    @Override
    public boolean hasCompleted() {
        return bytesReaded == downloadFile.length();
    }
}
