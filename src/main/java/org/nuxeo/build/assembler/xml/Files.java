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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.assembler.NuxeoAssembler;
import org.nuxeo.build.assembler.resource.FileResource;
import org.nuxeo.build.assembler.resource.Resource;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.assembler.resource.ZipEntryResource;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("files")
public class Files implements ResourceSet {

    @XContext("mojo")
    private AbstractNuxeoAssembler mojo;

    @XNode("@id")
    private String id;

    @XNodeList(value = "file", type = String[].class, componentType = String.class)
    private String[] files;

    @XNodeList(value = "extends", type = String[].class, componentType = String.class)
    private String[] extendedSets;

    @XNode("@profile")
    private String profile;

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
     * @return the profile.
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set.
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the mojo.
     */
    public NuxeoAssembler getMojo() {
        return mojo;
    }

    /**
     * @param mojo the mojo to set.
     */
    public void setMojo(AbstractNuxeoAssembler mojo) {
        this.mojo = mojo;
    }

    /**
     * @return the extendedSets.
     */
    public String[] getExtendedSets() {
        return extendedSets;
    }

    /**
     * @param extendedSets the extendedSets to set.
     */
    public void setExtendedSets(String[] extendedSets) {
        this.extendedSets = extendedSets;
    }

    public Iterator<Resource> iterator() {
        ArrayList<Resource> result = new ArrayList<Resource>();
        if (files != null) {
            for (String path : files) {
                path = mojo.expandVars(path);
                int p = path.indexOf('!');
                if (p > 0) { // a zip
                    File file = new File(path.substring(0, p));
                    try {
                        result.add(new ZipEntryResource(path.substring(p + 1),
                                file));
                    } catch (IOException e) {
                        throw new Error(
                                "Failed to create zip entry resource for "
                                        + path, e);
                    }
                } else {
                    File file = new File(path);
                    result.add(new FileResource(file.getName(), file));
                }
            }
        }
        return result.iterator();
    }

}
