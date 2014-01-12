/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.enums.ProjectState;
import java.io.Serializable;

/**
 *
 * @author Jakub
 */
public class ProjectInfo implements Serializable {

    private String projectName;
    private String clientName;
    private int numOfCompletedTasks;
    private int numOfAllTasks;
    private ProjectState state;
    private int priority;
    private int cores;
    private int memory;
    private int time;

    /**
     * Constructor
     *
     * @param projectName project name
     * @param clientName client's name
     * @param numOfCompletedTasks number of completed tasks
     * @param numOfAllTasks number of all tasks
     * @param state project state
     * @param priority project priority
     * @param cores number of cores needed by task
     * @param memory amount of memory needed by task
     * @param time average time of task to be calculated
     */
    public ProjectInfo(String projectName, String clientName, int numOfCompletedTasks, int numOfAllTasks,
            ProjectState state, int priority, int cores, int memory, int time) {
        this.projectName = projectName;
        this.clientName = clientName;
        this.numOfCompletedTasks = numOfCompletedTasks;
        this.numOfAllTasks = numOfAllTasks;
        this.state = state;
        this.priority = priority;
        this.cores = cores;
        this.memory = memory;
        this.time = time;
    }

    /**
     *
     * @return number of cores needed by task
     */
    public int getCores() {
        return cores;
    }

    /**
     *
     * @param cores number of cores needed by task
     */
    public void setCores(int cores) {
        this.cores = cores;
    }

    /**
     *
     * @return amount of memory needed by task
     */
    public int getMemory() {
        return memory;
    }

    /**
     *
     * @param memory amount of memory needed by task
     */
    public void setMemory(int memory) {
        this.memory = memory;
    }

    /**
     *
     * @return average time of task to be calculated
     */
    public int getTime() {
        return time;
    }

    /**
     *
     * @param time average time of task to be calculated
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     *
     * @param projectName project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     *
     * @return client's name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     *
     * @param clientName client's name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     *
     * @return number of completed tasks
     */
    public int getNumOfCompletedTasks() {
        return numOfCompletedTasks;
    }

    /**
     *
     * @param numOfCompletedTasks number of completed tasks
     */
    public void setNumOfCompletedTasks(int numOfCompletedTasks) {
        this.numOfCompletedTasks = numOfCompletedTasks;
    }

    /**
     *
     * @return number of all tasks
     */
    public int getNumOfAllTasks() {
        return numOfAllTasks;
    }

    /**
     *
     * @param numOfAllTasks number of all tasks
     */
    public void setNumOfAllTasks(int numOfAllTasks) {
        this.numOfAllTasks = numOfAllTasks;
    }

    /**
     *
     * @return project state
     */
    public ProjectState getState() {
        return state;
    }

    /**
     *
     * @param state project state
     */
    public void setState(ProjectState state) {
        this.state = state;
    }

    /**
     *
     * @return project priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     *
     * @param priority project priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ProjectID: " + projectName + "; State: " + state + "; Priority " + priority + "; Num of completed tasks: " + numOfCompletedTasks + "/" + numOfAllTasks;
    }
}
