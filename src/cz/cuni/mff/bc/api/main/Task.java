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
 * Class which represents a task
 *
 * @author Jakub Hava
 */
public class Task implements Serializable {

    private TaskID unicateTaskID;
    private ITask computeTask;
    private TaskState state = TaskState.BEFORE_START;
    private boolean dataHasBeenSaved = false;

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
     * Sets the class which is used to do computation
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
     *
     * @return client's name
     */
    public String getClientName() {
        return unicateTaskID.getClientName();
    }

    /**
     *
     * @return project name
     */
    public String getProjectName() {
        return unicateTaskID.getProjectName();
    }

    /**
     *
     * @return task unique id
     */
    public TaskID getUnicateID() {
        return unicateTaskID;
    }

    /**
     *
     * @return unique id of project where this task belong to
     */
    public ProjectUID getProjectUID() {
        return new ProjectUID(unicateTaskID.getClientName(), unicateTaskID.getProjectName());
    }

    /**
     * Methods do the calculation on loaded data
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
     *
     * @param state task state
     */
    public void setState(TaskState state) {
        this.state = state;
    }

    /**
     *
     * @return task state
     */
    public TaskState getState() {
        return this.state;
    }

    private void writeObject(ObjectOutputStream os) throws Exception {
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream os) throws Exception {
        os.defaultReadObject();
    }
}
