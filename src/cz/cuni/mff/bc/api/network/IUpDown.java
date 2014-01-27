/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.network;

import java.util.concurrent.Callable;

/**
 * Interface for download and upload classes
 *
 * @author Jakub Hava
 */
public interface IUpDown extends Callable<Object> {

    /**
     * Gets the progress of uploading or downloading in percents
     *
     * @return the progress of uploading or downloading in percents
     */
    public int getProgress();

    /**
     * Tests if uploading or downloading has finished
     *
     * @return true if uploading or downloading has completed, false otherwise
     */
    public boolean hasCompleted();

    /**
     * Tests if uploading or downloading has started
     *
     * @return true if uploading or downloading has started, false otherwise
     */
    public boolean hasStarted();

    @Override
    public Object call() throws Exception;
}
