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

package org.nuxeo.build.assembler;

import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

public interface Assembly {

    @SuppressWarnings("unchecked")
    Map<String, Artifact> getArtifactMap();

    @SuppressWarnings("unchecked")
    Map<String, Artifact> getArtifactMapById();

    /**
     * Get an artifact given its ID. Two ID formats are supported:
     * <ul>
     * <li>groupId:artifactId:type:classifier:version
     * <li>groupId:artifactId
     * </ul>
     *
     * @param key the artifact Id.
     *
     * @return the artifact if any or null otherwise
     */
    Artifact getArtifact(String key);

    /**
     * @return the project.
     */
    MavenProject getProject();

    void run(Map<Object, Object> context) throws Exception;

    Object getDescriptor();

}