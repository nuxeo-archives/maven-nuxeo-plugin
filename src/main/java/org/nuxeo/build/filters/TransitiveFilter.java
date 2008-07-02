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

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
@SuppressWarnings("unchecked")
public class TransitiveFilter implements ArtifactFilter {

    private boolean excludeTransitive = false;

    private Set<Artifact> directArtifacts;

    public TransitiveFilter(MavenProject project, boolean excludeTransitive) {
        this.excludeTransitive = excludeTransitive;
        this.directArtifacts = project.getDependencyArtifacts();
    }

    public boolean include(Artifact artifact) {
        if (excludeTransitive && !directArtifacts.contains(artifact)) {
            return false;
        }
        return true;
    }

}
