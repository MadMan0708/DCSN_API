/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

/**
 *
 * @author Aku
 */
import cz.cuni.mff.bc.api.enums.InformMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.cojen.dirmi.Asynchronous;
import org.cojen.dirmi.Pipe;

public interface IServer extends Remote {

    public void setClientsMemoryLimit(String clientID, int memory) throws RemoteException;
    
    public void setClientsCoresLimit(String clientID, int cores) throws RemoteException;
    
    public Task getTask(String clientID, TaskID taskID) throws RemoteException;

    public void saveCompletedTask(String clientID, Task task) throws RemoteException;

    public boolean isProjectExists(String clientID, String projectID) throws RemoteException;

    public boolean hasClientTasksInProgress(String clientID) throws RemoteException;

    @Asynchronous
    public Pipe uploadProject(String clientName, String projectName, int priority, int cores, int memory, int time, Pipe pipe) throws RemoteException;

    @Asynchronous
    public Pipe downloadProjectJar(ProjectUID uid, Pipe pipe) throws RemoteException;

    public boolean isProjectReadyForDownload(String clientID, String projectID) throws RemoteException;

    public long getProjectFileSize(String clientID, String projectID) throws RemoteException;

    @Asynchronous
    public Pipe downloadProject(String clientID, String projectID, Pipe pipe) throws RemoteException;

    public void setSessionClassLoaderDetails(String clientSessionID, ProjectUID projectUID) throws RemoteException;

    public TaskID getTaskIdBeforeCalculation(String clientID) throws RemoteException;

    public ArrayList<ProjectInfo> getProjectList(String clientID) throws RemoteException;

    public void sendInformMessage(String clientID, InformMessage message) throws RemoteException;

    public boolean pauseProject(String clientID, String projectID) throws RemoteException;

    public boolean cancelProject(String clientID, String projectID) throws RemoteException;

    public boolean resumeProject(String clientID, String projectID) throws RemoteException;

    public boolean isConnected(String clientName) throws RemoteException;

    public ArrayList<TaskID> calculatedTasks(String clientID, ArrayList<TaskID> tasks) throws RemoteException;

    public void cancelTaskFromClient(String clientID, TaskID taskToCancel) throws RemoteException;
}
