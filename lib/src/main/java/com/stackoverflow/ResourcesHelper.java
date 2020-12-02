/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.stackoverflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ResourcesHelper {
    //https://stackoverflow.com/a/3923685/1562087
    public static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? ResourcesHelper.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for(final String element : classPathElements){
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    private static Collection<String> getResources(
            final String element,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if(file.isDirectory()){
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else{
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(
            final File file,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try{
            zf = new ZipFile(file);
        } catch(final ZipException e){
            throw new Error(e);
        } catch(final IOException e){
            throw new Error(e);
        }
        final Enumeration e = zf.entries();
        while(e.hasMoreElements()){
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if(accept){
                retval.add(fileName);
            }
        }
        try{
            zf.close();
        } catch(final IOException e1){
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        File[] fileList = directory.listFiles();
        if (fileList == null) fileList = new File[0];
        for(final File file : fileList){
            if(file.isDirectory()){
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else{
                try{
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if(accept){
                        retval.add(fileName);
                    }
                } catch(final IOException e){
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

    /**
     * list the resources that match args[0]
     *
     * @param args
     *            args[0] is the pattern to match, or list all resources if
     *            there are no args
     */
    private static void testGetResources(final String[] args){
        Pattern pattern;
        if(args.length < 1){
            pattern = Pattern.compile(".*");
        } else{
            pattern = Pattern.compile(args[0]);
        }
        final Collection<String> list = ResourcesHelper.getResources(pattern);
        for(final String name : list){
            System.out.println(name);
        }
    }

    public static List<String> getFilenamesForDirnameFromCP(String directoryName) throws URISyntaxException, IOException {
        List<String> filenames = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(directoryName);
        if (url != null) {
            if (url.getProtocol().equals("file")) {
                File file = Paths.get(url.toURI()).toFile();
                if (file != null) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File filename : files) {
                            filenames.add(filename.toString());
                        }
                    }
                }
            } else if (url.getProtocol().equals("jar")) {
                String dirname = directoryName + "/";
                String path = url.getPath();
                String jarPath = path.substring(5, path.indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(dirname) && !dirname.equals(name)) {
                            URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
                            filenames.add(resource.toString());
                        }
                    }
                }
            }
        }
        return filenames;
    }
    public static void listResourcesZip() throws IOException {
        final URI uri = URI.create("jar:file:/tmp/example.zip");
        try (
                final FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.emptyMap());
        ) {
            Files.walk(zipfs.getPath("/")).forEach(path -> System.out.println("Files in zip:" + path));
            System.out.println("-----");
            final String manifest = Files.readAllLines(
                    zipfs.getPath("x", "y", "z").resolve("d")
            ).stream().collect(Collectors.joining(System.lineSeparator()));
            System.out.println(manifest);
        }
    }
}
