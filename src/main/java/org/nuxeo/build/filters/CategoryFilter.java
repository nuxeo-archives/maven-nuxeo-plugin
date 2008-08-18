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

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

/**
 * This filter checks one of these conditions:
 * <ul>
 * <li>artifact bundle manifest category matches pattern</li>
 * <li>artifact depends on an artifact satisfying above condition</li>
 * <li>artifact is dependency of an artifact satisfying one of both above
 * conditions
 * </ul>
 * When an artifact is satisfying one of the two first conditions, all its
 * dependencies are added in the required list for corresponding pattern
 *
 * @see ManifestBundleCategoryPatternFilter DependsOnCategoryPatternFilter
 *      DependencyRequiredPatternFilter
 */
public class CategoryFilter extends OrArtifactFilter implements ArtifactFilter {

    DependencyRequiredPatternFilter dependencyRequiredFilter;

    AbstractNuxeoAssembler mojo;

    public CategoryFilter(String pattern, AbstractNuxeoAssembler mojo) {
        this.mojo = mojo;
        add(PatternFilterFactory.createDependsOnCategoryFilter(pattern, mojo));
        add(PatternFilterFactory.createBundleCategoryFilter(pattern, mojo));
        // dependencyRequiredFilter = (DependencyRequiredPatternFilter)
        // PatternFilterFactory.createDependencyRequiredFilter(
        // pattern, mojo);
        // add(dependencyRequiredFilter);
    }

    // @Override
    // public boolean include(Artifact artifact) {
    // for (ArtifactFilter filter : filters) {
    // if (filter.include(artifact)) {
    // if (!(filter instanceof DependencyRequiredPatternFilter)) {
    // dependencyRequiredFilter.add(mojo.getArtifactDependencies(artifact));
    // }
    // return true;
    // }
    // }
    // return false;
    // }

}
