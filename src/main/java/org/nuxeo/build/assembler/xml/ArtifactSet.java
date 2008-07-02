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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.assembler.ArtifactResolver;
import org.nuxeo.build.assembler.NuxeoAssembler;
import org.nuxeo.build.assembler.resource.ArtifactResourceSet;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.filters.OrArtifactFilter;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("artifactSet")
public class ArtifactSet extends ArtifactResourceSet {

    @XContext("mojo")
    protected AbstractNuxeoAssembler mojo;

    @XNode("@id")
    protected String id;

    @XNode("includeDependencies")
    protected boolean includeDependencies = false;

    @XNode("excludeDependencies")
    protected boolean excludeDependencies = false;

    @XNode(value = "@extends")
    private String extendedSetId;

    protected ArtifactResourceSet superSet;

    @XNodeList(value = "import", type = String[].class, componentType = String.class)
    private String[] importedSets;

    @XNodeList(value = "artifacts/artifact", type = ArrayList.class, componentType = ArtifactDescriptor.class)
    private List<ArtifactDescriptor> artifactDescriptors;

    @XNodeList(value = "includes/artifact", type = ArrayList.class, componentType = ArtifactDescriptor.class)
    private List<ArtifactDescriptor> includes;

    @XNodeList(value = "excludes/artifact", type = ArrayList.class, componentType = ArtifactDescriptor.class)
    private List<ArtifactDescriptor> excludes;

    private ArtifactFilter includeFilter;

    private ArtifactFilter excludeFilter;

    protected Set<Artifact> artifacts;

    private Set<Artifact> resolvedArtifacts;

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
     * @return the includes.
     */
    public List<ArtifactDescriptor> getArtifactDescriptors() {
        return artifactDescriptors;
    }

    /**
     * @param artifacts to set.
     */
    public void setArtifactDescriptors(List<ArtifactDescriptor> artifacts) {
        this.artifactDescriptors = artifacts;
    }

    /**
     * @param mojo the mojo to set.
     */
    public void setMojo(AbstractNuxeoAssembler mojo) {
        this.mojo = mojo;
    }

    /**
     * @return the mojo.
     */
    public NuxeoAssembler getMojo() {
        return mojo;
    }

    /**
     * @return the extendedSetId.
     */
    public String getExtendedSetId() {
        return extendedSetId;
    }

    /**
     * @param extendedSetId the extendedSetId to set.
     */
    public void setExtendedSetId(String extendedSetId) {
        this.extendedSetId = extendedSetId;
    }

    /**
     * @return the superSet.
     */
    public ArtifactResourceSet getSuperSet() {
        return superSet;
    }

    /**
     * @param superSet the superSet to set.
     */
    public void setSuperSet(ArtifactResourceSet superSet) {
        this.superSet = superSet;
    }

    /**
     * @return the extendedSets.
     */
    public String[] getExtendedSets() {
        return importedSets;
    }

    /**
     * @param extendedSets the extendedSets to set.
     */
    public void setExtendedSets(String[] extendedSets) {
        this.importedSets = extendedSets;
    }

    /**
     * @param includeDependencies the includeDependencies to set.
     */
    public void setIncludeDependencies(boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
    }

    /**
     * @return the includeDependencies.
     */
    public boolean getIncludeDependencies() {
        return includeDependencies;
    }

    /**
     * @param excludeDependencies the excludeDependencies to set.
     */
    public void setExcludeDependencies(boolean excludeDependencies) {
        this.excludeDependencies = excludeDependencies;
    }

    /**
     * @return the excludeDependencies.
     */
    public boolean getExcludeDependencies() {
        return excludeDependencies;
    }

    /**
     * @return the resolvedArtifacts.
     */
    public Set<Artifact> getResolvedArtifacts() {
        return resolvedArtifacts;
    }

    /**
     * @return the includeFilter.
     */
    public ArtifactFilter getIncludeFilter() {
        if (includes == null || includes.isEmpty())
            return null;
        if (includeFilter == null) {
            OrArtifactFilter filter = new OrArtifactFilter();
            for (ArtifactDescriptor artifactDescriptor : includes) {
                filter.add(artifactDescriptor.getFilter(mojo));
            }
            // TODO: add transitivity filter if needed
            includeFilter = filter;
        }
        return includeFilter;
    }

