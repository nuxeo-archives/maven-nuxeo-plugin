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

package org.nuxeo.build.filters;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Collections;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.InversionArtifactFilter;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class OrArtifactFilter implements ArtifactFilter {

    Collection<ArtifactFilter> filters = new ArrayList<ArtifactFilter>();

    public boolean include(Artifact artifact) {
        for (ArtifactFilter filter : filters) {
            if (filter.include(artifact)) {
                return true;
            }
        }
        return false;
    }

    public void add(ArtifactFilter filter) {
        filters.add(filter);
    }

    /**
     * @return the filters.
     */
    public Collection<ArtifactFilter> getFilters() {
        return Collections.unmodifiableCollection(filters);
    }

    public AndArtifactFilter invert() {
        AndArtifactFilter and = new AndArtifactFilter();
        for (ArtifactFilter filter : filters) {
            and.add(new InversionArtifactFilter(filter));
        }
        return and;
    }

}
