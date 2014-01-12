/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

/**
 * Remote interface, used for RMI calls
 *
 * @author Jakub Hava
 */
import cz.cuni.mff.bc.api.enums.InformMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.cojen.dirmi.Asynchronous;
import org.cojen.dirmi.Pipe;

public interface IServer extends Remote {

    /**
     * Sets the client's memory limit for task computation on the server
     *
     * @param clientName client's name
     * @param memory memory limit to be set
     * @throws RemoteException
     */
    public void setClientsMemoryLimit(String clientName, int memory) throws RemoteException;

    /**
     * Sets the client's cores limit for task computation on the server
     *
     * @param clientName client's name
     * @param cores cores limit to be set
     * @throws RemoteException
     */
    public void setClientsCoresLimit(String clientName, int cores) throws RemoteException;

    /**
     * Gets task from the server
     *
     * @param clientName client's name
     * @param taskID task id of the task to be downloaded
     * @return the task
     * @throws RemoteException
     */
    public Task getTask(String clientName, TaskID taskID) throws RemoteException;

    /**
     * Saves the completed task on the server
     *
     * @param clientName client's name
     * @param task task to be saved
     * @throws RemoteException
     */
    public void saveCompletedTask(String clientName, Task task) throws RemoteException;

    /**
     * Checks if the project exists on the server
     *
     * @param clientName client's name
     * @param projectName project name
     * @return true if the project exists, false otherwise
     * @throws RemoteException
     */
    public boolean isProjectExists(String clientName, String projectName) throws RemoteException;

    /**
     * Checks if the client has tasks in progress
     *
     * @param clientName client's name
     * @return true if client has some tasks in progress, false otherwise
     * @throws RemoteException
     */
    public boolean hasClientTasksInProgress(String clientName) throws RemoteException;

    /**
     * Uploads the project on the server
     *
     * @param clientName client's name
     * @param projectName project name
     * @param priority tasks priority
     * @param cores number of cores used by the tasks
     * @param memory amount of memory used by the tasks
     * @param time average time of task computation
     * @param pipe pipe used to transfer the data files
     * @return pipe for the other side of the connection
     * @throws RemoteException
     */
    @Asynchronous
    public Pipe uploadProject(String clientName, String projectName, int priority, int cores, int memory, int time, Pipe pipe) throws RemoteException;

    /**
     * Downloads the complete project
     *
     * @param clientName client's name
     * @param projectName project name
     * @param pipe pipe used to transfer the data files
     * @return pipe for the other side of the connection
     * @throws RemoteException
     */
    @Asynchronous
    public Pipe downloadProject(String clientName, String projectName, Pipe pipe) throws RemoteException;

    /**
     * Downloads only the project jar
     *
     * @param projectUID unique id of the project
     * @param pipe pipe used to transfer the data files
     * @return pipe for the other side of the connection
     * @throws RemoteException
     */
    @Asynchronous
    public Pipe downloadProjectJar(ProjectUID projectUID, Pipe pipe) throws RemoteException;

    /**
     * Checks it the project is ready for download
     *
     * @param clientName client's name
     * @param projectName project name
     * @return true if the project is ready for download, false otherwise
     * @throws RemoteException
     */
    public boolean isProjectReadyForDownload(String clientName, String projectName) throws RemoteException;

    /**
     * Gets the size of the project jar file
     *
     * @param clientName client's name
     * @param projectName project name
     * @return size of the project jar file
     * @throws RemoteException
     */
    public long getProjectFileSize(String clientName, String projectName) throws RemoteException;

    /**
     * Gets the unique id of task which will calculated and associates the task
     * with the client
     *
     * @param clientName client's name
     * @return task unique id
     * @throws RemoteException
     */
    public TaskID getTaskIdBeforeCalculation(String clientName) throws RemoteException;

    /**
     * Gets the list of client's projects
     *
     * @param clientName client's name
     * @return the list of the projects
     * @throws RemoteException
     */
    public ArrayList<ProjectInfo> getProjectList(String clientName) throws RemoteException;

    /**
     * Method used to send info messages to the server. It is used to set the
     * start and the end of the client's task computation
     *
     * @param clientName client's name
     * @param message message to be sent
     * @throws RemoteException
     */
    public void sendInformMessage(String clientName, InformMessage message) throws RemoteException;

    /**
     * Pauses the project
     *
     * @param clientName client's name
     * @param projectName project name
     * @return true if the project was paused, false otherwise
     * @throws RemoteException
     */
    public boolean pauseProject(String clientName, String projectName) throws RemoteException;

    /**
     * Cancels the project
     *
     * @param clientName client's name
     * @param projectName project name
     * @return true if the project was cancelled, false otherwise
     * @throws RemoteException
     */
    public boolean cancelProject(String clientName, String projectName) throws RemoteException;

    /**
     * Resumes the project
     *
     * @param clientName client's name
     * @param projectName project name
     * @return true if the project was resumed, false otherwise
     * @throws RemoteException
     */
    public boolean resumeProject(String clientName, String projectName) throws RemoteException;

    /**
     * Checks if the client is connected
     *
     * @param clientName client's name
     * @return true if the client is connected, false otherwise
     * @throws RemoteException
     */
    public boolean isConnected(String clientName) throws RemoteException;

    /**
     * Sends to the server the list of task currently calculated tasks. If for
     * any reason some tasks are already finished, the unique id of those task
     * is sent back to the client
     *
     * @param clientName client's name
     * @param tasksInCalculation tasks currently calculated on the client
     * @return the list of task which calculation is supposed to be terminated
     * @throws RemoteException
     */
    public ArrayList<TaskID> sendTasksInCalculation(String clientName, ArrayList<TaskID> tasksInCalculation) throws RemoteException;

    /**
     * Unassociate task from given client's and puts it again back to the tasks
     * pool
     *
     * @param clientName client's name
     * @param taskToCancel task to cancel
     * @throws RemoteException
     */
    public void cancelTaskOnClient(String clientName, TaskID taskToCancel) throws RemoteException;
}
