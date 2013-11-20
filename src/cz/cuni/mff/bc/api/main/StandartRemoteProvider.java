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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class StandartRemoteProvider {

    private static final Logger LOG = Logger.getLogger(StandartRemoteProvider.class.getName());
    private RemoteProvider remoteProvider;
    private String clientName;
    private Handler logHandler;
    private Path currentJar;

    public StandartRemoteProvider(IServer remoteService, String clientName, Path currentJar, Handler logHandler) {
        this.remoteProvider = new RemoteProvider(remoteService, clientName, currentJar);
        this.logHandler = logHandler;
        LOG.addHandler(logHandler);
        this.clientName = clientName;
        this.currentJar = currentJar;
    }

    public StandartRemoteProvider(IServer remoteService, String clientName, Handler logHandler) {
        this(remoteService, clientName, null, logHandler);
    }

    public Path getCurrentJarPath() {
        return currentJar;
    }

    public RemoteProvider getClientAPI() {
        return remoteProvider;
    }

    public String getClientName() {
        return clientName;
    }

    public Handler getLogHandler() {
        return logHandler;
    }

    public boolean isConnected() {
        return remoteProvider.isConnected();
    }

    public Boolean pauseProject(String projectName) {
        try {
            if (remoteProvider.pauseProject(projectName)) {
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
            if (remoteProvider.unpauseProject(projectName)) {
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
            if (remoteProvider.cancelProject(projectName)) {
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
            if (remoteProvider.isProjectReadyForDownload(projectName)) {
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

    public void download(String projectName, File destination) {
        try {
            final String projectNameLocal = projectName;
            final ProgressChecker pc = remoteProvider.downloadProject(projectName, destination.toPath());
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

    public void uploadProject(Path projectJar, Path projectData) {
        try {
            LOG.info(projectJar.toString());
            final String projectName = JarAPI.getAttributeFromManifest(projectJar, "Project-Name");
            final ProgressChecker pc = remoteProvider.uploadProject(projectJar, projectData);
            if (pc != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.log(Level.INFO, "Project: {0}, Uploaded: 0 %...", projectName);
                        while (pc.isInProgress()) {
                            LOG.log(Level.INFO, "Project: {0}, Uploaded: {1} %...", new Object[]{projectName, pc.getProgress()});
                            try {
                                Thread.sleep(800);
                            } catch (InterruptedException e) {
                                LOG.log(Level.INFO, "Progress checking during uploading has been interupted: {0}", e.getMessage());
                            }
                        }
                        try {
                            pc.wasSuccesfull();
                            LOG.log(Level.INFO, "Project: {0}, Uploaded: 100 %...", projectName);
                            LOG.log(Level.INFO, "Project {0} has been uploaded", projectName);
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
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Jar file couldn't be accessed: {0}", e.getMessage());
        }
    }

    public void printAllProjects() {
        int i = 1;
        try {
            ArrayList<ProjectInfo> pAll = remoteProvider.getProjectList();
            for (ProjectInfo projectInfo : pAll) {
                LOG.log(Level.INFO, "{0}: {1}", new Object[]{i, projectInfo.toString()});
                i++;
            }
            if (i == 1) {
                LOG.log(Level.INFO, "No projects on server");
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Project list couldn't be obtained due to network error: {0}", e.getMessage());
        }
    }

    public void printProjects(ProjectState state) {
        int i = 1;
        try {
            ArrayList<ProjectInfo> pAll = remoteProvider.getProjectList(state);
            for (ProjectInfo projectInfo : pAll) {
                LOG.log(Level.INFO, "{0}: {1}", new Object[]{i, projectInfo.toString()});
                i++;
            }
            if (i == 1) {
                LOG.log(Level.INFO, "No projects on server with state:{0}", state.toString());
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Project list couldn''t be obtained due to network error: {0}", e.getMessage());
        }
    }
}
