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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class StreamResource implements Resource {

    private InputStream in;

    private String name;

    public StreamResource(String name, InputStream in) {
        this.in = in;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public InputStream getStream() throws IOException {
        return in;
    }

    public boolean isFile() {
        return false;
    }

    public File getFile() {
        return null;
    }

}
