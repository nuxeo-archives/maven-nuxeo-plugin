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

package org.nuxeo.build.assembler.xml;

import org.nuxeo.common.utils.FilePathPattern;
import org.nuxeo.common.utils.Path;

/**
 * Base class for patterns containers
 * <p>
 * Provides common pattern matching methods
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class FilePathPatterns {

    protected FilePathPattern[] patterns;

    /**
     * @return the patterns.
     */
    public FilePathPattern[] getPatterns() {
        return patterns;
    }

    /**
     * @param patterns the patterns to set.
     */
    public void setPatterns(String[] patterns) {
        this.patterns = new FilePathPattern[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            this.patterns[i] = new FilePathPattern(patterns[i]);
        }
    }

    public void setPatterns(FilePathPattern[] patterns) {
        this.patterns = patterns;
    }

    /**
     * Find a match on the given text with one of the patterns
     * 
     * @param text the text to match
     * @return true if the text match one of the patterns false otherwise
     */
    public boolean match(String text) {
        Path path = new Path(text);
        for (FilePathPattern pattern : patterns) {
            if (pattern.match(path)) {
                return true;
            }
        }
        return false;
    }

}
