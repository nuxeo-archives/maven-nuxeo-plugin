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

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.nuxeo.build.assembler.Assembly;

public interface AssemblyBuilder {

    Assembly parse(File file) throws Exception;

    Assembly parse(URL url) throws Exception;

    Assembly parse(InputStream in) throws Exception;

}