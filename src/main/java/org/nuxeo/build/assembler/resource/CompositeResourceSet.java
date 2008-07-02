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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class CompositeResourceSet implements ResourceSet {

    List<Resource> resources = new ArrayList<Resource>();

    /**
     *
     */
    public CompositeResourceSet() {
    }

    public void add(ResourceSet set) {
        for (Resource res : set) {
            resources.add(res);
        }
    }

    public void clear() {
        resources.clear();
    }

    public Iterator<Resource> iterator() {
        return resources.iterator();
    }

    public List<Resource> getResources() {
        return resources;
    }

}
