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

    private static final Logger LOG = Logger.getLogger(StandardRemoteProvider.class.getName());
    private RemoteProvider remoteProvider;

    /**
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param uploadDir upload directory
     * @param currentJar path to project jar
     * @param logger logger
     */
    public StandardRemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Path currentJar, Logger logger) {
        this.remoteProvider = new RemoteProvider(remoteService, clientName, downloadDir, uploadDir, currentJar);
        LOG.setParent(logger);
    }

    /**
     *
     * @param remoteService remote interface
     * @param clientName client name
     * @param downloadDir download directory
     * @param uploadDir upload directory
     * @param logger logger
     */
    public StandardRemoteProvider(IServer remoteService, String clientName, Path downloadDir, Path uploadDir, Logger logger) {
        this(remoteService, clientName, null, downloadDir, uploadDir, logger);
    }

    /**
     *
     * @return download directory which has been set in console
     */
    public Path getStandartDownloadDir() {
        return remoteProvider.getStandartDownloadDir();
    }

    /**
     *
     * @return upload directory which has been set in console
     */
    public Path getStandartUploadDir() {
        return remoteProvider.getStandartUploadDir();
    }

    /**
     *
     * @return path to the project jar
     */
    public Path getCurrentJarPath() {
        return remoteProvider.getCurrentJarPath();
    }

    /**
     *
     * @return basic remote provider
     */
    public RemoteProvider getRemoteProvider() {
        return remoteProvider;
    }

    /**
     *
     * @return client name
     */
    public String getClientName() {
        return remoteProvider.getClientName();
    }

    /**
     *
     * @return logger
     */
    public Logger getLogger() {
        return LOG;
    }

    /**
     * Test if client is connected on the server
     *
     * @return true if client is connected, false otherwise
     */
    public boolean isConnected() {
        return remoteProvider.isConnected();
    }

    /**
     * Sends to server information about memory limit which can tasks use during
     * computation
     *
     * @param memory
     */
    public void setMemoryLimit(int memory) {
        try {
            remoteProvider.setMemoryLimit(memory);
            LOG.log(Level.INFO, "Memory limit {0}m for tasks has been set on server", memory);
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Memory limit couldn't be set due to connection problem");
        }
    }

    /**
     * Sends to server information about number of cores which can be used to
     * task computation
     *
     * @param cores
     */
    public void setCoresLimit(int cores) {
        try {
            remoteProvider.setCoresLimit(cores);
            LOG.log(Level.INFO, "Cores limit {0} for tasks has been set on server", cores);
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Cores limit couldn't be set due to connection problem");
        }
    }

    /**
     * Tries to pause the project
     *
     * @param projectName project name
     * @return true if the project has been paused, false if the project doesn't
     * exist or exception has been thrown
     */
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
            LOG.log(Level.WARNING, "Problem during pausing project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }

    /**
     * Tries to resume the project
     *
     * @param projectName project name
     * @return true if the project has been resumed, false if the project
     * doesn't exist or exception has been thrown
     */
    public Boolean resumeProject(String projectName) {
        try {
            if (remoteProvider.resumeProject(projectName)) {
                LOG.log(Level.INFO, "Project {0} was successfuly unpaused", projectName);
                return true;
            } else {
                LOG.log(Level.INFO, "No such project: {0}", projectName);
                return false;
            }
        } catch (RemoteException e) {
            LOG.log(Level.WARNING, "Problem during unpausing project due to network erorr: {0}", e.getMessage());
            return null;
        }
    }

    /**
     * Marks the project as corrupted
     *
     * @param ownerName name of the owner of the project
     * @param projectName project name
     * @throws RemoteException
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
     * @return true if the project has been cancelled, false if the project
     * doesn't exist or exception has been thrown
     */
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

    /**
     * Check if client has tasks in progress
     *
     * @return true if client has tasks in progress, false otherwise
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
     * Tests if project is ready for download
     *
     * @param projectName project name
     * @return true if the project is ready for download, false if the project
     * is not ready for download or exception has been thrown
     */
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

    /**
     * Downloads the project with progress logging and exception handling
     *
     * @param projectName project name
     * @param destination path to destination file
     */
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
                            pc.wasSuccesful();
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

    /**
     * Checks project parameters in project jar file
     *
     * @param projectJar project jar
     * @return true if parameters are correct, false otherwise
     * @throws IOException
     */
    public boolean checkProjectParams(Path projectJar) throws IOException {
        boolean ok = true;
        try {
            int projectPriority = Integer.parseInt(JarAPI.getAttributeFromManifest(projectJar, "Project-Priority"));
            if (projectPriority <= 0 || projectPriority > 10) {
                LOG.log(Level.WARNING, "Project priority range is from 1 to 10");
                ok = false;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Project priority has to be integer in range from 1 to 10");
            ok = false;
        }
        try {
            int memory = Integer.parseInt(JarAPI.getAttributeFromManifest(projectJar, "Memory-Per-Task"));
            if (memory <= 0) {
                LOG.log(Level.WARNING, "Memory limit has to be bigger then 0 megabytes");
                ok = false;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Memory limit has to be integer bigger then 0");
            ok = false;
        }

        try {
            int cores = Integer.parseInt(JarAPI.getAttributeFromManifest(projectJar, "Cores-Per-Task"));
            if (cores <= 0) {
                LOG.log(Level.WARNING, "Cores limit has to be bigger then 0 ");
                ok = false;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Cores limit has to be integer bigger then 0");
            ok = false;
        }

        try {
            int time = Integer.parseInt(JarAPI.getAttributeFromManifest(projectJar, "Time-Per-Task"));
            if (time <= 0) {
                LOG.log(Level.WARNING, "Time has to be bigger then 0");
                ok = false;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Time  has to be integer bigger then 0");
            ok = false;
        }
        return ok;
    }

    /**
     *
     * Uploads the project with progress logging and exception handling
     *
     * @param projectJar path to project jar
     * @param projectData path to project data
     */
    public void uploadProject(Path projectJar, Path projectData) {
        if (!CustomIO.getExtension(projectData.toFile()).equals("zip")) {
            LOG.log(Level.WARNING, "Possible archive for data files is only *.zip");
            return;
        }
        if (!CustomIO.isZipValid(projectData.toFile())) {
            LOG.log(Level.WARNING, "Zip file is corrupted");
            return;
        }

        try {
            if (!checkProjectParams(projectJar)) {
                return;
            }
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
                            pc.wasSuccesful();
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
            LOG.log(Level.WARNING, "Jar file {0} couldn't be accessed or is not correct project", projectJar.getFileName());
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
     * Prints basic information about client's projects with given state
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
