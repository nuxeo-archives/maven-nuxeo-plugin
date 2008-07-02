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

package org.nuxeo.build.utils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class Patterns {

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
        int k = 0;
        for (; k < pattern.length; k++) {
            char c = pattern[k];
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
                        if (k < pattern.length - 1)
                            return false;
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
        if (wildcard && k < pattern.length) {
            // this cover cases when pattern starts with an wildcard. Ex: */.svn
            return false;
        }
        return wildcard || i == len;
    }

}
