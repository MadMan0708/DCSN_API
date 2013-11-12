/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.File;

/**
 *
 * @author Jakub
 */
public abstract class Commander {

    public abstract void start(ClientAPIWithLog clientAPI);

    public File getActualJarLocation() {
        return new File(Commander.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
