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

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class ArtifactIdPatternFilter extends AbstractPatternFilter {

    public ArtifactIdPatternFilter(String pattern) {
        super(pattern);
    }

    public ArtifactIdPatternFilter(char[] pattern) {
        super(pattern);
    }

    @Override
    protected String getValueToMatch(Artifact artifact) {
        return artifact.getArtifactId();
    }

}
