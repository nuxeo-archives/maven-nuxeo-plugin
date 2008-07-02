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

package org.nuxeo.build.assembler.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nuxeo.build.assembler.commands.AssembleCommand;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@XObject("assembly")
public class AssemblyDescriptor {

    @XNodeList(value = "fileSet", type = ArrayList.class, componentType = FileSet.class)
    private List<FileSet> fileSets;

    @XNodeList(value = "files", type = ArrayList.class, componentType = Files.class)
    private List<Files> files;

    @XNodeList(value = "artifactSet", type = ArrayList.class, componentType = ArtifactSet.class)
    private List<ArtifactSet> artifactSets;

    @XNodeList(value = "zipEntrySet", type = ArrayList.class, componentType = ZipEntrySet.class)
    private List<ZipEntrySet> zipEntrySets;

    @XNodeList(value = "assemble", type = ArrayList.class, componentType = AssembleCommand.class)
    private List<Command> commands;

    // TODO includes cannot be used in included files
    @XNodeList(value = "include", type = String[].class, componentType = String.class)
    private String[] includes;

    @XNodeMap(value = "properties/property", key = "@name", type = HashMap.class, componentType = String.class)
    private HashMap<Object, Object> properties;

//    /**
//     * artifacts sets definition that replaces assembly, fileSet and artifactSet
//     */
//    @XNodeList(value = "serverSet", type = ArrayList.class, componentType = ServerSet.class)
//    private List<ServerSet> servers;

    /**
     * @return the files.
     */
    public List<Files> getFiles() {
        return files;
    }

    /**
     * @param files the files to set.
     */
    public void setFiles(List<Files> files) {
        this.files = files;
    }

    /**
     * @return the fileSets.
     */
    public List<FileSet> getFileSets() {
        return fileSets;
    }

    /**
     * @param fileSets the fileSets to set.
     */
    public void setFileSets(List<FileSet> fileSets) {
        this.fileSets = fileSets;
    }

    /**
     * @return the zipEntrySet.
     */
    public List<ZipEntrySet> getZipEntrySets() {
        return zipEntrySets;
    }

    /**
     * @param zipEntrySet the zipEntrySet to set.
     */
    public void setZipEntrySets(List<ZipEntrySet> zipEntrySet) {
        this.zipEntrySets = zipEntrySet;
    }

    /**
     * @return the artifactSet.
     */
    public List<ArtifactSet> getArtifactSets() {
        return artifactSets;
    }

    /**
     * @param artifactSet the artifactSet to set.
     */
    public void setArtifactSets(List<ArtifactSet> artifactSet) {
        this.artifactSets = artifactSet;
    }

    /**
     * @return the properties.
     */
    public HashMap<Object, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set.
     */
    public void setProperties(HashMap<Object, Object> properties) {
        this.properties = properties;
    }

    /**
     * @return the commands.
     */
    public List<Command> getCommands() {
        return commands;
    }

    /**
     * @param commands the commands to set.
     */
    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    /**
     * @return the includes.
     */
    public String[] getIncludes() {
        return includes;
    }

    /**
     * @param includes the includes to set.
     */
    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

//    public List<ServerSet> getServers() {
//        return servers;
//    }
//
//    public void setServers(List<ServerSet> servers) {
//        this.servers = servers;
//    }

}
