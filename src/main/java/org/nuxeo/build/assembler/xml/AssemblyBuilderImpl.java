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

import java.io.InputStream;

import org.nuxeo.build.assembler.AssemblyImpl;
import org.nuxeo.build.assembler.NuxeoAssembler;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.common.xmap.Context;
import org.nuxeo.common.xmap.XMap;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class AssemblyBuilderImpl extends AbstractAssemblyBuilder implements
        AssemblyBuilder {

    public AssemblyBuilderImpl(NuxeoAssembler mojo) {
        this.mojo = mojo;
    }

    @Override
    public AssemblyImpl parse(InputStream in) throws Exception {

        XMap xmap = new XMap();
        xmap.register(AssemblyDescriptor.class);
        Context ctx = new Context();
        ctx.setProperty(Command.MOJO, mojo);
        ctx.setProperty(Command.PROJECT, mojo.getProject());
        ctx.setProperty(Command.RESOLVER, mojo.getArtifactResolver());
//        ctx.setProperty(Command.as, mojo.getLog());
        ctx.setProperty(Command.RESOURCE_SETS, mojo.getResourceSetMap());
        AssemblyDescriptor descriptor = (AssemblyDescriptor) xmap.load(ctx, in);

        AssemblyImpl assembly = new AssemblyImpl(mojo, descriptor);

        return assembly;
    }

}
