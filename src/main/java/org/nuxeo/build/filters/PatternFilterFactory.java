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
 *     bstefanescu, jcarsique
 *
 * $Id$
 */

package org.nuxeo.build.filters;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.InversionArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

/**
 * Patterns starting with a "!" (exclamation point) are treated as inverse of
 * pattern.subString(1) (boolean NOT)
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class PatternFilterFactory {

    public static ArtifactFilter createGroupFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new GroupPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new GroupPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createNameFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new ArtifactIdPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new ArtifactIdPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createTypeFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new TypePatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new TypePatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createVersionFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new VersionPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new VersionPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createClassifierFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new ClassifierPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new ClassifierPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createScopeFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new ScopePatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new ScopePatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createFileFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new FilePatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new FilePatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createTrailFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new TrailPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new TrailPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createIdFilter(String pattern) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(new IdPatternFilter(
                    pattern.substring(1)));
        } else {
            filter = new IdPatternFilter(pattern);
        }
        return filter;
    }

    public static ArtifactFilter createBundleCategoryFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(
                    new ManifestBundleCategoryPatternFilter(
                            pattern.substring(1), mojo));
        } else {
            filter = new ManifestBundleCategoryPatternFilter(pattern, mojo);
        }
        return filter;
    }

    public static ArtifactFilter createDependsOnCategoryFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(
                    new DependsOnCategoryPatternFilter(pattern.substring(1),
                            mojo));
        } else {
            filter = new DependsOnCategoryPatternFilter(pattern, mojo);
        }
        return filter;
    }

    public static ArtifactFilter createDependencyRequiredFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        ArtifactFilter filter = null;
        if (pattern.startsWith("!")) {
            filter = new InversionArtifactFilter(
                    new DependencyRequiredPatternFilter(pattern.substring(1),
                            mojo));
        } else {
            filter = new DependencyRequiredPatternFilter(pattern, mojo);
        }
        return filter;
    }

    /**
     * This filter checks one of these conditions:
     * <ul>
     * <li>artifact bundle manifest category matches pattern</li>
     * <li>artifact depends on an artifact satisfying above condition</li>
     * <li>artifact is dependency of an artifact satisfying one of both above
     * conditions
     * </ul>
     *
     * @param pattern category to match, look for or depends on
     * @param mojo running mojo
     * @return a category filter combining (boolean inclusive OR)
     *         {@link ManifestBundleCategoryPatternFilter},
     *         {@link DependsOnCategoryPatternFilter},
     *         {@link DependencyRequiredPatternFilter}
     */
    public static ArtifactFilter createCategoryFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        return new CategoryFilter(pattern, mojo);
    }

}
