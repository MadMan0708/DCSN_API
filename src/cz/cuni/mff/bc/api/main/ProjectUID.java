/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents unique project ID
 *
 * @author Jakub Hava
 */
public class ProjectUID implements Serializable {

    private final String clientName;
    private final String projectName;

    /**
     * Constructor
     *
     * @param clientName client's name
     * @param projectName project name
     */
    public ProjectUID(String clientName, String projectName) {
        this.clientName = clientName;
        this.projectName = projectName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.clientName);
        hash = 41 * hash + Objects.hashCode(this.projectName);
        return hash;
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
     * Gets the clientName
     *
     * @return client's name
     */
    public String getClientName() {
        return clientName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProjectUID other = (ProjectUID) obj;
        if (!Objects.equals(this.clientName, other.clientName)) {
            return false;
        }
        if (!Objects.equals(this.projectName, other.projectName)) {
            return false;
        }
        return true;
    }
}
