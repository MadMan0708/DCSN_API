/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 *
 * @author Jakub
 */
public class JarAPI {

    public static String getAttributeFromManifest(Path jar, String attrName) throws FileNotFoundException, IOException {
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jar.toFile()))) {
            Manifest mf = jarStream.getManifest();
            Attributes attr = mf.getMainAttributes();
            return attr.getValue(attrName);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Project file not exists on written path:");
        } catch (IOException e) {
            throw new IOException("Problem during accesing project jar file: ", e);
        }
    }

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
