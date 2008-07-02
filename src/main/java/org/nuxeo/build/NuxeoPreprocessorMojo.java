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
import org.nuxeo.runtime.jboss.deployment.preprocessor.DeploymentPreprocessor;

/**
 * Goal whih preprocess a nuxeo EAR
 * 
 * @goal preprocess
 * 
 * @phase package
 */
public class NuxeoPreprocessorMojo extends AbstractMojo {
    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}/nuxeo.ear"
     * @required
     */
    private File earDirectory;

    public void execute() throws MojoExecutionException {
        File f = earDirectory;

        if (!f.exists()) {
            throw new MojoExecutionException("No EAR to preprocess: " + f);
        }

        System.out.println("Preprocessing: " + f);
        DeploymentPreprocessor processor = new DeploymentPreprocessor(f);
        try {
            // initialize
            processor.init();
            // and predeploy
            processor.predeploy();
        } catch (Exception e) {
            throw new MojoExecutionException("Preprocessing Failed", e);
        }
        System.out.println("Preprocessing done.");
    }

}
