/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.network;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Class used to check the progress of any class which implements IUpDown
 * interface
 *
 * @author Jakub Hava
 */
public class ProgressChecker {

    private Future<?> future;
    private IUpDown upDown;

    /**
     * Constructor
     *
     * @param future future which represents active IUpDown object
     * @param upDown implementation of the IUpDown interface
     */
    public ProgressChecker(Future<?> future, IUpDown upDown) {
        this.future = future;
        this.upDown = upDown;
    }

    /**
     * Checks if the downloading or uploading is still in progress
     *
     * @return true if the downloading or uploading is still in progress, false
     * otherwise
     */
    public boolean isInProgress() {
        if (!future.isDone()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if uploading or downloading has started
     *
     * @return true if uploading or downloading has started, false otherwise
     */
    public boolean hasStarted() {
        return upDown.hasStarted();
    }

    /**
     * Gets the progress
     *
     * @return percentage of the progress
     */
    public int getProgress() {
        return upDown.getProgress();
    }

    /**
     *
     * @return true if downloading or uploading was successful
     * @throws RemoteException
     * @throws IOException
     */
    public boolean wasSuccesful() throws RemoteException, IOException {
        if (upDown.hasCompleted()) {
            return true;
        } else {
            try {
                future.get();
                return true;
            } catch (ExecutionException e) {
                if (e.getCause() instanceof IOException) {
                    throw new IOException(e.getCause().getMessage());
                }
                if (e.getCause() instanceof RemoteException) {
                    throw new RemoteException(e.getCause().getMessage());
                }
            } catch (InterruptedException e) {
            }
            return false;
        }
    }
}
