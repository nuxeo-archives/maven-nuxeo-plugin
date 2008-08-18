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

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.assembler.resource.ResourceSet;

public interface NuxeoAssembler extends Mojo {

    Map<String, ResourceSet> getResourceSetMap();

    MavenProject getProject();

    Object getArtifactResolver();

    String getOutputDirectory();

    File getBasedir();

    String getTargetFileName();

    boolean isRunPreprocessor();

    String getFormat();

    String getZipRoot();

    boolean isProfileActivated(String id);

    Properties getProperties();
}