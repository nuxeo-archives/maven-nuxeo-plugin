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

import java.util.Iterator;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public abstract class ResourceIterator<T> implements Iterator<Resource> {

    private Iterator<T> it;

    public ResourceIterator(Iterable<T> it) {
        this(it.iterator());
    }

    public ResourceIterator(Iterator<T> it) {
        this.it = it;
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public void remove() {
        it.remove();
    }

    public Resource next() {
        return adapt(it.next());
    }

    protected abstract Resource adapt(T object);
}
