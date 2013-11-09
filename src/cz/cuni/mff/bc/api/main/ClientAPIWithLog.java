/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.enums.ProjectState;
import cz.cuni.mff.bc.api.network.ProgressChecker;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class ClientAPIWithLog {
    
    private static final Logger LOG = Logger.getLogger(ClientAPIWithLog.class.getName());
    private ClientAPI clientAPI;
    
    public ClientAPIWithLog(IServer remoteService, Handler logHandler) {
        this.clientAPI = new ClientAPI(remoteService);
        LOG.addHandler(logHandler);
    }
    
    public ClientAPI getClientAPI() {
        return clientAPI;
    }
    
    public Boolean pauseProject(String projectName) {
        try {
            if (clientAPI.pauseProject(projectName)) {
                LOG.log(Level.INFO, "Project {0} was successfuly paused", projectName);
                return true;
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Prolem during pausing project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }
    
    public Boolean unpauseProject(String projectName) {
        try {
            if (clientAPI.unpauseProject(projectName)) {
                LOG.log(Level.INFO, "Project {0} was successfuly unpaused", projectName);
                return true;
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Prolem during unpausing project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }
    
    public Boolean cancelProject(String projectName) {
        try {
            if (clientAPI.cancelProject(projectName)) {
                LOG.log(Level.INFO, "Project {0} was successfuly canceled", projectName);
                return false;
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Prolem during canceling project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }
    
    public Boolean isProjectReadyForDownload(String projectName) {
        try {
            if (clientAPI.isProjectReadyForDownload(projectName)) {
                LOG.log(Level.INFO, "Project {0} is ready for download", projectName);
                return true;
            } else {
                LOG.log(Level.INFO, "Project {0} is not ready for download", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during determining project state due to network error{0}", e.getMessage());
            return null;
        }
    }
    
    public void download(String projectName, String downloadDir) {
        try {
            final String projectNameLocal = projectName;
            final ProgressChecker pc = clientAPI.downloadProject(projectName, Paths.get(downloadDir + File.separator + projectName + ".zip"));
            if (pc != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.log(Level.INFO, "Project: {0}, Downloaded: 0 %...", projectNameLocal);
                        while (pc.isInProgress()) {
                            LOG.log(Level.INFO, "Project: {0}, Downloaded: {1} %...", new Object[]{projectNameLocal, pc.getProgress()});
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                LOG.log(Level.INFO, "Progress checking during downloading has been interupted: {0}", e.getMessage());
                            }
                        }
                        try {
                            pc.wasSuccesfull();
                            LOG.log(Level.INFO, "Project: {0}, Downloaded: 100 %...", projectNameLocal);
                            LOG.log(Level.INFO, "Project {0} has been downloaded", projectNameLocal);
                        } catch (RemoteException e) {
                            LOG.log(Level.WARNING, "Problem with network during downloading: {0}", e.getMessage());
                        } catch (IOException e) {
                            LOG.log(Level.WARNING, "Problem with accessing file on server side: {0}", e.getMessage());
                        }
                    }
                }).start();
            } else {
                LOG.log(Level.WARNING, "Project {0} is not ready for download", projectName);
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem with network during downloading: {0}", e.getMessage());
        }
    }
    
    public void uploadProject(Path pathToProject, String projectName, int projectPriority) {
        try {
            final String projectNameLocal = projectName;
            final ProgressChecker pc = clientAPI.uploadProject(pathToProject, projectName, projectPriority);
            if (pc != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.log(Level.INFO, "Project: {0}, Uploaded: 0 %...", projectNameLocal);
                        while (pc.isInProgress()) {
                            LOG.log(Level.INFO, "Project: {0}, Uploaded: {1} %...", new Object[]{projectNameLocal, pc.getProgress()});
                            LOG.log(Level.INFO, "Project: {0}, Uploaded: 100 %...", projectNameLocal);
                            try {
                                Thread.sleep(800);
                            } catch (InterruptedException e) {
                                LOG.log(Level.INFO, "Progress checking during uploading has been interupted: {0}", e.getMessage());
                            }
                        }
                        try {
                            pc.wasSuccesfull();
                            LOG.log(Level.INFO, "Project: {0}, Uploaded: 100 %...", projectNameLocal);
                            LOG.log(Level.INFO, "Project {0} has been uploaded", projectNameLocal);
                        } catch (IOException e) {
                            LOG.log(Level.WARNING, "Problem with accessing file: {0}", e.getMessage());
                        }
                    }
                }).start();
            } else {
                LOG.log(Level.INFO, "Project with name {0} already exists", projectName);
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem with network during uploading: {0}", e.getMessage());
        }
    }
    
    public void printAllProjects() {
        int i = 1;
        try {
            ArrayList<ProjectInfo> pAll = clientAPI.getProjectList();
            for (ProjectInfo projectInfo : pAll) {
                LOG.log(Level.INFO, "{0}: {1}", new Object[]{i, projectInfo.toString()});
                i++;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Project list couldn''t be obtained due to network error: {0}", e.getMessage());
        }
    }
    
    public void printProjects(ProjectState state) {
        int i = 1;
        try {
            ArrayList<ProjectInfo> pAll = clientAPI.getProjectList(state);
            for (ProjectInfo projectInfo : pAll) {
                LOG.log(Level.INFO, "{0}: {1}", new Object[]{i, projectInfo.toString()});
                i++;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Project list couldn''t be obtained due to network error: {0}", e.getMessage());
        }
    }
}
