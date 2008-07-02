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

    private MavenProject project;

    public DependencyFilter(MavenProject project) {
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
    public void setExcludeTransitive(boolean transitive) {
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
    public void setId(String id) {
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
    public void setGroup(String group) {
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
    public void setName(String name) {
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
    public void setVersion(String version) {
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
    public void setType(String type) {
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
    public void setScope(String scope) {
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
    public void setClassifier(String classifier) {
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
    public void setFileName(String file) {
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
    public void setTrail(String trail) {
        this.trail = trail;
    }

    /**
     * @param filter the filter to set.
     */
    public void setFilter(ArtifactFilter filter) {
        this.filter = filter;
    }

    public ArtifactFilter getFilter() {
        if (filter == null) {
            AndArtifactFilter andFilter = new AndArtifactFilter();
            if (id != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createIdFilter(id);
                andFilter.add(theFilter);
            }
            if (group != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createGroupFilter(group);
                andFilter.add(theFilter);
            }
            if (name != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createNameFilter(name);
                andFilter.add(theFilter);
            }
            if (version != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createVersionFilter(version);
                andFilter.add(theFilter);
            }
            if (type != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createTypeFilter(type);
                andFilter.add(theFilter);
            }
            if (scope != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createScopeFilter(scope);
                andFilter.add(theFilter);
            }
            if (classifier != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createClassifierFilter(classifier);
                andFilter.add(theFilter);
            }
            if (fileName != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createFileFilter(fileName);
                andFilter.add(theFilter);
            }
            if (trail != null) {
                ArtifactFilter theFilter = PatternFilterFactory.createTrailFilter(trail);
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
    public Collection<Artifact> applyFilters(Collection<Artifact> artifacts) {
        ArrayList<Artifact> result = new ArrayList<Artifact>();
        for (Artifact artifact : artifacts) {
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
    public boolean accept(Artifact artifact) {
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
    public Collection<Artifact> applyFilters(Collection<Artifact> artifacts,
            Collection<Artifact> excluded) {
        if (excluded == null)
            throw new IllegalArgumentException(
                    "excluded argument should not be null ");
        ArrayList<Artifact> result = new ArrayList<Artifact>();
        for (Artifact artifact : artifacts) {
            if (getFilter().include(artifact)) {
                result.add(artifact);
            } else {
                excluded.add(artifact);
            }
        }
        return result;
    }

}
