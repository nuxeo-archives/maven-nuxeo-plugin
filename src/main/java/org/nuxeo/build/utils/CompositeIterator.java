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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class CompositeIterator<T> implements java.util.Iterator<T> {

    private List<java.util.Iterator<T>> iterators = new ArrayList<java.util.Iterator<T>>();

    private int offset;

    private Iterator<T> iterator;

    public CompositeIterator() {
    }

    public void addIterator(Iterator<T> iteratorT) {
        iterators.add(iteratorT);
    }

    /**
     * @return the iterators.
     */
    public List<java.util.Iterator<T>> getIterators() {
        return iterators;
    }

    public boolean hasNext() {
        if (offset < iterators.size()) {
            return iterators.get(offset).hasNext();
        }
        return false;
    }

    public T next() {
        try {
            iterator = iterators.get(offset);
            if (!iterator.hasNext()) {
                offset++;
            }
            return iterator.next();
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException(
                    "No more iterators to iterate over");
        }
    }

    public void remove() {
        if (iterator == null) {
            throw new IllegalStateException(
                    "cannot remove an unitialized iterator entry");
        }
        iterator.remove();
    }

}
