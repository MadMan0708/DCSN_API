package cz.cuni.mff.bc.api.main;

import java.io.Serializable;
import java.nio.file.Path;
import java.rmi.Remote;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Interface which represents the general task
 *
 * @author Jakub Hava
 */
public interface ITask extends Remote, Serializable {

    /**
     * Loads the data
     *
     * @param from path to the data file
     */
    public void loadData(Path from);

    /**
     * Saves the data
     *
     * @param to path to the data file
     */
    public void saveData(Path to);

    /**
     * Method does the calculation on loaded data
     */
    public void calculate();
}
