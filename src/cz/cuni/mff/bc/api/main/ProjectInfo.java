/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import cz.cuni.mff.bc.api.enums.ProjectState;
import java.io.Serializable;

/**
 * Contains information about project
 *
 * @author Jakub
 */
public class ProjectInfo implements Serializable {

    private final String projectName;
    private final String clientName;
    private final int numOfCompletedTasks;
    private final int numOfAllTasks;
    private final ProjectState state;
    private final int priority;
    private final int cores;
    private final int memory;
    private final int time;

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
     * Gets the cores limit
     *
     * @return cores limit
     */
    public int getCores() {
        return cores;
    }

    /**
     * Gets the memory limit
     *
     * @return memory limit
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Gets average task time
     *
     * @return average task time
     */
    public int getTime() {
        return time;
    }

    /**
     * Gets the project name
     *
     * @return project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the client's name
     *
     * @return client's name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Gets number of completed tasks
     *
     * @return number of completed tasks
     */
    public int getNumOfCompletedTasks() {
        return numOfCompletedTasks;
    }

    /**
     * Gets number of all tasks
     *
     * @return number of all tasks
     */
    public int getNumOfAllTasks() {
        return numOfAllTasks;
    }

    /**
     * Gets the project state
     *
     * @return project state
     */
    public ProjectState getState() {
        return state;
    }

    /**
     * Gets the project priority
     *
     * @return project priority
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "ProjectID: " + projectName + "; State: " + state + "; Priority " + priority + "; Num of completed tasks: " + numOfCompletedTasks + "/" + numOfAllTasks;
    }
}
