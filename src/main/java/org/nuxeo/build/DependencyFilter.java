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

package org.nuxeo.build;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.filters.AndArtifactFilter;
import org.nuxeo.build.filters.PatternFilterFactory;
import org.nuxeo.build.filters.TransitiveFilter;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@SuppressWarnings("unchecked")
public class DependencyFilter {

    private ArtifactFilter filter;

    private boolean excludeTransitive;

    private String id;

    private String group;

    private String name;

    private String version;

    private String type;

    private String scope;

    private String classifier;

    private String fileName;

    private String trail;

    private final MavenProject project;

    public DependencyFilter(final MavenProject project) {
        this.project = project;
    }

    /**
     * @return the transitive.
     */
    public boolean isExcludeTransitive() {
        return excludeTransitive;
    }

    /**
     * @param transitive the transitive to set.
     */
    public void setExcludeTransitive(final boolean transitive) {
        this.excludeTransitive = transitive;
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
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return the group.
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group the group to set.
     */
    public void setGroup(final String group) {
        this.group = group;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set.
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set.
     */
    public void setScope(final String scope) {
        this.scope = scope;
    }

    /**
     * @return the classifier.
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * @param classifier the classifier to set.
     */
    public void setClassifier(final String classifier) {
        this.classifier = classifier;
    }

    /**
     * @return the file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param file the file to set.
     */
    public void setFileName(final String file) {
        this.fileName = file;
    }

    /**
     * @return the trail.
     */
    public String getTrail() {
        return trail;
    }

    /**
     * @param trail the trail to set.
     */
    public void setTrail(final String trail) {
        this.trail = trail;
    }

    /**
     * @param filter the filter to set.
     */
    public void setFilter(final ArtifactFilter filter) {
        this.filter = filter;
    }

    public ArtifactFilter getFilter() {
        if (filter == null) {
            final AndArtifactFilter andFilter = new AndArtifactFilter();
            if (id != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createIdFilter(id);
                andFilter.add(theFilter);
            }
            if (group != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createGroupFilter(group);
                andFilter.add(theFilter);
            }
            if (name != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createNameFilter(name);
                andFilter.add(theFilter);
            }
            if (version != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createVersionFilter(version);
                andFilter.add(theFilter);
            }
            if (type != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createTypeFilter(type);
                andFilter.add(theFilter);
            }
            if (scope != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createScopeFilter(scope);
                andFilter.add(theFilter);
            }
            if (classifier != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createClassifierFilter(classifier);
                andFilter.add(theFilter);
            }
            if (fileName != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createFileFilter(fileName);
                andFilter.add(theFilter);
            }
            if (trail != null) {
                final ArtifactFilter theFilter = PatternFilterFactory.createTrailFilter(trail);
                andFilter.add(theFilter);
            }

            if (excludeTransitive) {
                andFilter.add(new TransitiveFilter(project, true));
            }
            this.filter = andFilter;
        }
        return filter;
    }

    /**
     * Apply filter on all project artifacts and copy included artifacts in a
     * new collection and return it The original collection is not modified.
     *
     * @return
     */
    public Collection<Artifact> applyFilters() {
        return applyFilters(project.getArtifacts());
    }

    /**
     * Apply filter and copy included artifacts in a new collection and return
     * it The original collection is not modified
     *
     * @param artifacts
     * @return
     */
    public Collection<Artifact> applyFilters(final Collection<Artifact> artifacts) {
        final ArrayList<Artifact> result = new ArrayList<Artifact>();
        for (final Artifact artifact : artifacts) {
            if (getFilter().include(artifact)) {
                result.add(artifact);
            }
        }
        return result;
    }

    /**
     * Test if an artifact is accepted by the filters
     *
     * @param artifact
     * @return
     */
    public boolean accept(final Artifact artifact) {
        return getFilter().include(artifact);
    }

    /**
     * Apply the filters on the input collection. Each excluded artifact is put
     * into the collection given as the second argument. Included artifacts are
     * returned as the result. The input artifacts collection is not modified
     *
     * @param artifacts
     * @param excluded
     * @return
     * @throws IllegalArgumentException if excluded collection arg is null
     */
    public Collection<Artifact> applyFilters(final Collection<Artifact> artifacts,
            final Collection<Artifact> excluded) {
        if (excluded == null) {
            throw new IllegalArgumentException(
                    "excluded argument should not be null ");
        }
        final ArrayList<Artifact> result = new ArrayList<Artifact>();
        for (final Artifact artifact : artifacts) {
            if (getFilter().include(artifact)) {
                result.add(artifact);
            } else {
                excluded.add(artifact);
            }
        }
        return result;
    }

}
