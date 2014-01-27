/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.enums;

import java.io.Serializable;

/**
 * Project states
 *
 * @author Jakub Hava
 */
public enum ProjectState implements Serializable {

    /**
     * Active project
     */
    ACTIVE,
    /**
     * Paused project
     */
    PAUSED,
    /**
     * Completed project but not packed yet
     */
    COMPLETED,
    /**
     * Project packed and ready for download
     */
    READY_FOR_DOWNLOAD,
    /**
     * Project is being prepared for the calculation
     */
    PREPARING,
    /**
     * Project is corrupted
     */
    CORRUPTED
}
