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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Goal which preprocess a nuxeo EAR
 *
 * @goal attach
 *
 *
 * @phase package
 *
 */
@SuppressWarnings("unchecked")
public class AttachFileMojo extends AbstractMojo {

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

    // /**
    // * @parameter expression="${artifactId}"
    // *
    // * @throws MojoExecutionException
    // */
    // private String artifactId;

    /**
     * @parameter expression="${type}"
     */
    private String type;

    /**
     * @parameter expression="${classifier}"
     */
    private String classifier;

    /**
     * Maven ProjectHelper
     *
     * @component
     */
    private MavenProjectHelper projectHelper;

    public void execute() throws MojoExecutionException {
        // project.setFile(new File(file));
        if (classifier != null) {
            if (type != null) {
                projectHelper.attachArtifact(project, type, classifier,
                        new File(file));
            } else {
                projectHelper.attachArtifact(project, new File(file),
                        classifier);
            }
        } else if (type == null) {
            throw new MojoExecutionException(
                    "Attached artifacts must define at least a type or a classifier");
        } else {
            projectHelper.attachArtifact(project, type, new File(file));
        }
    }

}
