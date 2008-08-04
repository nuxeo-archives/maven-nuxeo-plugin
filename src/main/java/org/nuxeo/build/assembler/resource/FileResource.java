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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class FileResource implements Resource {

    private File file;

    private String name;

    public FileResource(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public FileResource(File file) {
        this.file=file;
        this.name=(file!=null)?file.getName():"";
    }

    public String getName() {
        return name;
    }

    public InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

    public boolean isFile() {
        return file != null && file.isFile();
    }

    /**
     * @return the file.
     */
    public File getFile() {
        return file;
    }

}
