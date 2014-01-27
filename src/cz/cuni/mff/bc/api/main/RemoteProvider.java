/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.network.ProgressChecker;
import cz.cuni.mff.bc.api.enums.ProjectState;
import cz.cuni.mff.bc.api.network.Downloader;
import cz.cuni.mff.bc.api.network.IUpDown;
import cz.cuni.mff.bc.api.network.Uploader;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Provides basic remote methods from the server. Using these methods requires
 * own RemoteException handling and logging implementation if needed
 *
 * @author Jakub Hava
 */
public class RemoteProvider {

    private IServer remoteService;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private String clientName;
    private Path currentJar;
    private Path uploadDir;
    private Path downloadDir;
    private Path temporaryDir;

    /**
     * Constructor
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param uploadDir upload directory
     * @param temporaryDir temporary directory
     * @param currentJar path to project jar
     */
    public RemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Path temporaryDir, Path currentJar) {
        this.remoteService = remoteService;
        this.clientName = clientName;
        this.currentJar = currentJar;
        this.uploadDir = uploadDir;
        this.downloadDir = downloadDir;
        this.temporaryDir = temporaryDir;
    }

    /**
     * Constructor
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param temporaryDir temporary directory
     * @param uploadDir upload directory
     */
    public RemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Path temporaryDir) {
        this(remoteService, clientName, downloadDir, uploadDir, temporaryDir, null);
    }

    /**
     * Gets the client's name
     *
     * @return client's name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Gets the path to the temporary directory which has been set in console
     *
     * @return path to the temporary directory
     */
    public Path getStandartTemporaryDir() {
        return temporaryDir;
    }

    /**
     * Gets the path to the download directory which has been set in console
     *
     * @return path to the download directory
     */
    public Path getStandartDownloadDir() {
        return downloadDir;
    }

    /**
     * Gets the path to the upload directory which has been set in console
     *
     * @return path to the upload directory
     */
    public Path getStandartUploadDir() {
        return uploadDir;
    }

    /**
     * Gets the path to the current project jar
     *
     * @return path to current project jar
     */
    public Path getCurrentJarPath() {
        return currentJar;
    }

    /**
     * Sends to the server information about amount of memory which can be used
     * during task computation
     *
     * @param memory memory limit
     * @throws RemoteException
     */
    public void setMemoryLimit(int memory) throws RemoteException {
        remoteService.setClientsMemoryLimit(clientName, memory);
    }

    /**
     * Sends to server information about number of cores which can be used
     * during task computation
     *
     * @param cores cores limit
     * @throws RemoteException
     */
    public void setCoresLimit(int cores) throws RemoteException {
        remoteService.setClientsCoresLimit(clientName, cores);
    }

    /**
     * Checks if the project is ready for download
     *
     * @param projectName project name
     * @return true if the project is ready for download, false otherwise
     * @throws RemoteException
     */
    public boolean isProjectReadyForDownload(String projectName) throws RemoteException {
        if (remoteService.isProjectReadyForDownload(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the client is connected
     *
     * @return true if the client is connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return remoteService.isConnected(clientName);
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Checks if the client has some tasks in progress
     *
     * @return true if client has some tasks in progress, false otherwise
     * @throws RemoteException
     */
    public boolean hasClientTasksInProgress() throws RemoteException {
        if (remoteService.hasClientTasksInProgress(clientName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the project exists
     *
     * @param projectName project name
     * @return true if the project exists, false otherwise
     * @throws RemoteException
     */
    public boolean isProjectExists(String projectName) throws RemoteException {
        if (remoteService.isProjectExists(clientName, projectName)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Downloads the project
     *
     * @param projectName project name
     * @param target path where the project will be downloaded
     * @return ProgressChecker - object used to get information about
     * downloading status
     * @throws RemoteException
     */
    public ProgressChecker downloadProject(String projectName, Path target) throws RemoteException {
        if (isProjectReadyForDownload(projectName)) {
            IUpDown downloader = new Downloader(remoteService, clientName, projectName, target);
            Future<?> f = executor.submit(downloader);
            return new ProgressChecker(f, downloader);
        } else {
            return null;
        }
    }

    /**
     * Uploads the project
     *
     * @param projectJar path to project jar
     * @param projectData path to project data
     * @return ProgressChecker - object used to get information about uploading
     * status
     * @throws RemoteException
     * @throws IOException
     */
    public ProgressChecker uploadProject(Path projectJar, Path projectData) throws RemoteException, IOException {
        try {
            String projectName = JarTools.getAttributeFromManifest(projectJar, "Project-Name");
            int priority = Integer.parseInt(JarTools.getAttributeFromManifest(projectJar, "Project-Priority"));
            int cores = Integer.parseInt(JarTools.getAttributeFromManifest(projectJar, "Cores-Per-Task"));
            int memory = Integer.parseInt(JarTools.getAttributeFromManifest(projectJar, "Memory-Per-Task"));
            int time = Integer.parseInt(JarTools.getAttributeFromManifest(projectJar, "Time-Per-Task"));
            if (!isProjectExists(projectName)) {
                IUpDown uploader = new Uploader(remoteService, projectJar, projectData, temporaryDir, clientName, projectName, priority, cores, memory, time);
                Future<?> f = executor.submit(uploader);
                return new ProgressChecker(f, uploader);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            throw new IOException("Incorrect values in jar manifest file:", e);
        }
    }

    /**
     * Cancels the project
     *
     * @param projectName project name
     * @return true if project has been successfully cancelled, false if the
     * project is in preparing phase or null if the project doesn't exist
     * @throws RemoteException
     */
    public Boolean cancelProject(String projectName) throws RemoteException {
        return remoteService.cancelProject(clientName, projectName);
    }

    /**
     * Pauses the project
     *
     * @param projectName project name
     * @return Null if the project doesn't exist on the server or project state
     * before pausing. Project has been paused only if the previous project
     * state was set to ACTIVE
     * @throws RemoteException
     */
    public ProjectState pauseProject(String projectName) throws RemoteException {
        return remoteService.pauseProject(clientName, projectName);

    }

    /**
     * Resumes the project
     *
     * @param projectName project name
     * @return Null if the project doesn't exist on the server or project state
     * before resuming. Project has been resumed only if the previous project
     * state was set to PAUSED
     * @throws RemoteException
     */
    public ProjectState resumeProject(String projectName) throws RemoteException {
        return remoteService.resumeProject(clientName, projectName);
    }

    /**
     * Marks the project as corrupted
     *
     * @param ownerName name of the owner of the project
     * @param projectName project name
     * @throws RemoteException
     */
    public void markProjectAsCurrupted(String ownerName, String projectName) throws RemoteException {
        remoteService.markProjectAsCorrupted(ownerName, projectName);
    }

    /**
     * Gets list of all client's projects
     *
     * @return list of projects
     * @throws RemoteException
     */
    public ArrayList<ProjectInfo> getProjectList() throws RemoteException {
        return remoteService.getProjectList(clientName);
    }

    /**
     * Gets list of client's projects in given state
     *
     * @param state project state to filter the project list
     * @return list of projects
     * @throws RemoteException
     */
    public ArrayList<ProjectInfo> getProjectList(ProjectState state) throws RemoteException {
        ArrayList<ProjectInfo> projects = new ArrayList<>();
        ArrayList<ProjectInfo> allProjects = getProjectList();
        for (ProjectInfo projectInfo : allProjects) {
            if (projectInfo.getState() == state) {
                projects.add(projectInfo);
            }
        }
        return projects;
    }
}
