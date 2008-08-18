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

package org.nuxeo.build.assembler.commands;

import java.util.Map;

import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public interface Command {

    String RESOURCE_SETS = "resourceSets";

    String OUTPUT_DIRECTORY = "outputDirectory";

    String ASSEMBLY_FILE = "assemblyFile";

    String BUILDER = "builder";

    String LOG = "log";

    String RESOLVER = "resolver";

    String PROJECT = "project";

    String MOJO = "mojo";

    String REPOSITORY = "repository";

    String FACTORY = "factory";

    String METADATA_SOURCE = "metadataSource";

    String COLLECTOR = "collector";

    void execute(MavenProject mavenProject, Map<Object, Object> context)
            throws Exception;

}
