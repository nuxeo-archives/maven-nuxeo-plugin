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

import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which preprocess a nuxeo EAR
 * 
 * @goal find
 * 
 * @requiresDependencyResolution test
 * 
 * @phase process-sources
 * 
 */
@SuppressWarnings("unchecked")
public class DependencyFinderMojo extends AbstractMojo {
    /**
     * Location of the file.
     * 
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${pattern}"
     */
    private String pattern;

    /**
     * @parameter expression="${group}"
     */
    private String group;

    /**
     * @parameter expression="${name}"
     */
    private String name;

    /**
     * @parameter expression="${artifactVersion}"
     */
    private String version;

    /**
     * @parameter expression="${type}"
     */
    private String type;

    /**
     * @parameter expression="${scope}"
     */
    private String scope;

    /**
     * @parameter expression="${classifier}"
     */
    private String classifier;

    /**
     * @parameter expression="${excludeTransitive}"
     */
    private boolean excludeTransitive;

    public void execute() throws MojoExecutionException {

        DependencyFilter filters = new DependencyFilter(project);
        filters.setId(pattern);
        filters.setGroup(group);
        filters.setName(name);
        filters.setVersion(version);
        filters.setType(type);
        filters.setScope(scope);
        filters.setExcludeTransitive(excludeTransitive);
        filters.setClassifier(classifier);

        Collection<Artifact> artifacts = filters.applyFilters();

        getLog().info(
                " ----------------------------------------------------------------------------");
        getLog().info("Found " + artifacts.size() + " dependency artifacts");
        getLog().info("    Using pattern filter: " + pattern);
        getLog().info(
                "    Exclude transitive dependencies: " + excludeTransitive);
        getLog().info(
                " ----------------------------------------------------------------------------");

        System.out.println("");
        System.out.println("");

        for (Artifact artifact : artifacts) {
            System.out.println("===============================================================================");
            System.out.println("Artifact: " + artifact.getId());
            System.out.println("===============================================================================");
            Utils.printArtifactTrail(artifact);
            System.out.println("");
            System.out.println("");
        }

    }

}
