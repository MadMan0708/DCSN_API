/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.enums;

/**
 * Task states
 *
 * @author Jakub Hava
 */
public enum TaskState {

    /**
     * Task calculation haven't started yet
     */
    BEFORE_START,
    /**
     * Task calculation is complete
     */
    COMPLETE,
    /**
     * Task calculation is in progress
     */
    IN_PROGRESS
}
