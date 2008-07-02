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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

public class DependencyRequiredPatternFilter implements ArtifactFilter {

    private AbstractNuxeoAssembler mojo;

    private static Map<String,Set<String>> requiredDependenciesByPattern;

    protected String pattern;

    public DependencyRequiredPatternFilter(String pattern,
            AbstractNuxeoAssembler mojo) {
        this.mojo = mojo;
        this.pattern = pattern;
        if (requiredDependenciesByPattern==null) {
            requiredDependenciesByPattern=new HashMap<String,Set<String>>();
        }
    }

    public boolean include(Artifact artifact) {
        return getRequiredDependencies().contains(artifact.getId());
    }

    private Set<String> getRequiredDependencies() {
        if (!requiredDependenciesByPattern.containsKey(pattern)) {
            requiredDependenciesByPattern.put(pattern, new HashSet<String>());
        }
        return requiredDependenciesByPattern.get(pattern);
    }

    public void add(Set<Artifact> artifacts) {
        for(Artifact artifact:artifacts) {
            add(artifact);
        }
    }

    public void add(Artifact artifact) {
        getRequiredDependencies().add(artifact.getId());
        mojo.getLog().debug("add in requiredDependencies: "+artifact);
    }
}
