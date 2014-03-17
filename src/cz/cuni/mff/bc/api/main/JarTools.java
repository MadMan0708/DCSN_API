/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Class with some basic methods to work with jar files
 *
 * @author Jakub Hava
 */
public class JarTools {

    /**
     * Checks if the project contains commander class
     *
     * @param projectJar project jar
     * @return true if the project contains commander class, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectCommanderClassValid(Path projectJar) throws IllegalArgumentException, IOException {
        String attributeFromManifest = getAttributeFromManifest(projectJar, "Main-Commander-Class");
        if (attributeFromManifest == null) {
            throw new IllegalArgumentException("Path to commander class has to be specified in the manifest file");
        } else {
            URLClassLoader cl = new URLClassLoader(new URL[]{projectJar.toUri().toURL()});
            try {
                cl.loadClass(attributeFromManifest);
                cl.close();
                return true;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Commander class is not present in jar file");
            }

        }
    }

    /**
     * Checks if the project contains main computation class
     *
     * @param projectJar project jar
     * @return true if the project contains main computation class, otherwise
     * false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectCompClassValid(Path projectJar) throws IllegalArgumentException, IOException {
        String attributeFromManifest = getAttributeFromManifest(projectJar, "Main-Comp-Class");
        if (attributeFromManifest == null) {
            throw new IllegalArgumentException("Path to main computation class has to be specified in the manifest file");
        } else {
            URLClassLoader cl = new URLClassLoader(new URL[]{projectJar.toUri().toURL()});
            try {
                cl.loadClass(attributeFromManifest);
                cl.close();
                return true;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Computation class is not present in jar file");
            }

        }
    }

    /**
     * Checks if the project name is valid
     *
     * @param projectJar project jar
     * @return true if the project name is valid, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectNameValid(Path projectJar) throws IllegalArgumentException, IOException {
        String attributeFromManifest = getAttributeFromManifest(projectJar, "Project-Name");
        if (attributeFromManifest == null) {
            throw new IllegalArgumentException("Project name has to be specified in the manifest file");
        } else {
            return true;
        }
    }

    /**
     * Checks if the project priority is valid
     *
     * @param projectJar project jar
     * @return true if the project priority is valid, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     *
     */
    public static boolean isProjectPriorityValid(Path projectJar) throws IllegalArgumentException, IOException {
        try {
            String attributeFromManifest = getAttributeFromManifest(projectJar, "Project-Priority");
            if (attributeFromManifest == null) {
                throw new IllegalArgumentException("Project priority has to be specified in the manifest file");
            } else {
                int projectPriority = Integer.parseInt(attributeFromManifest);
                if (projectPriority >= 0 && projectPriority <= 10) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Project priority has to be integer in range from 1 to 10");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Project priority has to be integer in range from 1 to 10");
        }
    }

    /**
     * Checks if the project memory limit is valid
     *
     * @param projectJar project jar
     * @return true if the project memory limit is valid, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectMemoryValid(Path projectJar) throws IllegalArgumentException, IOException {
        try {
            String attributeFromManifest = getAttributeFromManifest(projectJar, "Memory-Per-Task");
            if (attributeFromManifest == null) {
                throw new IllegalArgumentException("Memory limit has to be specified in the manifest file");
            } else {
                int memory = Integer.parseInt(attributeFromManifest);
                if (memory > 0) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Memory limit has to be integer higher then 0 mb");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Memory limit has to be integer higher then 0 mb");
        }
    }

    /**
     * Checks if the project cores limit is valid
     *
     * @param projectJar project jar
     * @return true if the project cores limit is valid, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectCoresValid(Path projectJar) throws IllegalArgumentException, IOException {
        try {
            String attributeFromManifest = getAttributeFromManifest(projectJar, "Cores-Per-Task");
            if (attributeFromManifest == null) {
                throw new IllegalArgumentException("Cores limit has to be specified in the manifest file");
            } else {
                int cores = Integer.parseInt(attributeFromManifest);
                if (cores > 0) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Cores limit has to be integer higher then 0");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cores limit has to be integer higher then 0");

        }
    }

    /**
     * Checks if the project average time is valid
     *
     * @param projectJar project jar
     * @return true if the project average time is valid, otherwise false
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean isProjectTimeValid(Path projectJar) throws IllegalArgumentException, IOException {
        try {
            String attributeFromManifest = getAttributeFromManifest(projectJar, "Time-Per-Task");
            if (attributeFromManifest == null) {
                throw new IllegalArgumentException("Average time has to be specified in the manifest file");
            } else {
                int time = Integer.parseInt(attributeFromManifest);
                if (time > 0) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Time has to be integer higher then 0 minutes");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Time has to be integer higher then 0 minutes");
        }
    }

    /**
     * Checks the project parameters in project file
     *
     * @param projectJar project jar
     * @return true if the parameters are correct, false otherwise
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static boolean checkProjectParams(Path projectJar) throws IllegalArgumentException, IOException {
        return isProjectTimeValid(projectJar) && isProjectCoresValid(projectJar) && isProjectMemoryValid(projectJar)
                && isProjectPriorityValid(projectJar) && isProjectTimeValid(projectJar) && isProjectCompClassValid(projectJar);
    }

    /**
     * Gets the value of attribute from jar manifest file
     *
     * @param projectJar path to the jar file
     * @param attrName attribute name
     * @return value of the attribute
     * @throws IOException
     */
    public static String getAttributeFromManifest(Path projectJar, String attrName) throws IOException {
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(projectJar.toFile()))) {
            Manifest mf = jarStream.getManifest();
            if (mf != null) {
                Attributes attr = mf.getMainAttributes();
                return attr.getValue(attrName);
            } else {
                return null; // manifest does not exist
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Project " + projectJar + " file not found on given path", e);
        } catch (IOException e) {
            throw new IOException("Problem with accesing project jar file " + projectJar, e);
        }
    }

    /**
     * Creates the new jar file with value of one attribute changed
     *
     * @param sourceJar path to the source jar
     * @param destinationjar path to the destination jar
     * @param attrName attribute name
     * @param value new attribute value
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void createJarWithChangedAttributeValue(Path sourceJar, Path destinationjar, String attrName, String value) throws FileNotFoundException, IOException {
        JarFile file = new JarFile(sourceJar.toFile());
        Manifest mf = file.getManifest();
        Attributes attr = mf.getMainAttributes();
        // insert new value
        attr.put(new Attributes.Name(attrName), value);
        //create new manifest
        Manifest newManifest = new Manifest();
        newManifest.getMainAttributes().putAll(attr);
        Enumeration<JarEntry> entries = file.entries();
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(destinationjar.toFile()), newManifest)) {
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().equals("META-INF/MANIFEST.MF")) {
                    target.putNextEntry(entry);
                    try (InputStream input = file.getInputStream(entry)) {
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = input.read(buffer)) > 0) {
                            target.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
