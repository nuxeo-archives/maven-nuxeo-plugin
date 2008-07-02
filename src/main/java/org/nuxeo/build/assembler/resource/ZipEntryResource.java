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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class ZipEntryResource implements Resource {

    private ZipFile file;

    private ZipEntry entry;

    public ZipEntryResource(String name, File file) throws IOException {
        this(name, new ZipFile(file));
    }

    public ZipEntryResource(String name, ZipFile file) {
        this(file.getEntry(name), file);
    }

    public ZipEntryResource(ZipEntry entry, ZipFile file) {
        this.file = file;
        this.entry = entry;
    }

    public String getName() {
        return entry.getName();
    }

    public InputStream getStream() throws IOException {
        return file.getInputStream(entry);
    }

    public boolean isFile() {
        return false;
    }

    public File getFile() {
        return null;
    }

}
