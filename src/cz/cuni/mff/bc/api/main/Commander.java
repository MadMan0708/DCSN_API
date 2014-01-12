/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

/**
 * Main entry for user defined class, which can be used to work without console
 * and for doing more sophisticated actions like dynamic data creation and so
 * on.
 *
 * @author Jakub Hava
 */
public abstract class Commander {

    /**
     * This method is supposed to be overwritten by users, to fulfil they
     * demands
     *
     * @param standartRemoteProvider
     */
    public abstract void start(StandardRemoteProvider standartRemoteProvider);
}
