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

package org.nuxeo.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;

public class Node {

    protected Map<String, Node> nodes;

    private Artifact artifact;

    private List<Node> children;

    public Node(Map<String, Node> nodes) {
        this(nodes, null);
    }

    public Node(Map<String, Node> nodes, Artifact artifact) {
        this.nodes = nodes;
        this.artifact = artifact;
        children = new ArrayList<Node>();
    }

    /**
     * @return the artifact.
     */
    public Artifact getArtifact() {
        return artifact;
    }

    public Collection<Node> getChildren() {
        return children;
    }

    public void add(Node node) {
        children.add(node);
    }

    public void clear() {
        children.clear();
    }

    @SuppressWarnings("unchecked")
    public String getParentID() {
        List<String> trail = artifact.getDependencyTrail();
        if (trail != null) {
            return trail.get(trail.size() - 2);
        }
        return null;
    }

    public Node getParent() {
        String parentId = getParentID();
        return parentId == null ? null : nodes.get(parentId);
    }

    @SuppressWarnings("unchecked")
    public String[] getDependencyTrail() {
        List<String> trail = artifact.getDependencyTrail();
        if (trail == null)
            return null;
        String[] ar = new String[trail.size()];
        for (int i = 0; i < ar.length; i++) {
            ar[i] = trail.get(i);
        }
        return ar;
    }

    @SuppressWarnings("unchecked")
    public Node[] getParents() {
        List<String> trail = artifact.getDependencyTrail();
        if (trail == null)
            return null;
        Node[] ar = new Node[trail.size()];
        for (int i = 0; i < ar.length; i++) {
            ar[i] = nodes.get(trail.get(i));
        }
        return ar;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @Override
    public String toString() {
        StringBuffer toString= new StringBuffer();
        toString.append(super.toString());
        toString.append(" ["+artifact+", "+getParents().length+" parents, "+getChildren().size()+ " children");
        return toString.toString();
    }



}