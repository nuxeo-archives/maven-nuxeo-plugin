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
 *     jcarsique
 *
 * $Id$
 */

package org.nuxeo.build;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.common.xmap.Context;

/**
 * Builds full tree (parents know all their children and reciprocally)
 * @author jcarsique
 *
 */
public class DependencyTreeFull extends DependencyTreeImpl {

    @SuppressWarnings("deprecation")
    public DependencyTreeFull(Context ctx) {
        nodes = new HashMap<String, Node>();
        log=(Log) ctx.getProperty(Command.LOG);

        setRoot(ctx);
    }

    protected void setRoot(Context ctx) {
        DependencyNode rootDependencyNode = null;
        try {
            DependencyTreeBuilder dependencyTreeBuilder=(DependencyTreeBuilder) ctx.getProperty(Command.BUILDER);
            rootDependencyNode = dependencyTreeBuilder.buildDependencyTree(
                    (MavenProject) ctx.getProperty(Command.PROJECT),
                    (ArtifactRepository) ctx.getProperty(Command.REPOSITORY),
                    (ArtifactFactory) ctx.getProperty(Command.FACTORY),
                    (ArtifactMetadataSource) ctx.getProperty(Command.METADATA_SOURCE),
                    null,
                    (ArtifactCollector) ctx.getProperty(Command.COLLECTOR));
        } catch (DependencyTreeBuilderException e) {
            log.error("Cannot build project dependency tree", e);
        }
        root = putNode(rootDependencyNode);
    }

    @SuppressWarnings("unchecked")
    private Node putNode(DependencyNode dependencyNode) {
        if (dependencyNode==null) {
            return null;
        }
        Node node;
        Artifact artifact = dependencyNode.getArtifact();
        Node existingNode=nodes.get(artifact.getId());
        if (existingNode!=null) {
            node=existingNode;
        } else {
            node=new Node(nodes, artifact);
            nodes.put(artifact.getId(), node);
        }
        Iterator<DependencyNode> childrenIterator=dependencyNode.iterator();
        // skip first child (its first child is itself !); check this strange behavior
        Artifact artifactItself = childrenIterator.next().getArtifact();
        if (!artifactItself.getId().equals(artifact.getId())) {
            log.error("first child "+artifactItself+" different from artifact "+artifact);
        }
        // add children to node
        while (childrenIterator.hasNext()) {
            DependencyNode childNode= childrenIterator.next();
                node.add(putNode(childNode));
        }
        return node;
    }

}
