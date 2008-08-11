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
 *
 * $Id$
 */

package org.nuxeo.build.assembler.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.assembler.resource.CompositeResourceSet;
import org.nuxeo.build.assembler.resource.FileResource;
import org.nuxeo.build.assembler.resource.Resource;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.assembler.xml.Files;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.ZipUtils;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("assemble")
public class AssembleCommand implements Command {

    @XContext("mojo")
    private AbstractNuxeoAssembler mojo;

    @XContext("log")
    private Log log;

    @XNodeList(value = "file", type = String[].class, componentType = String.class)
    private String[] files;

    @XNodeList(value = "set", type = String[].class, componentType = String.class)
    private String[] sets;

    @XNode("outputFile")
    private String outputFile;

    @XNode("pack")
    private boolean pack;

    @XNode("unpack")
    private boolean unpack;

    @XNode("delete")
    private boolean delete;

    @XNode("unpackInNewDirectory")
    private boolean unpackInNewDirectory;

    public AssembleCommand() {
    }

    /**
     * @return the outputFile.
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set.
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * @return the pack.
     */
    public boolean getPack() {
        return pack;
    }

    /**
     * @param pack the pack to set.
     */
    public void setPack(boolean pack) {
        this.pack = pack;
    }

    /**
     * @param unpack the unpack to set.
     */
    public void setUnpack(boolean unpack) {
        this.unpack = unpack;
    }

    /**
     * @return the unpack.
     */
    public boolean getUnpack() {
        return unpack;
    }

    /**
     * @param unpackInNewDirectory the unpackInNewDirectory to set.
     */
    public void setUnpackInNewDirectory(boolean unpackInNewDirectory) {
        this.unpackInNewDirectory = unpackInNewDirectory;
    }

    /**
     * @return the unpackInNewDirectory.
     */
    public boolean getUnpackInNewDirectory() {
        return unpackInNewDirectory;
    }

    /**
     * @return the files.
     */
    public String[] getFiles() {
        return files;
    }

    /**
     * @param files the files to set.
     */
    public void setFiles(String[] files) {
        this.files = files;
    }

    /**
     * @return the sets.
     */
    public String[] getSets() {
        return sets;
    }

    /**
     * @param sets the sets to set.
     */
    public void setSets(String[] sets) {
        this.sets = sets;
    }

    @SuppressWarnings("unchecked")
    public void execute(MavenProject project, Map<Object, Object> context)
            throws Exception {
        File outDir = (File) context.get(OUTPUT_DIRECTORY);
        if (outputFile != null) {
            outDir = new File(outDir, outputFile);
        }

        CompositeResourceSet cset = new CompositeResourceSet();
        if (files != null && files.length > 0) {
            Files filesSet = new Files();
            filesSet.setMojo(mojo);
            filesSet.setFiles(files);
            cset.add(filesSet);
        }

        if (sets != null && sets.length > 0) {
            Map<String, ResourceSet> setMap = (Map<String, ResourceSet>) context.get(RESOURCE_SETS);
            for (String setName : sets) {
                setName = mojo.expandVars(setName);
                ResourceSet set = setMap.get(setName);
                if (set != null) {
                    mojo.getLog().debug("add " + set);
                    cset.add(set);
                } else {
                    System.out.println("Skip unfound set: " + setName);
                }
            }
        }

        if (pack) {
            outDir.getParentFile().mkdirs();
            zip(cset, outDir);
        } else if (unpack) {
            outDir.mkdirs();
            unzip(cset, outDir);
        } else if (delete) {
            remove(cset, outDir);
        } else {
            outDir.mkdirs();
            copy(cset, outDir);
        }
    }

    public void copy(ResourceSet set, File outDir) throws IOException {
        for (Resource res : set) {
            File toFile = new File(outDir, res.getName());
            if (!res.isFile()) {
                toFile.mkdirs();
                continue; // a directory
            }
            toFile.getParentFile().mkdirs();
            InputStream in = res.getStream();
            try {
                log.info("Copying " + res.getName() + " to " + toFile.getPath());
                FileUtils.copyToFile(in, toFile);
            } finally {
                in.close();
            }
        }
    }

    public void remove(ResourceSet set, File outDir) {
        for (Resource res : set) {
            if (!(res instanceof FileResource)) {
                throw new Error("Only File Resources can be used on remove command");
            }
            File file = new File(outDir, res.getName());
            log.info("Deleting " + file.getAbsolutePath());
            if (file.isFile()) {
                file.delete();
            } else {
                FileUtils.deleteTree(file);
            }
        }
    }

    public void zip(ResourceSet set, File file) throws IOException {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
        try {
            for (Resource res : set) {
                if (!res.isFile()) {
                    continue;
                }
                log.info("Compressing " + res.getName() + " to "
                        + file.getPath());
                ZipUtils._zip(res.getName(), res.getStream(), zout);
            }
        } finally {
            zout.close();
        }
    }

    public void unzip(ResourceSet set, File dir) throws IOException {
        for (Resource res : set) {
            File target = unpackInNewDirectory ? new File(dir, res.getName())
                    : dir;
            log.info("Uncompressing " + res.getName() + " to "
                    + target.getPath());
            ZipUtils.unzip(res.getStream(), target);
        }
    }

}
