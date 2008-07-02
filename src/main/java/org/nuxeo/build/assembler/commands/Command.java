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

    public final static String RESOURCE_SETS = "resourceSets";

    public final static String OUTPUT_DIRECTORY = "outputDirectory";

    public static final String ASSEMBLY_FILE = "assemblyFile";

    public static final String BUILDER = "builder";

    public static final String LOG = "log";

    public static final String RESOLVER = "resolver";

    public static final String PROJECT = "project";

    public static final String MOJO = "mojo";

    public static final String REPOSITORY = "repository";

    public static final String FACTORY = "factory";

    public static final String METADATA_SOURCE = "metadataSource";

    public static final String COLLECTOR = "collector";

    public void execute(MavenProject project, Map<Object, Object> context)
            throws Exception;

}
