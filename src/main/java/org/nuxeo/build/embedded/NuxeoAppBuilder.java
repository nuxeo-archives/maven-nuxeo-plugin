/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.build.embedded;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.ZipUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class NuxeoAppBuilder {

    public static void main(String[] args) throws Exception {
        
        File h, t = null;
        String home = null;
        String target = null;
        if (args.length == 0) {
            syntaxError();
        }
        if (args.length == 1) {
            // assume current directory is the home
            home = ".";
            target = args[0];
        } else {
            home = args[0];
            target = args[1];            
        }
        if (home.startsWith("/")) {
            h = new File(home);
        } else {
            h = new File(new File("."), home);
        }
        if (target.startsWith("/")) {
            t = new File(target);
        } else {
            t = new File(new File("."), target);
        }
        
        
        NuxeoAppBuilder merger = new NuxeoAppBuilder();        
        merger.process(h, t, null);
    }
    
    protected static void syntaxError() {
        System.err.println("Syntax error. Usage: <app> [nxserverInstallDir] targetJar");
        System.exit(1);
    }
    

    protected boolean copyEmbeddedJars = true;
    
    protected File home;
    protected File manifest;
    protected File nxserver;
    protected File nuxeo;
    protected JarFile targetJar;
    protected File tmp;
    protected File metaInf; 
     
    protected LinkedList<BundleInfo> bundles = new LinkedList<BundleInfo>();
    protected BundleInfo cbundle = null;

    protected boolean excludeLibs = false;
    protected boolean excludeOsgi;
    protected boolean excludeJetty;

    public void setExcludeOsgi(boolean excludeOsgi) {
        this.excludeOsgi = excludeOsgi;
    }
    
    public void setExcludeJetty(boolean excludeJetty) {
        this.excludeJetty = excludeJetty;
    }
    
    public void setExcludeLibs(boolean excludeLibs) {
        this.excludeLibs = excludeLibs;
    }
    
    public void setCopyEmbeddedJars(boolean copyEmbeddedJars) {
        this.copyEmbeddedJars = copyEmbeddedJars;
    }
    
    protected void pushBundle(BundleInfo bi) {
        if (bi.isRuntimeBundle) {
            bundles.addFirst(bi);
        } else {
            bundles.addLast(bi);
        }
        cbundle = bi;
    }
    
    
    public void process(File src, File target, File launcherJar) throws IOException {
        File homeDir = src; 
        if (homeDir.isFile()) {
            homeDir = File.createTempFile("nuxeo-app-builder-src-", ".tmp");
            homeDir.delete();
            homeDir.mkdirs();
            ZipUtils.unzip(src, homeDir);
            File[] files = homeDir.listFiles();
            if (files.length == 1 && files[0].isDirectory()) {
                File tmp = homeDir;
                homeDir = File.createTempFile("nuxeo-app-builder-src-", ".tmp");
                homeDir.delete();
                homeDir.mkdirs();                
                files[0].renameTo(homeDir);
                FileUtils.deleteTree(tmp);
            }
        }
        try {
            initialize(homeDir);
            // first copy the launcher jar 
            if (launcherJar != null) {
                ZipUtils.unzip(launcherJar, tmp);
            }
            File cfg = getConfigDirectory();
            if (cfg.isDirectory()) {
                System.out.println("Copying configuration");
                File dst = new File(nxserver, "config");
                dst.mkdir();
                copyResources(cfg, dst);
            }
            File nxwar = getNuxeoWar();
            if (nxwar.isDirectory()) {
                System.out.println("Copying Nuxeo WAR");
                File dst = new File(nxserver, "nuxeo.war");
                dst.mkdir();
                copyResources(nxwar, dst);
            }            
            File web = getWebDirectory();
            if (web.isDirectory()) {
                System.out.println("Copying web resources");
                File dst = new File(nxserver, "web/root.war/WEB-INF");
                dst.mkdirs();
                copyResources(web, dst);
            }
            if (!excludeLibs) {
                File lib = getLibDirectory();
                if (lib.isDirectory()) {
                    expandJars(lib);
                }
            }
            File bundles = getBundlesDirectory();
            if (bundles.isDirectory()) {
                expandBundles(bundles);
            }
            // write manifest
            System.out.println("Writing manifest");
            writeManifest();
            // write component descriptors
            writeDescriptors();
            processingDone();
            System.out.println("Creating target file: "+target);
            ZipUtils.zip(tmp.listFiles(), target);
        } finally {            
            System.out.println("Cleaning up ...");
            cleanup();
            if (homeDir.isDirectory()) {
                FileUtils.deleteTree(homeDir);
            }
        }
        System.out.println("Done.");
    }
    
    protected void cleanup() {
        if (tmp != null) {
            FileUtils.deleteTree(tmp);
        }
        home = null;
        bundles = new LinkedList<BundleInfo>();
        cbundle = null;
    }

    protected void writeDescriptors() throws IOException {
        PrintWriter p1 = new PrintWriter(new File(nuxeo, "activators"));
        PrintWriter p2 = new PrintWriter(new File(nuxeo, "components"));
        try {
            for (BundleInfo bi : bundles) {
                if (bi.activator != null) {
                    p1.println(bi.activator);                    
                }
                if (!bi.components.isEmpty()) {
                    for (String co : bi.components) {
                        p2.println(co);
                    }
                }
            }
        } finally {
            try { p1.close(); } catch(Exception e) {}
            p2.close();
        }
    }
    
    protected void writeManifest() throws IOException {
        StringBuilder buf = new StringBuilder();
        for (BundleInfo bi : bundles) {
            if (bi.exports != null) {
                buf.append(bi.exports).append(", ");
            }
        }
        if (buf.length() > 2) {
            buf.setLength(buf.length()-2);
        }
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.put(Name.MANIFEST_VERSION, "1.0");
        attrs.put(Name.IMPLEMENTATION_VERSION, "5.2");
        attrs.put(Name.IMPLEMENTATION_VENDOR, "Nuxeo");
        attrs.putValue("Created-By", "Nuxeo App builder");
        attrs.put(Name.MAIN_CLASS, "org.nuxeo.embedded.NuxeoApp");
        attrs.putValue("Bundle-ManifestVersion", "2");
        attrs.putValue("Bundle-Version", "5.2");
        attrs.putValue("Bundle-Name", "Nuxeo Application");
        attrs.putValue("Bundle-SymbolicName", "org.nuxeo.embedded.application; singleton:=true");
        attrs.putValue("Bundle-Activator", "org.nuxeo.embedded.NuxeoAppActivator");
        attrs.putValue("Bundle-Vendor", "Nuxeo");
        attrs.putValue("Bundle-Category", "application");
        attrs.putValue("Export-Package", buf.toString());
        attrs.putValue("Bundle-ActivationPolicy", "lazy"); 
        // needed when deployed as an osgi plugin. also in this case the osgi jar must not be included
        // we also need to import org.eclipse.core.runtime for eclipse osgi builds
        attrs.putValue("Import-Package", "org.osgi.framework,org.eclipse.core.runtime");   
        FileOutputStream out = new FileOutputStream(manifest);
        try {
            mf.write(out);
        } finally {
            out.close();
        }
    }
    
    protected void expandJars(File dir) throws IOException {
        for (File jar : dir.listFiles()) {            
            processJar(jar);
        }
    }
    
    protected void processJar(File jar) throws IOException {
        if (excludeOsgi && jar.getName().startsWith("osgi")) {
            System.out.println("??? Excluding osgi library: "+jar);
            return;
        }
        if (jar.isFile() && jar.getPath().endsWith(".jar")) {
            System.out.println("Processing library: "+jar);
            File tmpLib = new File(tmp, jar+".tmp");
            try {
                ZipUtils.unzip(jar, tmpLib);
                copyLibResources(tmpLib);
            } finally {
                FileUtils.deleteTree(tmpLib);
            }
            
        }        
    }    
    
    protected void expandBundles(File dir) throws IOException {
        for (File jar : dir.listFiles()) {
            processBundle(jar);
        }
    }
    
    protected void processBundle(File jar) throws IOException {
        if (jar.isFile() && jar.getPath().endsWith(".jar")) {
            System.out.println("Processing bundle: "+jar);
            File tmpBundle = new File(tmp, jar+".tmp");
            try {
                ZipUtils.unzip(jar, tmpBundle);
                BundleInfo bi = null;
                try {
                    bi = new BundleInfo(tmpBundle);
                } catch (IllegalArgumentException e) { // not an OSGI bundle - treat this JAR like an ordinary lib
                    // this is the case of nuxeo-core-storage-sql-extensions-xxx.jar which is unfortunately not an OSGi bundle
                    copyLibResources(tmpBundle);
                    return;
                }
                pushBundle(bi);
                copyBundleResources(bi);
            } finally {
                FileUtils.deleteTree(tmpBundle);
            }
        } else if (jar.isDirectory()) {
            System.err.println("! Bundle directories support is not yet implemented. Failed processing: "+jar);
            //throw new UnsupportedOperationException("Bundle directories support is not yet implemented. Failed processing: "+jar);            
        }
    }

    
    protected File getLibDirectory() {
        return new File(home, "lib"); 
    }

    protected File getBundlesDirectory() {
        return new File(home, "bundles"); 
    }

    protected File getWebDirectory() {
        return new File(home, "web/root.war/WEB-INF");   
    }
    
    protected File getNuxeoWar() {
        return new File(home, "nuxeo.war");
    }
    
    protected File getConfigDirectory() {
        return new File(home, "config");   
    }
    
    
    protected void initialize(File home) throws IOException {
        System.out.println("Initializing ...");
        this.home = home;
        if (tmp == null) {
            tmp = File.createTempFile("nuxeo-app-merger", ".tmp");
            tmp.delete();
            tmp.mkdirs();        
        }
        // create manifest
        metaInf = new File(tmp, "META-INF");
        metaInf.mkdir();
        manifest = new File(metaInf, "MANIFEST.MF");
        nxserver = new File(metaInf, "nuxeo/nxserver");
        nxserver.mkdirs();
        nuxeo = nxserver.getParentFile();
        // run preprocessor if needed
        runPreprocessor(home);
    }
    
    

    public void copyLibResources(File bundleDir) throws IOException {
        for (File file : bundleDir.listFiles()) {
            String name = file.getName();
            if ("META-INF".equals(name)) {
                continue;
            } else {
                copyResource(file, tmp);
            } 
        }         
    }

    public void copyResources(File resDir, File dstDir) throws IOException {
        for (File file : resDir.listFiles()) {
            copyResource(file, dstDir);
        }         
    }

    public void copyBundleResources(BundleInfo bi) throws IOException {
        if (bi.isWebBundle) {
            copyWebModuleResources(bi);
        } else {
            copyRegularBundleResources(bi);
        }
    }
    
    public void copyRegularBundleResources(BundleInfo bi) throws IOException {
        // process first meta inf
        File metaInf = new File(bi.file, "META-INF");
        if (metaInf.isDirectory()) {
            copyBundleMetaInf(bi, metaInf);
        }
        for (File file : bi.file.listFiles()) {
            String name = file.getName();
            if ("META-INF".equals(name) || "module.xml".equals(name) || "skin".equals(name)) {
                continue;                
            } else {
                copyResource(file, tmp);
            } 
        }        
    }

    
    public void copyWebModuleResources(BundleInfo bi) throws IOException {
        File modules = new File(nxserver, "web/root.war/modules");
        modules.mkdirs();
        // get the symbolic name
        File module = new File(modules, bi.id);
        module.mkdir();
        copyResources(bi.file, module);
        // also copy the content in the jar - since it may contains extension points and services
        copyRegularBundleResources(bi);
    }
    
    protected void copyBundleMetaInf(BundleInfo bi, File file) throws IOException {
        File metaInf = new File(tmp, "META-INF");
        for (File f : file.listFiles()) {
            String name = f.getName();
            if ("MANIFEST.MF".equals(name) || "maven".equals(name)) {
                continue;
            } else {
                copyResource(f, metaInf);
            }
        }
    }

    protected void copyResource(File file, File toDir) throws IOException {
        String name = file.getName();
        if (name.endsWith(".jar")) {
            if (copyEmbeddedJars) {
                System.out.println("??? Expanding embedded JAR: "+name);
                ZipUtils.unzip(file, tmp);
            } else {
                // avoid to copy embedded jars
                System.out.println("!!! Skiping embedded JAR: "+name);
            }
            return; 
        }
        if (file.isDirectory()) {
            toDir = new File(toDir, name);
            toDir.mkdir();
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    copyResource(f, toDir);
                }
            }
        } else {
            File dst = new File(toDir, name);
            if (dst.exists()) {
                if (!mergeFile(name, dst, file)) {
                    // ignoring duplicate resources
                    System.err.println("!!! Resource already exists: "+dst.getAbsolutePath());
                }
            } else {
                FileUtils.copyFile(file, dst);
            }
        }
    }
    
    /**
     * You can override this to implement custom merging 
     * @param oldFile
     * @param newFile
     * @return
     */
    protected boolean mergeFile(String name, File oldFile, File newFile) throws IOException {
        if (name.equals("deployment-fragment.xml") || name.equals("NOTICE") || name.equals("LICENSE") || name.equals("web-types") || name.equals("module.xml")) {
            return true; // do not warn for these file
        }
        if (cbundle != null) {
            if (cbundle.components != null) {
                for (int i=0,size=cbundle.components.size(); i<size; i++) {
                    String co = cbundle.components.get(i);
                    if (newFile.equals(new File(cbundle.file, co))) {
                        File f = new File(co);
                        String fname = cbundle.id+"_"+name;
                        co = f.getParentFile().getPath()+"/"+fname;
                        cbundle.components.set(i, co);
                        System.out.println("??? resolved conflict -> renamed file <"+newFile+"> to "+co);
                        FileUtils.copyFile(newFile, new File(oldFile.getParent(), fname));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    protected void processingDone() throws IOException {
        new File(tmp, "LICENSE").delete();
        new File(tmp, "LICENSE.txt").delete();
        new File(tmp, "NOTICE").delete();
        new File(tmp, "NOTICE.txt").delete();
        new File(tmp, "README.txt").delete();
        new File(tmp, "module.xml").delete();
        new File(tmp, "web.xml").delete();
        // remove javax.management since it is part of java 1.5
        FileUtils.deleteTree(new File(tmp, "javax/management"));
        // needed when developing GWT apps since GWT eclipse plugin comes with jetty
        if (excludeJetty) {
            FileUtils.deleteTree(new File(tmp, "org/mortbay"));    
        }
    }

    
    protected void runPreprocessor(File home) {
        if (!new File(home, "OSGI-INF/deployment-container.xml").isFile()) {
            return;
        }
        File file = findFile(new File(home, "bundles"), "nuxeo-runtime-deploy-");
        if (file != null) {
            try {
                runPreprocessor(home, file);
            } catch (Exception e) {
                throw new Error("Failed to run nuxeo preprocessor", e);
            }
        } else {
            System.out.println("No nuxeo preprocessor found.");
        }
    }
    
    protected File findFile(File dir, String prefix) {
        String[] names = dir.list();
        for (String name : names) {
            if (name.startsWith(prefix)) {
                return new File(dir, name);
            }
        }
        return null;
    }
    
    
    protected void runPreprocessor(File home, File preprocessor) throws Exception {
        MutableURLClassLoader cl = new MutableURLClassLoader();
        File libs = new File(home, "lib");
        File[] files = libs.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getPath().endsWith(".jar")) {
                    cl.addURL(file.toURI().toURL());
                }
            }        
        }
        File bundles = new File(home, "bundles");
        files = bundles.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getPath().endsWith(".jar")) {
                    cl.addURL(file.toURI().toURL());
                }
            }        
        }
        System.out.println("# Running preprocessor ...");
        Class<?> klass = cl.loadClass("org.nuxeo.runtime.deployment.preprocessor.DeploymentPreprocessor");
        Method main = klass.getMethod("main", String[].class);
        main.invoke(null, new Object[] {new String[] {home.getAbsolutePath()}});
        System.out.println("# Preprocessing done.");
    }
    
    class MutableURLClassLoader extends URLClassLoader {
        public MutableURLClassLoader() {
            super (new URL[0], NuxeoAppBuilder.class.getClassLoader());            
        }
        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
    
}
