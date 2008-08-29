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

package org.nuxeo.build;

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@SuppressWarnings("unchecked")
public abstract class Utils {

    private Utils() {
    }

    public static String getArtifactTrail(Artifact artifact) {
        return getArtifactTrail(artifact, "  ");
    }

    public static String getArtifactTrail(Artifact artifact, String tab) {
        StringBuffer trailStringBuffer=new StringBuffer();
        List<String> trail = artifact.getDependencyTrail();
        String t = "";
        for (String id : trail) {
            trailStringBuffer.append(t + "+ " + id+System.getProperty("line.separator"));
            t += tab;
        }
        return trailStringBuffer.toString();
    }

    public static String expandVars(String expression,
            Map<Object, Object> properties) {
        int p = expression.indexOf("${");
        if (p == -1) {
            return expression; // do not expand if not needed
        }

        char[] buf = expression.toCharArray();
        StringBuffer result = new StringBuffer(buf.length);
        if (p > 0) {
            result.append(expression.substring(0, p));
        }
        StringBuffer varBuf = new StringBuffer();
        boolean dollar = false;
        boolean var = false;
        for (int i = p; i < buf.length; i++) {
            char c = buf[i];
            switch (c) {
            case '$':
                dollar = true;
                break;
            case '{':
                if (dollar) {
                    dollar = false;
                    var = true;
                }
                break;
            case '}':
                if (var) {
                    var = false;
                    String varName = varBuf.toString();
                    Object v = properties.get(varName); // get the variable
                                                        // value
                    String varValue = v == null ? null : v.toString();
                    if (varValue != null) {
                        result.append(varValue);
                    } else { // let the variable as is
                        result.append("${").append(varName).append("}");
                    }
                }
                break;
            default:
                if (var) {
                    varBuf.append(c);
                } else {
                    result.append(c);
                }
                break;
            }
        }
        return result.toString();
    }

}
