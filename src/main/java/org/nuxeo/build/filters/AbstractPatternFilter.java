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
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public abstract class AbstractPatternFilter implements ArtifactFilter {

    protected char[] pattern;
    protected AbstractNuxeoAssembler mojo;

    protected abstract String getValueToMatch(Artifact artifact);

    protected AbstractPatternFilter(String pattern) {
        this(pattern.toCharArray());
    }

    protected AbstractPatternFilter(final char[] pattern) {
        this.pattern = pattern.clone();
    }

    protected AbstractPatternFilter(String pattern, AbstractNuxeoAssembler mojo) {
        this(pattern.toCharArray(),mojo);
    }

    protected AbstractPatternFilter(final char[] pattern, AbstractNuxeoAssembler mojo) {
        this.pattern=pattern.clone();
        this.mojo=mojo;
    }

    /**
     * @param pattern the pattern to set.
     */
    public void setPattern(final char[] pattern) {
        this.pattern = pattern.clone();
    }

    public void setPattern(String pattern) {
        this.pattern = pattern.toCharArray();
    }

    /**
     * @return the pattern.
     */
    public char[] getPattern() {
        return pattern.clone();
    }

    public boolean include(Artifact artifact) {
        return matchPattern(getValueToMatch(artifact), pattern);
    }

    public static boolean matchPattern(String name, char[] pattern) {
        return matchPattern(name.toCharArray(), pattern);
    }

    public static boolean matchPattern(char[] name, char[] pattern) {
        return matchPattern(name, 0, name.length, pattern);
    }

    public static boolean matchPattern(char[] name, int offset, int len,
            char[] pattern) {
        int i = offset;
        boolean wildcard = false;
        for (char c : pattern) {
            switch (c) {
            case '*':
                wildcard = true;
                break;
            case '?':
                i++;
                break;
            default:
                if (wildcard) {
                    while (i < len) {
                        if (name[i++] == c) {
                            break;
                        }
                    }
                    if (i == len) {
                        return true;
                    }
                    wildcard = false;
                } else if (i >= len || name[i] != c) {
                    return false;
                } else {
                    i++;
                }
                break;
            }
        }
        return wildcard || i == len;
    }

}
