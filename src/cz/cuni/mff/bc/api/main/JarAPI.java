/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import javax.imageio.IIOException;
import sun.tools.jar.resources.jar;

/**
 *
 * @author Jakub
 */
public class JarAPI {

    public static String getAttributeFromManifest(Path jar, String attrName) throws IOException {
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jar.toFile()))) {
            Manifest mf = jarStream.getManifest();
            Attributes attr = mf.getMainAttributes();
            return attr.getValue(attrName);
        } catch (IOException e) {
            throw new IOException("Problem during accesing project jar file", e);
        }
    }
}
