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
 *     Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.build.filters;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

/**
 *
 * @author jcarsique An artifact satisfies this filter if at least one of its
 *         dependencies match ManifestBundleCategoryPatternFilter conditions
 * @see ManifestBundleCategoryPatternFilter
 *
 */
public class DependsOnCategoryPatternFilter implements ArtifactFilter {

    private AbstractNuxeoAssembler mojo;

    private ArtifactFilter categoryFilter;

    private String pattern;

    public DependsOnCategoryPatternFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        this.mojo = mojo;
        this.pattern=pattern;
        this.categoryFilter = PatternFilterFactory.createBundleCategoryFilter(pattern, mojo);
    }

    public boolean include(Artifact artifact) {
        boolean include=false;
        Set<Artifact> dependencies = mojo.getArtifactDependencies(artifact);
        mojo.getLog().debug(DependsOnCategoryPatternFilter.class+" filtering "+artifact+" on pattern "+
                pattern +" ...");
        if (dependencies!=null) {
            for (Artifact dependency : dependencies) {
                Artifact resolvedDependency = mojo.getArtifact(dependency);
                if (categoryFilter.include(resolvedDependency)) {
                    include= true;
                    break;
                }
            }
        }
        mojo.getLog().debug("filtering on pattern "+pattern+" result for "+artifact+" : "+include);
        return include;
    }

}
