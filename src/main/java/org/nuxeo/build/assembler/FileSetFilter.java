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

package org.nuxeo.build.assembler;

import java.io.File;
import java.io.FileFilter;

import org.nuxeo.build.assembler.xml.ExcludePatterns;
import org.nuxeo.build.assembler.xml.IncludePatterns;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class FileSetFilter implements FileFilter {

    private int offset;

    private String root;

    private IncludePatterns includes;

    private ExcludePatterns excludes;

    public FileSetFilter(File root, IncludePatterns includes,
            ExcludePatterns excludes) {
        this.root = root.getAbsolutePath();
        this.includes = includes;
        this.excludes = excludes;
        this.offset = this.root.length() + 1; // avoid leading / when matching
    }

    public boolean accept(File file) {
        String path = file.getAbsolutePath().substring(offset);
        if (excludes != null) {
            if (excludes.match(path)) {
                return false;
            }
        }
        if (includes != null) {
            if (includes.match(path)) {
                return true;
            }
            return false;
        } else {
            return true;
        }

    }

}
