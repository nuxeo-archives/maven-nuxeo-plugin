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
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Goal which preprocess a nuxeo EAR
 * 
 * @goal project-tree
 * 
 * @requiresDependencyResolution test
 * 
 * @phase process-sources
 * 
 */
@SuppressWarnings("unchecked")
public class ProjectTreeMojo extends AbstractMojo {

    /**
     * Location of the file.
     * 
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${file}"
     */
    private String file;

    /**
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {

        try {

            Set<Artifact> artifacts = project.getArtifacts();
            getLog().info("artifacts " + artifacts.size());

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("project");
            doc.appendChild(root);
            createArtifactElement(root, project.getArtifact());
            Element dependencies = doc.createElement("dependencies");
            root.appendChild(dependencies);

            for (Artifact artifact : artifacts) {
                createArtifactElement(dependencies, artifact);
            }

            if (file == null) {
                file = "project-tree.xml";
            }
            OutputStream out = new BufferedOutputStream(new FileOutputStream(
                    new File(file)));

            try {
                DOMSerializer.write(doc, out);
            } finally {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException(
                    "failed to export dependency tree", e);
        }
    }

    protected Element createArtifactElement(Element parent, Artifact artifact) {
        Document doc = parent.getOwnerDocument();
        Element element = doc.createElement("artifact");
        parent.appendChild(element);
        element.setAttribute("id", project.getArtifact().getId());

        Element property = doc.createElement("group");
        property.setTextContent(artifact.getGroupId());
        element.appendChild(property);

        property = doc.createElement("name");
        property.setTextContent(artifact.getArtifactId());
        element.appendChild(property);

        property = doc.createElement("version");
        property.setTextContent(artifact.getVersion());
        element.appendChild(property);

        property = doc.createElement("scope");
        property.setTextContent(artifact.getScope());
        element.appendChild(property);

        property = doc.createElement("type");
        property.setTextContent(artifact.getType());
        element.appendChild(property);

        property = doc.createElement("classifier");
        property.setTextContent(artifact.getClassifier());
        element.appendChild(property);

        property = doc.createElement("url");
        property.setTextContent(artifact.getDownloadUrl());
        element.appendChild(property);

        property = doc.createElement("file");
        File artifactFile = artifact.getFile();
        if (artifactFile != null) {
            property.setTextContent(artifactFile.getAbsolutePath());
        }
        element.appendChild(property);

        property = doc.createElement("parent");
        List<String> trail = artifact.getDependencyTrail();
        if (trail != null) {
            int size = trail.size();
            if (size > 1) {
                String parentId = trail.get(size - 2);
                property.setTextContent(parentId);
            }
        }
        element.appendChild(property);

        return element;
    }

}
