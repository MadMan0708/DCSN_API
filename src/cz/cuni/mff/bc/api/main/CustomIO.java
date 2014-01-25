/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.bc.api.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Basic I/O methods
 *
 * @author Jakub Hava
 */
public class CustomIO {

    /**
     * Checks if the file exist with given extension
     *
     * @param file file to check
     * @param extension extension which file has to have
     * @return true if file exist, otherwise false
     */
    public static boolean isFileExist(Path file, String extension) {
        if (file.toFile().isFile() && getExtension(file.toFile()).equals(extension)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the jar file is valid
     *
     * @param file jar file to be checked
     * @return true if jar is valid, false otherwise
     */
    public static boolean isJarValid(Path file) {
        return isZipValid(file);
    }

    /**
     * Checks if the zip file is valid
     *
     * @param file zip file to be checked
     * @return true if zip is valid, false otherwise
     */
    public static boolean isZipValid(Path file) {
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(file.toFile());
            return true;
        } catch (ZipException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * Gets the extension of the file
     *
     * @param file file
     * @return extension of the file
     */
    public static String getExtension(File file) {
        int dotIndex = file.getName().lastIndexOf(".") + 1;
        return file.getName().substring(dotIndex);
    }

    /**
     * Extracts zip file do destination folder
     *
     * @param zip zip file location
     * @param dest destination folder
     * @throws IOException
     */
    public static void extractZipFile(File zip, File dest) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(dest, fileName);
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    ze = zis.getNextEntry();
                } catch (IOException e) {
                    throw new IOException("Problem with extracting file " + ze.getName() + " from archive " + zip.getName(), e);
                }
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            throw new IOException("Problem with opening project archive" + zip.getName(), e);
        }
    }

    private static void appendToZip(File file, ZipOutputStream zos) throws IOException {
        String name = file.getName();
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(byteBuffer)) != -1) {
                zos.write(byteBuffer, 0, bytesRead);
            }
            zos.flush();
        }
    }

    /**
     * Zips the file in array to one output zip
     *
     * @param zip destination zip file
     * @param files array of files to be zipped
     * @throws IOException
     */
    public static void zipFiles(File zip, File[] files) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
            for (File file : files) {
                appendToZip(file, zos);
            }
        }
    }

    /**
     * Creates the folder
     *
     * @param folder file which represents the folder
     * @return file which represents the folder
     */
    public static File createFolder(Path folder) {
        if (!folder.toFile().exists()) {
            folder.toFile().mkdirs();
        }
        return folder.toFile();
    }

    /**
     * Copies the file
     *
     * @param from origin file
     * @param to destination file
     * @throws IOException
     */
    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath());
    }

    /**
     * Deletes the directory and all its content recursively
     *
     * @param dir directory to delete
     */
    public static void deleteDirectory(Path dir) {
        File[] files = dir.toFile().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f.toPath());
                } else {
                    f.delete();
                }
            }
        }
        dir.toFile().delete();
    }

    /**
     * Deletes files in the directory. It deletes only those files, which name
     * matches the pattern
     *
     * @param dirWhere path to the directory to delete
     * @param pattern pattern
     */
    public static void deleteWithPattern(Path dirWhere, final String pattern) {
        File[] files = dirWhere.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.matches(pattern)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (File file : files) {
            file.delete();
        }
    }

    /**
     * Deletes the directory and all its content recursively at closing the
     * program
     *
     * @param path directory to delete
     */
    public static void recursiveDeleteOnShutdownHook(final Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread(
                new Runnable() {
            @Override
            public void run() {
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file,
                                @SuppressWarnings("unused") BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException e)
                                throws IOException {
                            if (e == null) {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                            // directory iteration failed
                            throw e;
                        }
                    });
                } catch (IOException e) {
                }
            }
        }));
    }
}