    /**
     * @return the excludeFilter.
     */
    public ArtifactFilter getExcludeFilter() {
        if (excludes == null || excludes.isEmpty())
            return null;
        if (excludeFilter == null) {
            OrArtifactFilter filter = new OrArtifactFilter();
            for (ArtifactDescriptor afd : excludes) {
                filter.add(afd.getFilter());
            }
            // TODO: add transitivity filter if needed
            excludeFilter = filter;
        }
        return excludeFilter;
    }

    protected Set<Artifact> loadImportedSets() {
        HashSet<Artifact> result = new HashSet<Artifact>();
        Map<String, ResourceSet> sets = mojo.getResourceSetMap();
        if (importedSets != null && importedSets.length > 0) {
            for (String setId : importedSets) {
                ResourceSet set = sets.get(setId);
                if (!(set instanceof ArtifactResourceSet)) {
                    mojo.getLog().warn(
                            "Cannot extend set " + setId
                                    + ". Set not found or not compatible");
                    continue;
                }
                ArtifactResourceSet arSet = (ArtifactResourceSet) set;
                Iterator<Artifact> it = arSet.artifactIterator();
                while (it.hasNext()) {
                    result.add(it.next());
                }
            }
        }
        return result;
    }

    /**
     * Get produced artifacts (after extending, resolving and filtering)
     *
     * @return
     * @throws MojoExecutionException
     */
    @Override
    public Set<Artifact> getArtifacts() {
        if (artifacts == null) {
            // get base artifacts if any (empty set otherwise)
            artifacts = loadImportedSets();
            if (superSet != null) {
                artifacts.addAll(superSet.getArtifacts());
            }
            // apply include filters
            ArtifactFilter filter = getIncludeFilter();
            if (filter != null) {
                Iterator<Artifact> it = artifacts.iterator();
                while (it.hasNext()) {
                    Artifact artifact = it.next();
                    if (!filter.include(artifact)) {
                        it.remove();
                    }
                }
            }
            // apply include dependencies filter
            if (includeDependencies) {
                HashSet<Artifact> result = new HashSet<Artifact>();
                for (Artifact artifact : artifacts) {
                    Set<Artifact> deps = mojo.getArtifactDependencies(artifact);
                    if (deps!=null) {
                        if (mojo.getLog().isDebugEnabled()) {
                            mojo.getLog().debug("add dependencies for "+artifact);
                            for (Artifact dep:deps) {
                                if (!result.contains(dep)) {
                                    mojo.getLog().debug("   added "+dep);
                                }
                            }
                        }
                        result.addAll(deps);
                    }
                }
                artifacts.addAll(result);
            }
            // apply exclude filters
            filter = getExcludeFilter();
            if (filter != null) {
                Iterator<Artifact> it = artifacts.iterator();
                while (it.hasNext()) {
                    if (filter.include(it.next())) {
                        it.remove();
                    }
                }
            }
            // apply exclude dependencies
            if (excludeDependencies) {
                HashSet<Artifact> result = new HashSet<Artifact>();
                for (Artifact artifact : artifacts) {
                    Set<Artifact> deps = mojo.getArtifactDependencies(artifact);
                    result.addAll(deps);
                }
                artifacts.removeAll(result);
            }
            resolveArtifacts(artifacts);
            // add explicit declared artifacts
            collectResolvedArtifacts(artifacts);
        }
        return artifacts;
    }

    private void resolveArtifacts(Set<Artifact> artifactsToResolve) {
        ArtifactResolver resolver = mojo.getArtifactResolver();
        for (Artifact artifact : artifactsToResolve) {
            if (!artifact.isResolved()) {
                try {
                    resolver.resolve(artifact);
                } catch (MojoExecutionException e) {
                    mojo.getLog().warn(e);
                }
            }
        }
    }

    public void collectResolvedArtifacts(Set<Artifact> artifactSet) {
        ArtifactResolver resolver = mojo.getArtifactResolver();
        try {
            for (ArtifactDescriptor ad : artifactDescriptors) {
                Artifact artifact = resolver.resolve(ad);
                if (artifact != null) {
                    artifactSet.add(artifact);
                }
            }
        } catch (MojoExecutionException e) {
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        StringBuffer toStringBuffer = new StringBuffer();
        toStringBuffer.append("{" + getId() + "," + getExtendedSets() + ","
                + getExtendedSetId() + ",includeDependencies=" + getIncludeDependencies() + ",resolvedArtifacts=" + getResolvedArtifacts()+
                ", "+getArtifacts().size()+" artifacts="+getArtifacts());
        return toStringBuffer.toString();
    }

}
