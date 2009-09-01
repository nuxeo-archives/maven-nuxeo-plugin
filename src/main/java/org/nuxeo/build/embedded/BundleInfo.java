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
 */
package org.nuxeo.build.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class BundleInfo {

    public final File file; 
    public final Manifest mf;
    public final String id;
    public final String exports;
    public final List<String> components;
    public final String activator;
    public final boolean isRuntimeBundle;
    public final boolean isWebBundle;
    
    public BundleInfo(File bundleDir) throws IOException {
        file = bundleDir;
        mf = getManifest(bundleDir);
        id = getSymbolicName(mf);

        isRuntimeBundle = "org.nuxeo.runtime".equals(id);
        isWebBundle = new File(file, "module.xml").isFile();

        String val = null;
        if (isRuntimeBundle) { // ignore activator for runtime bundle
            activator = null;
        } else {
            val = mf.getMainAttributes().getValue("Bundle-Activator");
            if (val != null) {
                activator = val.trim();
            } else {
                activator = null;
            }
        }
    
        components = new ArrayList<String>();
        val = mf.getMainAttributes().getValue("Nuxeo-Component");
        if (val != null) {
            val = val.trim();
            if (val.length() > 0) {
                String[] ar = val.split("\\s*,\\s*");
                components.addAll(Arrays.asList(ar));
            }
        }
        
        val = mf.getMainAttributes().getValue("Export-Package");
        if (val != null) {
            val = val.trim();
            if (val.length() > 0) {
                exports = val;
            } else {
                exports = null;
            }
        } else {
            exports = null;
        }

    }
    
    protected Manifest getManifest(File bundleDir) throws IOException {
        File mani = new File(bundleDir, "META-INF/MANIFEST.MF");
        FileInputStream in = new FileInputStream(mani);
        try {
            return new Manifest(in);
        } finally {
            in.close();
        }
    }
    
    protected String getSymbolicName(Manifest mf) {
        String name = mf.getMainAttributes().getValue("Bundle-SymbolicName");
        if (name == null) {
            throw new IllegalArgumentException("No symbolic name found"); 
        }
        int p = name.indexOf(';');
        if (p > -1) {
            name = name.substring(0, p);
        }
        return name;
    }


    
}
