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

package org.nuxeo.build.assembler.xml;

import java.util.Map;

import org.nuxeo.common.xmap.annotation.XObject;

@XObject("serverSet")
public class ServerSet extends ArtifactSet {

    public void build(Map<Object, Object> context) {
//        Map<String, ResourceSet> setMap = mojo.getResourceSetMap();
//        context.put(Command.OUTPUT_DIRECTORY, targetDir);
//        context.put(Command.RESOURCE_SETS, setMap);
//
//
//        AssemblyDescriptor ad = ass.getDescriptor();
//        properties = ad.getProperties();
//        if (properties != null) {
//            mojo.getProject().getProperties().putAll(properties);
//        }
//        List<ArtifactSet> asets = ad.getArtifactSets();
//        if (asets != null) {
//            descriptor.getArtifactSets().addAll(asets);
//        }

    }

    @Override
    public String toString() {
        return id;
    }

}
