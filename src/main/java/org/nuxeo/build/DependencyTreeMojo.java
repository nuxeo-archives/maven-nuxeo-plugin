package org.nuxeo.build;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.common.xmap.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Goal which builds a dependency tree
 *
 * @goal dependency-tree
 *
 * @requiresDependencyResolution test
 *
 * @phase process-sources
 *
 */
public class DependencyTreeMojo extends AbstractMojo {

    /**
     * Location of the file.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The dependency tree builder to use.
     *
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * The artifact repository to use.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * The artifact factory to use.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * The artifact metadata source to use.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * The artifact collector to use.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    private Set<String> nodesKnown;

    /**
     * @parameter expression="${file}"
     */
    private String file;

    /**
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        Context ctx = new Context();
        ctx.setProperty(Command.MOJO, this);
        ctx.setProperty(Command.PROJECT, getProject());
        ctx.setProperty(Command.BUILDER, getDependencyTreeBuilder());
        ctx.setProperty(Command.REPOSITORY, getLocalRepository());
        ctx.setProperty(Command.FACTORY, getArtifactFactory());
        ctx.setProperty(Command.METADATA_SOURCE, getArtifactMetadataSource());
        ctx.setProperty(Command.COLLECTOR, getArtifactCollector());
//        ctx.setProperty(Command.LOG, getLog());

        DependencyTree tree = new DependencyTreeFull(ctx);
        Node root = tree.getRoot();

        try {

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element element = doc.createElement("artifact");
            element.setAttribute("id", root.getArtifact().getId());
            doc.appendChild(element);

            buildDocument(element, root);

            if (file == null) {
                file = "dependency-tree.xml";
            }
            OutputStream out = new BufferedOutputStream(new FileOutputStream(
                    new File(file)));

            try {
                DOMSerializer.write(doc, out);
            } finally {
                out.close();
            }
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(
                    "failed to export dependency tree", e);
        }
    }

    protected void buildDocument(Element parent, Node node) {
        if (nodesKnown==null) {
            nodesKnown=new HashSet<String>();
        }
        Element element = parent.getOwnerDocument().createElement("artifact");
        element.setAttribute("id", node.getArtifact().getId());
        parent.appendChild(element);
        /*
         * Avoid infinite recursion in case of cyclic dependencies
         */
        nodesKnown.add(node.getArtifact().getId());
        for (Node child : node.getChildren()) {
            if (!nodesKnown.contains(child.getArtifact().getId())) {
                buildDocument(element, child);
            } else {
                getLog().debug("not exploring again "+child.getArtifact());
                Element childElement = parent.getOwnerDocument().createElement("artifact");
                childElement.setAttribute("id", child.getArtifact().getId());
                element.appendChild(childElement);
            }
        }
    }

    public MavenProject getProject() {
        return project;
    }

    public DependencyTreeBuilder getDependencyTreeBuilder() {
        return dependencyTreeBuilder;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public ArtifactCollector getArtifactCollector() {
        return artifactCollector;
    }

}
