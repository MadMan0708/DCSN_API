/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

/**
 * Main entry for user defined class which is used to work without console. User
 * is able to create data dynamically, and use console commands from the code.
 *
 * @author Jakub Hava
 */
public abstract class Commander {

    /**
     * Main method where the processing starts. This method is supposed to be
     * overwritten by users to fulfil they demands
     *
     * @param standartRemoteProvider
     */
    public abstract void start(StandardRemoteProvider standartRemoteProvider);
}
