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

    @XNode("@profile")
    protected String profile;

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
    public void setId(final String id) {
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
    public void setProfile(final String profile) {
        this.profile = profile;
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
    public void setArtifactDescriptors(final List<ArtifactDescriptor> artifacts) {
        this.artifactDescriptors = artifacts;
    }

    /**
     * @param mojo the mojo to set.
     */
    public void setMojo(final AbstractNuxeoAssembler mojo) {
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
    public void setExtendedSetId(final String extendedSetId) {
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
    public void setSuperSet(final ArtifactResourceSet superSet) {
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
    public void setExtendedSets(final String[] extendedSets) {
        this.importedSets = extendedSets;
    }

    /**
     * @param includeDependencies the includeDependencies to set.
     */
    public void setIncludeDependencies(final boolean includeDependencies) {
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
    public void setExcludeDependencies(final boolean excludeDependencies) {
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
        if (includes == null || includes.isEmpty()) {
            return null;
        }
        if (includeFilter == null) {
            final OrArtifactFilter filter = new OrArtifactFilter();
            for (final ArtifactDescriptor artifactDescriptor : includes) {
                filter.add(artifactDescriptor.getFilter());
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
        if (excludes == null || excludes.isEmpty()) {
            return null;
        }
        if (excludeFilter == null) {
            final OrArtifactFilter filter = new OrArtifactFilter();
            for (final ArtifactDescriptor afd : excludes) {
                filter.add(afd.getFilter());
            }
            // TODO: add transitivity filter if needed
            excludeFilter = filter;
        }
        return excludeFilter;
    }

    protected Set<Artifact> loadImportedSets() {
        final HashSet<Artifact> result = new HashSet<Artifact>();
        final Map<String, ResourceSet> sets = mojo.getResourceSetMap();
        if (importedSets != null && importedSets.length > 0) {
            for (final String setId : importedSets) {
                final ResourceSet set = sets.get(setId);
                if (!(set instanceof ArtifactResourceSet)) {
                    mojo.getLog().warn(
                            "Cannot extend set " + setId
                                    + ". Set not found or not compatible");
                    continue;
                }
                final ArtifactResourceSet arSet = (ArtifactResourceSet) set;
                final Iterator<Artifact> it = arSet.artifactIterator();
                // ESCA-JAVA0254:
                while (it.hasNext()) {
                    final Artifact artifact = it.next();
                    result.add(artifact);
                }
            }
        }
        return result;
    }

    /**
     * Get produced artifacts (after extending, resolving and filtering)
     * 
     * @return artifacts
     */
    @Override
    public Set<Artifact> getArtifacts() {
        if (artifacts == null) {
            // get base artifacts if any (empty set otherwise)
            artifacts = loadImportedSets();
            resolveArtifacts(artifacts);
            if (superSet != null) {
                final Set<Artifact> superSetArtifacts = superSet.getArtifacts();
                resolveArtifacts(superSetArtifacts);
                artifacts.addAll(superSetArtifacts);
            }
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK applyIncludeFilters");
            }
            applyIncludeFilters();
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK applyExcludeFilters");
            }
            artifacts=applyExcludeFilters(artifacts);
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK applyIncludeDependenciesFilter");
            }
            applyIncludeDependenciesFilter();
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK applyExcludeFilters");
            }
            resolveArtifacts(artifacts);
            // apply again exclusion in case of artifacts being included as
            // dependencies
            artifacts=applyExcludeFilters(artifacts);
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK applyExcludeDependenciesFilter");
            }
            applyExcludeDependenciesFilter();
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
                mojo.getLog().debug("CHECK resolveArtifacts");
            }
            collectResolvedArtifacts(artifacts);
            if (mojo.getLog().isDebugEnabled()) {
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-webapp-core "
                                + find("nuxeo-platform-webapp-core"));
                mojo.getLog().debug(
                        "CHECK nuxeo-platform-audit-facade "
                                + find("nuxeo-platform-audit-facade"));
            }
        }
        return artifacts;
    }

    private boolean find(String artifactId) {
        for (Iterator<Artifact> iterator = artifacts.iterator(); iterator.hasNext();) {
            Artifact artifact = iterator.next();
            if (artifact.getArtifactId().equals(artifactId)) {
                mojo.getLog().debug("found " + artifact.hashCode());
                return true;
            }
        }
        return false;
    }

    /**
     * apply exclude dependencies
     */
    private void applyExcludeDependenciesFilter() {
        if (excludeDependencies) {
            final HashSet<Artifact> result = new HashSet<Artifact>();
            for (final Artifact artifact : artifacts) {
                final Set<Artifact> deps = mojo.getArtifactDependencies(artifact);
                result.addAll(deps);
            }
            artifacts.removeAll(result);
        }
    }

    /**
     * apply include dependencies filter
     */
    private void applyIncludeDependenciesFilter() {
        if (includeDependencies) {
            final HashSet<Artifact> result = new HashSet<Artifact>();
            for (final Artifact artifact : artifacts) {
                final Set<Artifact> deps = mojo.getArtifactDependencies(artifact);
                if (deps != null) {
                    if (mojo.getLog().isDebugEnabled()) {
                        mojo.getLog().debug("add dependencies for " + artifact);
                        for (final Artifact dep : deps) {
                            if (!result.contains(dep)) {
                                mojo.getLog().debug("   added " + dep);
                            }
                        }
                    }
                    result.addAll(deps);
                }
            }
            artifacts.addAll(result);
        }
    }

    /**
     * apply exclude filters
     */
    private Set<Artifact> applyExcludeFilters(Set<Artifact> artifacts2) {
        Set<Artifact> result = new HashSet<Artifact>();
        final ArtifactFilter filter = getExcludeFilter();
        if (filter != null) {
            final Iterator<Artifact> it = artifacts2.iterator();
            while (it.hasNext()) {
                final Artifact nextArtifact = it.next();
                if (!filter.include(nextArtifact)) {
                    result.add(nextArtifact);
                } else {
                    mojo.getLog().debug(
                            "excluded " + nextArtifact + " "
                                    + nextArtifact.hashCode()
                                    + " (exclude filters)");
                }
            }
        return result;
        } else return artifacts2;
    }

    /**
     * apply include filters
-     * @param artifacts2
-     * @return 
     */
    private void applyIncludeFilters() {
        final ArtifactFilter filter = getIncludeFilter();
        if (filter != null) {
            final Iterator<Artifact> it = artifacts.iterator();
            while (it.hasNext()) {
                final Artifact artifact = it.next();
                if (!filter.include(artifact)) {
                    mojo.getLog().debug("removed " + artifact+" (include filters)");
                    it.remove();
                }
            }
        }
    }

    private void resolveArtifacts(final Set<Artifact> artifactsToResolve) {
        final ArtifactResolver resolver = mojo.getArtifactResolver();
        for (final Artifact artifact : artifactsToResolve) {
            if (!artifact.isResolved()) {
                try {
                    resolver.resolve(artifact);
                } catch (final MojoExecutionException e) {
                    mojo.getLog().warn(e);
                }
            }
        }
    }

    public void collectResolvedArtifacts(final Set<Artifact> artifactSet) {
        final ArtifactResolver resolver = mojo.getArtifactResolver();

        try {
            for (final ArtifactDescriptor ad : artifactDescriptors) {
                if (ad.profile != null && !mojo.isProfileActivated(ad.profile)) {
                    continue;
                }
                if (ad.version == null) { // no version given - try to guess the
                    // version using the managed versions
                    tryFillVersion(ad);
                }
                final Artifact artifact = resolver.resolve(ad);
                if (artifact != null) {
                    mojo.getLog().info("add explicitely declared "+artifact);
                    artifactSet.add(artifact);
                }
            }
        } catch (final MojoExecutionException e) {
            throw new Error(e);
        }
    }

    protected void tryFillVersion(final ArtifactDescriptor ad) {
        if (ad.group == null || ad.name == null) {
            return; // cannot guess version
        }
        Artifact artifact = null;
        final Map map = mojo.getProject().getManagedVersionMap();
        String key=ad.group+":"+ad.name;
        if (ad.type != null) {
            key += ":"+ad.type;
            artifact = (Artifact)map.get(key); // group:artifact:type:version
        } else {
            final String k = key + ":jar";
            artifact = (Artifact)map.get(k);
            if (artifact == null) {
                artifact = (Artifact)map.get(key+":ejb");
                if (artifact == null) {
                    artifact = (Artifact)map.get(key+":rar");
                }
            }
        }
        if (artifact != null) {
            ad.version = artifact.getVersion();
            ad.type = artifact.getType();
        }
    }

    @Override
    public String toString() {
        final StringBuffer toStringBuffer = new StringBuffer();
        toStringBuffer.append("{" + getId() + ", extends: " + getExtendedSets()
                + "," + getExtendedSetId() + ", includeDependencies="
                + getIncludeDependencies() + ", resolvedArtifacts="
                + getResolvedArtifacts() + ", " + getArtifacts().size()
                + " artifacts=" + getArtifacts());
        return toStringBuffer.toString();
    }

}
