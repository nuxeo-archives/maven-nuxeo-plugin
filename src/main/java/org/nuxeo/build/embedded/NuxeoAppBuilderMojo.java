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
 */
package org.nuxeo.build.embedded;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.assembler.ArtifactResolver;
import org.nuxeo.build.assembler.xml.ArtifactDescriptor;
import org.nuxeo.common.utils.StringUtils;

/**
 * Build an all in one Nuxeo JAR
 *
 * @goal allinone
 *
 * @phase package
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class NuxeoAppBuilderMojo extends AbstractMojo {

    /**
     * Location of the file.
     *
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;
    
    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List<ArtifactRepository> remoteArtifactRepositories;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * @parameter expression="${src}"
     * @required
     */
    private File src;

    /**
     * @parameter expression="${target}"
     * @required
     */
    private File target;

    /**
     * @parameter expression="${launcher}"
     * @required
     */
    private String launcher;

    /**
     * @parameter expression="${includeNestedJars}" default-value="true"
     */
    private boolean includeNestedJars;

    /**
     * @parameter expression="${excludeLibs}" default-value="false"
     */
    private boolean excludeLibs;    
        
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!isProfileActivated("embedded-standalone") && !isProfileActivated("embedded-minimal") && !isProfileActivated("embedded-osgi")) {
            System.out.println("Skiping all-in-one task. Use -Pembedded-standalone or -Pembedded-minimal or -Pembedded-osgi to activate it!");
            return;
        }
        NuxeoAppBuilder builder = new NuxeoAppBuilder();
        String[] ar = StringUtils.split(launcher, ':', false);
        if (ar.length != 3) {
            throw new MojoFailureException("Invalid launcher format. Must use a string like groupId:artifactId:version to specify the launcher");
        }
        ArtifactDescriptor ad = new ArtifactDescriptor();
        ad.setGroup(ar[0]);
        ad.setName(ar[1]);
        ad.setVersion(ar[2]);
        ArtifactResolver aresolver = new ArtifactResolver(project, local, remoteArtifactRepositories, resolver, factory);
        Artifact arti = aresolver.resolve(ad);
        File ljar = arti.getFile();
        builder.setCopyEmbeddedJars(includeNestedJars);
        builder.setExcludeLibs(excludeLibs);
        if (isProfileActivated("embedded-osgi")) {
            builder.setExcludeOsgi(true);   
        }
        try {
            builder.process(src, target, ljar);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to build all in one JAR for nuxeo", e);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isProfileActivated(String id) {
        List<Profile> profiles = project.getActiveProfiles();
        for (Profile p : profiles) {
            if (p.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    
}
