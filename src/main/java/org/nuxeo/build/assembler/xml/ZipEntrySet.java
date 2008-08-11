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
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.assembler.ZipEntrySetFilter;
import org.nuxeo.build.assembler.resource.Resource;
import org.nuxeo.build.assembler.resource.ResourceIterator;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.assembler.resource.ZipEntryResource;
import org.nuxeo.common.utils.ZipFileIterator;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descriptor for zipEntrySet element
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("zipEntrySet")
public class ZipEntrySet implements ResourceSet {

    @XContext("mojo")
    private AbstractNuxeoAssembler mojo;

    @XNode("@id")
    private String id;

    @XNode("file")
    public void setParametrizedFile(String value) {
        zipFile = mojo.expandVars(value);
    }
    private String zipFile;

    // the group:name:version
    @XNode("artifact")
    public void setParametrizeAtrifact(String artifactId) {
        this.artifactId = mojo.expandVars(artifactId);
    }
    private String artifactId;

    @XNode("includes")
    private IncludePatterns includes;

    @XNode("excludes")
    private ExcludePatterns excludes;

    @XNode("@profile")
    private String profile;

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
     * @return the zipFile.
     */
    public String getZipFile() {
        return zipFile;
    }

    /**
     * @param zipFile the zipFile to set.
     */
    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    /**
     * @return the artifactId.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @param artifactId the artifactId to set.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
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
        return excludes;
    }

    /**
     * @param excludes the excludes to set.
     */
    public void setExcludes(ExcludePatterns excludes) {
        this.excludes = excludes;
    }

    @SuppressWarnings("unchecked")
    public ZipFileIterator getZipFileIterator() throws Exception {
        File file = null;
        if (zipFile == null) {
            if (artifactId == null) {
                throw new IllegalArgumentException(
                        "A zip entry set must have a 'file' or an 'artifact' sub-elements");
            }
            ArtifactDescriptor ad = ArtifactDescriptor.fromVersionId(artifactId);
            Artifact artifact = null;
            if (artifactId.indexOf("*") > -1 || ad.version == null) {
                // try to find the artifact in the dependencies
                Set<Artifact> artifacts = mojo.getProject().getArtifacts();
                ArtifactFilter filter = ad.getFilter();
                for (Artifact a : artifacts) {
                    if (filter.include(a)) {
                        artifact = a;
                        break;
                    }
                }
            } else {
                artifact = mojo.getArtifactResolver().resolve(ad);
                if (artifact == null) {
                    throw new Error(
                            "Failed to process zip entry set: No such artifact: "
                                    + artifactId);
                }
            }
            if (artifact != null) {
                file = artifact.getFile();
            } else {
                throw new MojoExecutionException("Artifact cannot be found: "
                        + artifactId);
            }
        } else {
            file = new File(zipFile);
        }

        ZipEntrySetFilter filter = null;
        if (includes != null || excludes != null) {
            filter = new ZipEntrySetFilter(includes, excludes);
        }

        return new ZipFileIterator(file, filter);

    }

    // TODO : handle close streams
    public Iterator<Resource> iterator() {
        try {
            final ZipFileIterator it = getZipFileIterator();
            return new ResourceIterator<ZipEntry>(it) {
                @Override
                protected Resource adapt(ZipEntry object) {
                    return new ZipEntryResource(object, it.getZipFile());
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
