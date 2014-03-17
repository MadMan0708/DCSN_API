/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents unique task ID
 *
 * @author Jakub Hava
 */
public class TaskID implements Serializable {

    private int cores;
    private int memory;
    private int time;
    private final int priority;
    private final String projectName;
    private final String clientName;
    private final String taskName;

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
    public TaskID(String projectName, String clientName, String taskName, int priority, int cores, int memory, int time) {
        this.projectName = projectName;
        this.clientName = clientName;
        this.taskName = taskName;
        this.cores = cores;
        this.memory = memory;
        this.time = time;
        this.priority = priority;
    }

    /**
     * Gets the number of cores needed by task
     *
     * @return number of cores needed by task
     */
    public int getCores() {
        return cores;
    }

    /**
     * Sets the number of cores needed by task
     *
     * @param cores number of cores needed by task
     */
    public void setCores(int cores) {
        this.cores = cores;
    }

    /**
     * Gets the amount of memory needed by task
     *
     * @return amount of memory needed by task
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Sets the amount of memory needed by task
     *
     * @param memory amount of memory needed by task
     */
    public void setMemory(int memory) {
        this.memory = memory;
    }

    /**
     * Gets the average time of task to be calculated
     *
     * @return average time of task to be calculated
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the average time of task to be calculated
     *
     * @param time average time of task to be calculated
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Gets the project priority
     *
     * @return project priority
     */
    public int getPriority() {
        return priority;
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
     * Gets the unique project ID
     *
     * @return project unique ID
     */
    public ProjectUID getProjectUID() {
        return new ProjectUID(clientName, projectName);
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
     * Gets the task name
     *
     * @return task name
     */
    public String getTaskName() {
        return taskName;
    }

    @Override
    public String toString() {
        return clientName + "_" + projectName + "_" + taskName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TaskID)) {
            return false;
        } else {
            TaskID ID = (TaskID) other;
            if (this.clientName.equals(ID.getClientName()) && this.projectName.equals(ID.getProjectName()) && this.taskName.equals(ID.getTaskName())) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.projectName);
        hash = 53 * hash + Objects.hashCode(this.clientName);
        hash = 53 * hash + Objects.hashCode(this.taskName);
        return hash;
    }
}
