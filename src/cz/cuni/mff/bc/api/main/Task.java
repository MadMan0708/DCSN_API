/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.enums.TaskState;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Class representing task
 *
 * @author Jakub Hava
 */
public class Task implements Serializable {

    private TaskID unicateTaskID;
    private ITask computeTask;
    private TaskState state = TaskState.BEFORE_START;
    private Boolean dataHasBeenSaved = false;

    /**
     * Constructor
     *
     * @param projectName project name
     * @param clientName client's name
     * @param taskName task name
     * @param priority project priority
     * @param cores number of cores needed by task
     * @param memory amount of memory needed by task
     * @param time average time of task to be calculated
     */
    public Task(String projectName, String clientName, String taskName, int priority, int cores, int memory, int time) {
        this.unicateTaskID = new TaskID(projectName, clientName, taskName, priority, cores, memory, time);
    }

    /**
     * Sets the class where the computation methods are defined by the client
     *
     * @param className name of the class
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void setClass(Class<?> className) throws IllegalAccessException, InstantiationException {
        this.computeTask = (ITask) className.newInstance();
    }

    /**
     * Check if task data has been saved
     *
     * @return true if task data has been saved, false otherwise
     */
    public boolean hasDataBeenSaved() {
        return dataHasBeenSaved;
    }

    /**
     * Gets the client's name
     *
     * @return client's name
     */
    public String getClientName() {
        return unicateTaskID.getClientName();
    }

    /**
     * Gets the project name
     *
     * @return project name
     */
    public String getProjectName() {
        return unicateTaskID.getProjectName();
    }

    /**
     * Gets the task unique ID
     *
     * @return task unique ID
     */
    public TaskID getUnicateID() {
        return unicateTaskID;
    }

    /**
     * Gets the unique OD of the project to which this task belong
     *
     * @return unique project ID
     */
    public ProjectUID getProjectUID() {
        return new ProjectUID(unicateTaskID.getClientName(), unicateTaskID.getProjectName());
    }

    /**
     * Does the calculation on loaded data
     */
    public void calculate() {
        computeTask.calculate();
    }

    /**
     * Loads the data
     *
     * @param from path to the data file
     */
    public void loadData(Path from) {
        computeTask.loadData(from);
    }

    /**
     * Saves the data
     *
     * @param to path where to save data
     */
    public void saveData(Path to) {
        computeTask.saveData(to);
        dataHasBeenSaved = true;
    }

    /**
     * Sets the task state
     *
     * @param state task state
     */
    public void setState(TaskState state) {
        this.state = state;
    }

    /**
     * Gets the task state
     *
     * @return task state
     */
    public TaskState getState() {
        return this.state;
    }

    /*
     * Method use for serializing
     */
    private void writeObject(ObjectOutputStream os) throws Exception {
        os.writeObject(unicateTaskID);
        os.writeObject(computeTask);
        os.writeObject(state);
        os.writeObject(dataHasBeenSaved);
    }

    /*
     * Method use for deserializing
     */
    private void readObject(ObjectInputStream os) throws Exception {
        unicateTaskID = (TaskID) os.readObject();
        computeTask = (ITask) os.readObject();
        state = (TaskState) os.readObject();
        dataHasBeenSaved = (Boolean) os.readObject();
    }
}
