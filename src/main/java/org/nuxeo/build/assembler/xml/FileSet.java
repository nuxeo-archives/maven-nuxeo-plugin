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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.assembler.FileSetFilter;
import org.nuxeo.build.assembler.resource.FileResource;
import org.nuxeo.build.assembler.resource.FileResourceSet;
import org.nuxeo.build.assembler.resource.Resource;
import org.nuxeo.build.assembler.resource.ResourceIterator;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.common.utils.FileTreeIterator;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("fileSet")
public class FileSet extends FileResourceSet {

    @XContext("mojo")
    private AbstractNuxeoAssembler mojo;

    @XNode("@id")
    private String id;

    @XNodeList(value = "extends", type = String[].class, componentType = String.class)
    private String[] extendedSets;

    @XNode("directory")
    private String directory;

    @XNode("includes")
    private IncludePatterns includes;

    @XNode("excludes")
    private ExcludePatterns excludes;

    @XNode("profile")
    private String profile;

    private ExcludePatterns excludePatterns;

    private String[] defaultExclusions = new String[] { "**/.svn", "**/.hg" };

    private Set<File> files;

    /**
     * @return the directory.
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * @param directory the directory to set.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
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
     * @return the includes.
     */
    public IncludePatterns getIncludes() {
        return includes;
    }

    /**
     * @param includes the includes to set.
     */
    public void setIncludes(IncludePatterns includes) {
        this.includes = includes;
    }

    /**
     * @return the excludes.
     */
    public ExcludePatterns getExcludes() {
        if (excludePatterns == null) {
            if (excludes != null) {
                excludePatterns = excludes;
            } else {
                excludePatterns = new ExcludePatterns();
            }
            excludePatterns.addPatterns(defaultExclusions);
        }
        return excludePatterns;
    }

    /**
     * @param excludes the excludes to set.
     */
    public void setExcludes(ExcludePatterns excludes) {
        this.excludes = excludes;
    }

    /**
     * @param id the id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the id.
     */
    public String getId() {
        return id;
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

    private Set<File> loadBaseSets() {
        HashSet<File> result = new HashSet<File>();
        if (extendedSets != null && extendedSets.length > 0) {
            for (String setId : extendedSets) {
                Map<String, ResourceSet> sets = mojo.getResourceSetMap();
                ResourceSet set = sets.get(setId);
                if (set instanceof FileResourceSet) {
                    FileResourceSet frSet = (FileResourceSet) set;
                    Iterator<File> it = frSet.fileIterator();
                    while (it.hasNext()) {
                        result.add(it.next());
                    }
                } else {
                    mojo.getLog().warn(
                            "Base set not found or incompatible type: " + setId);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<File> fileIterator() {
        if (files == null) {
            files = new HashSet<File>();
            Set<File> base = loadBaseSets();
            File dir = new File(mojo.getBasedir(), directory);
            FileTreeIterator it = new FileTreeIterator(dir, true);
            IncludePatterns includePatterns = getIncludes();
            ExcludePatterns excludePatterns = getExcludes();
            if (includePatterns != null || excludePatterns != null) {
                it.setFilter(new FileSetFilter(dir, includePatterns,
                        excludePatterns));
            }
            while (it.hasNext()) {
                files.add(it.next());
            }
            if (includePatterns != null) {
                for (File file : base) {
                    if (includePatterns.match(file.getAbsolutePath())) {
                        files.add(file);
                    }
                }
            }
            if (excludePatterns != null) {
                for (File file : base) {
                    if (excludePatterns.match(file.getAbsolutePath())) {
                        files.remove(file);
                    }
                }
            }
        }
        return files.iterator();
    }

    @Override
    public Iterator<Resource> iterator() {
        String prefix = new File(mojo.getBasedir(), directory).getAbsolutePath();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        final int off = prefix.length();
        return new ResourceIterator<File>(fileIterator()) {
            @Override
            protected Resource adapt(File object) {
                return new FileResource(
                        object.getAbsolutePath().substring(off), object);
            }
        };
    }

    @Override
    public String toString() {
        StringBuffer toStringBuffer = new StringBuffer();
        toStringBuffer.append("{" + getId() + "," + getExtendedSets() + ","
                + getDirectory() + "," + getIncludes() + "," + getExcludes());
        return toStringBuffer.toString();
    }

}
