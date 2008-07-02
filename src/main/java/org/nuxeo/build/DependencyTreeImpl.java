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
 *     bstefanescu, jcarsique
 *
 * $Id$
 */

package org.nuxeo.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.common.xmap.Context;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@SuppressWarnings("unchecked")
public class DependencyTreeImpl implements DependencyTree {

    protected Map<String, Node> nodes;

    protected Node root;

    protected Log log;

    /**
     * @deprecated incomplete Tree, use {@link DependencyTreeFull} instead
     */
    public DependencyTreeImpl() {}

    /**
     * @deprecated incomplete Tree, use {@link DependencyTreeFull} instead
     */
    public DependencyTreeImpl(Context ctx) {
        nodes = new HashMap<String, Node>();
        setRoot(ctx);
    }

    protected void setRoot(Context ctx) {
        MavenProject project=(MavenProject)ctx.getProperty(Command.PROJECT);
        root = putNode(project.getArtifact());
        Collection<Artifact> artifacts = project.getArtifacts();
        for (Artifact artifact : artifacts) {
            Node node = putNode(artifact);
            String parentId = node.getParentID();
            if (parentId != null) {
                Node parentNode = putNode(parentId);
                parentNode.add(node);
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    protected Node putNode(String key) {
        Node node = nodes.get(key);
        if (node == null) {
            node = new Node(nodes);
            nodes.put(key, node);
        }
        return node;
    }

    protected Node putNode(Artifact artifact) {
        if (artifact==null) {
            return null;
        }
        Node node = putNode(artifact.getId());
        node.setArtifact(artifact);
        // TODO missing node.setChildren(children)
        return node;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

}
