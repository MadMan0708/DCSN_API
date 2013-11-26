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

/**
 * Provides basic methods from server. Using these methods requires own
 * RemoteException handling and logging if needed
 *
 * @author Jakub
 */
public class RemoteProvider {

    private IServer remoteService;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private String clientName;
    private Path currentJar;

    /**
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param currentJar path to project jar
     */
    public RemoteProvider(IServer remoteService, String clientName, Path currentJar) {
        this.remoteService = remoteService;
        this.clientName = clientName;
        this.currentJar = currentJar;
    }

    /**
     *
     * @param remoteService remote interface
     * @param clientName client name
     */
    public RemoteProvider(IServer remoteService, String clientName) {
        this(remoteService, clientName, null);
    }

    /**
     *
     * @return client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     *
     * @return path to project jar
     */
    public Path getCurrentJarPath() {
        return currentJar;
    }

    /**
     *
     * @param projectName project name
     * @return true if project is ready for download, false otherwise
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
     *
     * @return true if client is connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return remoteService.isConnected(clientName);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean hasClientTasksInProgress() throws RemoteException {
        if (remoteService.hasClientTasksInProgress(clientName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param projectName project name
     * @return true if project exists, false otherwise
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
     *
     * @param projectName project name
     * @param target path, where the project will be downloaded
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
            String projectName = JarAPI.getAttributeFromManifest(projectJar, "Project-Name");
            int priority = Integer.parseInt(JarAPI.getAttributeFromManifest(projectJar, "Project-Priority"));
            if (!isProjectExists(projectName)) {
                IUpDown uploader = new Uploader(remoteService, projectJar, projectData, clientName, projectName, priority);
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
     *
     * @param projectName project name
     * @return true if project has been cancelled, false if project doesn't
     * exist
     * @throws RemoteException
     */
    public boolean cancelProject(String projectName) throws RemoteException {
        if (remoteService.cancelProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param projectName project name
     * @return true if project has been paused, false if project doesn't exist
     * @throws RemoteException
     */
    public boolean pauseProject(String projectName) throws RemoteException {
        if (remoteService.pauseProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param projectName project name
     * @return true if project has been resumed, false if project doesn't exist
     * @throws RemoteException
     */
    public boolean resumeProject(String projectName) throws RemoteException {
        if (remoteService.resumeProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return list of all client's projects
     * @throws RemoteException
     */
    public ArrayList<ProjectInfo> getProjectList() throws RemoteException {
        return remoteService.getProjectList(clientName);
    }

    /**
     *
     * @param state project state to filter projects
     * @return list of client's project with given state
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
