/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.Serializable;
import java.util.Objects;

/**
 * ID of a task
 *
 * @author Jakub Hava
 */
public class TaskID implements Serializable {

    private int cores;
    private int memory;
    private int time;
    private int priority;
    private String projectName;
    private String clientName;
    private String taskName;

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
     * @return project priority
     */
    public int getPriority() {
        return priority;
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
     * @return project unique id
     */
    public ProjectUID getProjectUID() {
        return new ProjectUID(clientName, projectName);
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
