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
 *     Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.build.filters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ManifestBundleCategoryPatternFilter extends AbstractPatternFilter {

    public static final String MANIFEST_BUNDLE_CATEGORY = "Bundle-Category";
    public static final String MANIFEST_BUNDLE_CATEGORY_TOKEN = ",";

    public ManifestBundleCategoryPatternFilter(String pattern,AbstractNuxeoAssembler mojo) {
        super(pattern,mojo);
    }

    public ManifestBundleCategoryPatternFilter(char[] pattern,AbstractNuxeoAssembler mojo) {
        super(pattern,mojo);
    }

    protected List<String> getValuesToMatch(Artifact artifact) {
        List<String> valuesToMatch =new ArrayList<String>();
        File file=artifact.getFile();
        if (file==null) {
            if (artifact.isResolved()) {
                mojo.getLog().warn("Artifact "+artifact+" doesn't contain a file");
            } else {
                mojo.getLog().warn("Artifact "+artifact+" unresolved");
            }
            return valuesToMatch;
        }
        // ignore pom files
        if (file.getName().endsWith(".pom")) {
            return valuesToMatch;
        }
        try {
            JarFile jarFile = new JarFile(file, true);
            Manifest mf = jarFile.getManifest();
            if (mf!=null) {
                Attributes attributes=mf.getMainAttributes();
                if (attributes!=null) {
                    String bundleCategories= attributes.getValue(MANIFEST_BUNDLE_CATEGORY);
                    if (bundleCategories!=null) {
                        StringTokenizer st=new StringTokenizer(bundleCategories,MANIFEST_BUNDLE_CATEGORY_TOKEN);
                        while(st.hasMoreTokens()) {
                            valuesToMatch.add(st.nextToken());
                        }
                    }
                }
            } else {
                mojo.getLog().warn("Artifact "+artifact+" doesn't contain a manifest");
            }
        } catch (IOException e) {
            mojo.getLog().error("error while inspecting this jar manifest: "
                    + artifact.getFile(), e);
        }
        return valuesToMatch;
    }

    /**
     * must not be called, always returns null.
     */
    @Override
    protected String getValueToMatch(Artifact artifact) {
        return null;
    }

    @Override
    public boolean include(Artifact artifact) {
        boolean include=matchPattern(getValuesToMatch(artifact));
        mojo.getLog().debug((include?"accepts ":"rejects ")+artifact);
        return include;
    }

    private boolean matchPattern(List<String> valuesToMatch) {
        for (String valueToMatch : valuesToMatch) {
            if (matchPattern(valueToMatch, pattern)) {
                return true;
            }
        }
        return false;
    }

}
