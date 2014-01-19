/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.network;

import cz.cuni.mff.bc.api.main.CustomIO;
import cz.cuni.mff.bc.api.main.IServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cojen.dirmi.Pipe;

/**
 * Class used ti upload projects to the server
 *
 * @author Jakub Hava
 */
public class Uploader implements IUpDown {

    private IServer remoteService;
    private Path projectJar;
    private Path projectData;
    private Path temporaryDir;
    private int uploadProgress;
    private long bytesReaded;
    private String clientName;
    private String projectName;
    private int priority;
    private int cores;
    private int memory;
    private int time;
    private File tmp;

    /**
     * Constructor
     *
     * @param remoteService remote interface implementation
     * @param projectJar path to project jar
     * @param projectData path to project data
     * @param temporaryDir path to temporary directory
     * @param clientName client's name
     * @param projectName project name
     * @param priority project priority
     * @param cores number of cores needed by task
     * @param memory amount of memory needed by task
     * @param time average time of task to be calculated
     */
    public Uploader(IServer remoteService, Path projectJar, Path projectData, Path temporaryDir,
            String clientName, String projectName, int priority, int cores, int memory, int time) {
        this.remoteService = remoteService;
        this.projectJar = projectJar;
        this.projectData = projectData;
        this.temporaryDir = temporaryDir;
        this.clientName = clientName;
        this.projectName = projectName;
        this.priority = priority;
        this.cores = cores;
        this.memory = memory;
        this.time = time;
        this.uploadProgress = 0;
        this.bytesReaded = 0;
    }

    private void prepareFileToUpload(File projectJar, File projectData, File tmp) throws IOException {
        CustomIO.zipFiles(tmp, new File[]{projectJar, projectData});
    }

    @Override
    public Object call() throws Exception {
        tmp = Files.createTempFile(temporaryDir, clientName, projectName + ".zip").toFile();
        CustomIO.recursiveDeleteOnShutdownHook(tmp.toPath());
        prepareFileToUpload(projectJar.toFile(), projectData.toFile(), tmp);
        long size = tmp.length();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(tmp));
                Pipe pipe = remoteService.uploadProject(clientName, projectName, priority, cores, memory, time, null)) {
            int n;
            byte[] buffer = new byte[8192];
            while ((n = in.read(buffer)) > 0) {
                pipe.write(buffer, 0, n);
                bytesReaded = bytesReaded + n;
                uploadProgress = (int) Math.ceil(100 / (float) size * bytesReaded);
            }
            pipe.close();
            return null;
        } catch (IOException e) {
            throw new IOException("Problem during accessing project file: " + projectName);
        }
    }

    @Override
    public int getProgress() {
        return this.uploadProgress;
    }

    @Override
    public boolean isCompleted() {
        if (!tmp.exists()) {
            return false;
        } else {
            return bytesReaded == tmp.length();
        }

    }
}
