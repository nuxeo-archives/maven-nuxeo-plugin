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

package org.nuxeo.build.assembler.resource;

import java.util.Iterator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public abstract class ArtifactResourceSet implements ResourceSet {

    public abstract Set<Artifact> getArtifacts();

    public Iterator<Artifact> artifactIterator() {
        return getArtifacts().iterator();
    }

    public Iterator<Resource> iterator() {
        return new ResourceIterator<Artifact>(artifactIterator()) {
            @Override
            protected Resource adapt(Artifact artifact) {
                return new ArtifactResource(artifact);
            }
        };
    }

}
