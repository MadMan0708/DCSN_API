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
import java.io.File;
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
public class ClientAPI {

    private IServer remoteService;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private String clientName;

    public ClientAPI(IServer remoteService) {
        this.remoteService = remoteService;
    }

    public String getClientName(){
        return clientName;
    }
    public boolean isProjectReadyForDownload(String projectName) throws RemoteException {
        if (remoteService.isProjectReadyForDownload(clientName, projectName)) {
            return true;
        } else {
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

    public ProgressChecker uploadProject(Path pathToProject, String projectName, int priority) throws RemoteException {
        if (!isProjectExists(projectName)) {
            File fileToUpload = pathToProject.toFile();
            IUpDown uploader = new Uploader(remoteService, fileToUpload, clientName, projectName, priority);
            Future<?> f = executor.submit(uploader);
            return new ProgressChecker(f, uploader);
        } else {
            return null;
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
