/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.enums.ProjectState;
import static cz.cuni.mff.bc.api.enums.ProjectState.ACTIVE;
import static cz.cuni.mff.bc.api.enums.ProjectState.COMPLETED;
import static cz.cuni.mff.bc.api.enums.ProjectState.CORRUPTED;
import static cz.cuni.mff.bc.api.enums.ProjectState.PAUSED;
import static cz.cuni.mff.bc.api.enums.ProjectState.PREPARING;
import static cz.cuni.mff.bc.api.enums.ProjectState.READY_FOR_DOWNLOAD;
import cz.cuni.mff.bc.api.network.ProgressChecker;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides basic methods from server. These methods use default
 * RemoteExceptions handling and default logging. If you need use these methods
 * with own logging, use class RemoteProvider
 *
 * @author Jakub Hava
 */
public class StandardRemoteProvider {

    private static int timeout = 5000;
    private static final Logger LOG = Logger.getLogger(StandardRemoteProvider.class.getName());
    private RemoteProvider remoteProvider;

    /**
     * Constructor
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param uploadDir upload directory
     * @param temporaryDir temporary directory
     * @param currentJar path to project jar
     * @param logger logger
     */
    public StandardRemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Path temporaryDir, Path currentJar, Logger logger) {
        this.remoteProvider = new RemoteProvider(remoteService, clientName, downloadDir, uploadDir, temporaryDir, currentJar);
        LOG.setParent(logger);
    }

    /**
     * Constructor
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param uploadDir upload directory
     * @param temporaryDir temporary directory
     * @param logger logger
     */
    public StandardRemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Path temporaryDir, Logger logger) {
        this(remoteService, clientName, downloadDir, uploadDir, temporaryDir, null, logger);
    }

    /**
     * Gets the path to the temporary directory which has been set in console
     *
     * @return path to the temporary directory
     */
    public Path getStandartTemporaryDir() {
        return remoteProvider.getStandartTemporaryDir();
    }

    /**
     * Gets the path to the download directory which has been set in console
     *
     * @return path to the download directory
     */
    public Path getStandartDownloadDir() {
        return remoteProvider.getStandartDownloadDir();
    }

    /**
     * Gets the path to the upload directory which has been set in console
     *
     * @return path to the upload directory
     */
    public Path getStandartUploadDir() {
        return remoteProvider.getStandartUploadDir();
    }

    /**
     * Gets the path to the current project jar
     *
     * @return path to current project jar
     */
    public Path getCurrentJarPath() {
        return remoteProvider.getCurrentJarPath();
    }

    /**
     * Gets the basic remote provider
     *
     * @return basic remote provider
     */
    public RemoteProvider getRemoteProvider() {
        return remoteProvider;
    }

    /**
     * Gets the client's name
     *
     * @return client's name
     */
    public String getClientName() {
        return remoteProvider.getClientName();
    }

    /**
     * Gets the logger
     *
     * @return logger
     */
    public Logger getLogger() {
        return LOG;
    }

    /**
     * Checks if the client is connected
     *
     * @return true if the client is connected, false otherwise
     */
    public boolean isConnected() {
        return remoteProvider.isConnected();
    }

    /**
     * Sends to the server information about amount of memory which can be used
     * during task computation
     *
     * @param memory memory limit
     */
    public void setMemoryLimit(int memory) {
        try {
            remoteProvider.setMemoryLimit(memory);
            LOG.log(Level.FINE, "Memory limit {0}m for tasks has been set on server", memory);
        } catch (RemoteException e) {
            LOG.log(Level.FINE, "Memory limit couldn't be set due to connection problem");
        }
    }

    /**
     * Sends to server information about number of cores which can be used
     * during task computation
     *
     * @param cores cores limit
     */
    public void setCoresLimit(int cores) {
        try {
            remoteProvider.setCoresLimit(cores);
            LOG.log(Level.FINE, "Cores limit {0} for tasks has been set on server", cores);
        } catch (RemoteException e) {
            LOG.log(Level.FINE, "Cores limit could not be set due to connection problem");
        }
    }

    /**
     * Tries to pause the project
     *
     * @param projectName project name
     * @return true if the project has been paused, false if the project hasn't
     * been paused or if the project doesn't exist on the server and null if
     * there has been problem during pausing the project
     */
    public Boolean pauseProject(String projectName) {
        try {
            ProjectState state = remoteProvider.pauseProject(projectName);
            if (state != null) {
                switch (state) {
                    case ACTIVE:
                        LOG.log(Level.INFO, "Project {0} has been successfuly paused", projectName);
                        return true;
                    case COMPLETED:
                        LOG.log(Level.INFO, "Project {0} cannot be resumed because it is beeing prepared for download", projectName);
                        return false;
                    case CORRUPTED:
                        LOG.log(Level.INFO, "Project {0} is corruted, cancel it please", projectName);
                        return false;
                    case PAUSED:
                        LOG.log(Level.INFO, "Project {0} is already paused", projectName);
                        return false;
                    case PREPARING:
                        LOG.log(Level.INFO, "Project {0} cannot be paused because it is beeing uploaded to the server", projectName);
                        return false;
                    case READY_FOR_DOWNLOAD:
                        LOG.log(Level.INFO, "Project {0} is ready for download", projectName);
                        return false;
                    default:
                        return false;
                }
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during pausing project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }

    /**
     * Tries to resume the project
     *
     * @param projectName project name
     * @return true if the project has been resumed, false if the project hasn't
     * been resumed or if the project doesn't exist on the server and null if
     * there has been problem during resuming the project
     */
    public Boolean resumeProject(String projectName) {
        try {
            ProjectState state = remoteProvider.resumeProject(projectName);
            if (state != null) {
                switch (state) {
                    case ACTIVE:
                        LOG.log(Level.INFO, "Project {0} is already active", projectName);
                        return true;
                    case COMPLETED:
                        LOG.log(Level.INFO, "Project {0} cannot be resumed because it is beeing prepared for download", projectName);
                        return false;
                    case CORRUPTED:
                        LOG.log(Level.INFO, "Project {0} is corruted, cancel it please", projectName);
                        return false;
                    case PAUSED:
                        LOG.log(Level.INFO, "Project {0} has been successfully resumed", projectName);
                        return false;
                    case PREPARING:
                        LOG.log(Level.INFO, "Project {0} cannot be paused because it is beeing uploaded to the server", projectName);
                        return false;
                    case READY_FOR_DOWNLOAD:
                        LOG.log(Level.INFO, "Project {0} is ready for download", projectName);
                        return false;
                    default:
                        return false;
                }
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during resuming project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }

    /**
     * Marks the project as corrupted
     *
     * @param ownerName name of the owner of the project
     * @param projectName project name
     * @return true if the project was successfully marked as corrupted
     */
    public Boolean markProjectAsCurrupted(String ownerName, String projectName) {
        try {
            remoteProvider.markProjectAsCurrupted(ownerName, projectName);
            LOG.log(Level.INFO, "Project{0} by {1} is marked as corrupted now", new Object[]{projectName, ownerName});
            return true;
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during marking project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }

    /**
     * Tries to cancel the project
     *
     * @param projectName project name
     * @return true if the project has been cancelled, false if the project is
     * in preparing phase or doesn't exist and null if there has been problem
     * during cancelling the project
     */
    public Boolean cancelProject(String projectName) {
        try {
            Boolean canceled = remoteProvider.cancelProject(projectName);
            if (canceled == true) {
                LOG.log(Level.INFO, "Project {0} was successfuly canceled", projectName);
                return true;
            } else if (canceled == false) {
                LOG.log(Level.INFO, "Project {0} can be canceled during preparation on the server", projectName);
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

    /**
     * Check if the client has some tasks in progress
     *
     * @return true if the client has tasks in progress, false otherwise
     */
    public Boolean hasClientTasksInProgress() {
        try {
            if (!remoteProvider.hasClientTasksInProgress()) {
                LOG.log(Level.INFO, "Client {0} has no tasks in progress", getClientName());
                return false;
            } else {
                LOG.log(Level.INFO, "Client {0} has tasks in progress: ", getClientName());
                printAllProjects();
                return true;
            }
        } catch (RemoteException e) {
            LOG.log(Level.INFO, "Problem during discovering tasks in progress", e.getMessage());
            return null;
        }
    }

    /**
     * Tests if the project is ready for download
     *
     * @param projectName project name
     * @return true if the project is ready for download, false if the project
     * is not ready for download or doesn't exist and null if exception has been
     * thrown
     */
    public Boolean isProjectReadyForDownload(String projectName) {
        try {
            if (remoteProvider.isProjectExists(projectName)) {
                if (remoteProvider.isProjectReadyForDownload(projectName)) {
                    LOG.log(Level.INFO, "Project {0} is ready for download", projectName);
                    return true;
                } else {
                    LOG.log(Level.INFO, "Project {0} is not ready for download", projectName);
                    return false;
                }
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during determining project state due to network error{0}", e.getMessage());
            return null;
        }
    }

    /**
     * Downloads the project with progress logging and exception handling
     *
     * @param projectName project name
     * @param destination path to destination file
     */
    public void download(String projectName, Path destination) {
        try {
            if (remoteProvider.isProjectExists(projectName)) {
                final String projectNameLocal = projectName;
                final ProgressChecker pc = remoteProvider.downloadProject(projectName, destination);
                if (pc != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LOG.log(Level.INFO, "Project: {0}, Preparing files for download. Downloading will start immedeatelly after.", projectNameLocal);
                                while (!pc.hasStarted()) {
                                    Thread.sleep(100);
                                }
                                LOG.log(Level.FINE, "Project: {0}, Downloading started", projectNameLocal);
                                LOG.log(Level.FINE, "Project: {0}, Downloaded: 0 %...", projectNameLocal);
                                while (pc.isInProgress()) {
                                    Thread.sleep(timeout);
                                    LOG.log(Level.FINE, "Project: {0}, Downloaded: {1} %...", new Object[]{projectNameLocal, pc.getProgress()});
                                }
                            } catch (InterruptedException e) {
                                LOG.log(Level.FINE, "Progress checking during downloading has been interupted: {0}", e.getMessage());
                            }
                            try {
                                pc.wasSuccesful();
                                LOG.log(Level.FINE, "Project: {0}, Downloaded: 100 %...", projectNameLocal);
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
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem with network during downloading: {0}", e.getMessage());
        }
    }

    /**
     *
     * Uploads the project with progress logging and exception handling
     *
     * @param projectJar path to project jar
     * @param projectData path to project data
     */
    public void uploadProject(Path projectJar, Path projectData) {
        try {
            CustomIO.projectJarExistsAndValid(projectJar);
            CustomIO.projectDataExistsAndValid(projectData);
            JarTools.checkProjectParams(projectJar);
            final String projectName = JarTools.getAttributeFromManifest(projectJar, "Project-Name");
            final ProgressChecker pc = remoteProvider.uploadProject(projectJar, projectData);
            if (pc != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LOG.log(Level.INFO, "Project: {0}, Preparing files for upload. Uploading will start immedeatelly after.", projectName);
                            while (!pc.hasStarted()) {
                                Thread.sleep(100);
                            }
                            LOG.log(Level.FINE, "Project: {0}, Uploading started", projectName);
                            LOG.log(Level.FINE, "Project: {0}, Uploaded: 0 %...", projectName);
                            while (pc.isInProgress()) {
                                Thread.sleep(timeout);
                                LOG.log(Level.FINE, "Project: {0}, Uploaded: {1} %...", new Object[]{projectName, pc.getProgress()});
                            }
                        } catch (InterruptedException e) {
                            LOG.log(Level.FINE, "Progress checking during uploading has been interupted: {0}", e.getMessage());
                        }
                        try {
                            pc.wasSuccesful();
                            LOG.log(Level.FINE, "Project: {0}, Uploaded: 100 %...", projectName);
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
            LOG.log(Level.WARNING, "Problem with the network during uploading: {0}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "{0}", e.getMessage());
        } catch (IOException e) {
            LOG.log(Level.WARNING, "{0}", e.getMessage());
        }
    }

    /**
     * Prints information about the project
     *
     * @param projectName project name
     */
    public void printProjectInfo(String projectName) {
        try {
            ArrayList<ProjectInfo> pAll = remoteProvider.getProjectList();
            ProjectInfo particular = null;
            for (ProjectInfo projectInfo : pAll) {
                if (projectInfo.getProjectName().equals(projectName)) {
                    particular = projectInfo;
                    break;
                }
            }
            if (particular != null) {
                LOG.log(Level.INFO, "{0}", particular.toString());
            } else {
                LOG.log(Level.INFO, "No such project");
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Not connected to server: {0}", e.getMessage());
        }
    }

    /**
     * Prints basic information about all client's projects
     */
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
            LOG.log(Level.WARNING, "Not connected to server: {0}", e.getMessage());
        }
    }

    /**
     * Prints basic information about client's projects in given state
     *
     * @param state state used to filter projects
     */
    public void printProjects(ProjectState state) {
        int i = 1;
        try {
            ArrayList<ProjectInfo> pAll = remoteProvider.getProjectList(state);
            for (ProjectInfo projectInfo : pAll) {
                LOG.log(Level.INFO, "{0}: {1}", new Object[]{i, projectInfo.toString()});
                i++;
            }
            if (i == 1) {
                LOG.log(Level.INFO, "No projects on server with state: {0}", state.toString());
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Not connected to server: {0}", e.getMessage());
        }
    }
}
