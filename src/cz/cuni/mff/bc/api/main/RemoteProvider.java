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
 *
 * @author Jakub
 */
public class RemoteProvider {

    private IServer remoteService;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private String clientName;
    private Path currentJar;

    public RemoteProvider(IServer remoteService, String clientName, Path currentJar) {
        this.remoteService = remoteService;
        this.clientName = clientName;
        this.currentJar = currentJar;
    }
    
    public RemoteProvider(IServer remoteService, String clientName) {
        this(remoteService, clientName, null);
    }

    public String getClientName() {
        return clientName;
    }

    public Path getCurrentJarPath() {
        return currentJar;
    }

    public boolean isProjectReadyForDownload(String projectName) throws RemoteException {
        if (remoteService.isProjectReadyForDownload(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isConnected() {
        try {
            return remoteService.isConnected(clientName);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isProjectExists(String projectName) throws RemoteException {
        if (remoteService.isProjectExists(clientName, projectName)) {
            return true;
        } else {
            return false;
        }

    }

    public ProgressChecker downloadProject(String projectName, Path target) throws RemoteException {
        if (isProjectReadyForDownload(projectName)) {
            IUpDown downloader = new Downloader(remoteService, clientName, projectName, target);
            Future<?> f = executor.submit(downloader);
            return new ProgressChecker(f, downloader);
        } else {
            return null;
        }
    }

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

    public boolean cancelProject(String projectName) throws RemoteException {
        if (remoteService.cancelProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean pauseProject(String projectName) throws RemoteException {
        if (remoteService.pauseProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean unpauseProject(String projectName) throws RemoteException {
        if (remoteService.unpauseProject(clientName, projectName)) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ProjectInfo> getProjectList() throws RemoteException {
        return remoteService.getProjectList(clientName);
    }

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
    /* private void automatic(File alter, String projectName, int projectPriority, File alterClass, int numberOfCycles) {
     if (numberOfCycles == 0) {
     executor.submit(new Automatic(this, alter, projectName, projectPriority, alterClass));
     } else {
     executor.submit(new Automatic(this, alter, projectName, projectPriority, alterClass, numberOfCycles));
     }
     }*/
}
