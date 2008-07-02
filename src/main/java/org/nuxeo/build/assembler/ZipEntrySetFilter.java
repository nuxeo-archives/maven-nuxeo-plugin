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

import org.nuxeo.build.assembler.xml.ExcludePatterns;
import org.nuxeo.build.assembler.xml.IncludePatterns;
import org.nuxeo.common.utils.ZipEntryFilter;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class ZipEntrySetFilter implements ZipEntryFilter {

    private IncludePatterns includes;

    private ExcludePatterns excludes;

    public ZipEntrySetFilter(IncludePatterns includes, ExcludePatterns excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    public boolean accept(String entryPath) {
        if (excludes != null) {
            if (excludes.match(entryPath)) {
                return false;
            }
        }
        if (includes != null) {
            if (includes.match(entryPath)) {
                return true;
            }
            return false;
        }
        return true;
    }

}
