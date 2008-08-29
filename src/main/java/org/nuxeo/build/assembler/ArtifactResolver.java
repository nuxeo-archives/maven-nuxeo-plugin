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

package org.nuxeo.build.assembler;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.AttachedArtifact;
import org.nuxeo.build.assembler.xml.ArtifactDescriptor;

/**
 * Resolves artifacts.
 * <p>
 * Remote repositories are used if needed
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ArtifactResolver {

    protected org.apache.maven.artifact.repository.ArtifactRepository local;

    /**
     * List of Remote Repositories used by the resolver
     */
    protected List<ArtifactRepository> remoteRepos;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    private MavenProject project;

    /**
     *
     */
    public ArtifactResolver(MavenProject project, ArtifactRepository local,
            List<ArtifactRepository> remoteRepos,
            org.apache.maven.artifact.resolver.ArtifactResolver resolver,
            ArtifactFactory factory) {
        this.project = project;
        this.local = local;
        // ESCA-JAVA0256:
        this.remoteRepos = remoteRepos;
        this.factory = factory;
        this.resolver = resolver;
    }

    public Artifact resolve(ArtifactDescriptor ad)
            throws MojoExecutionException {
        return resolve(ad, Artifact.SCOPE_RUNTIME);
    }

    public Artifact resolve(ArtifactDescriptor ad, String scope)
            throws MojoExecutionException {
        if (ad.getGroup() == null || ad.getName() == null) {
            throw new MojoExecutionException(
                    "Invalid artifact descriptor. It should contains at least the groupId and artifactId");
        }
        if (ad.getScope() == null) {
            ad.setScope(scope);
        }
        if (ad.getType() == null) {
            ad.setType("jar");
        }
        VersionRange vr = null;
        if (ad.getVersion() != null) {
            try {
                vr = VersionRange.createFromVersionSpec(ad.getVersion());
            } catch (InvalidVersionSpecificationException e) {
                AbstractNuxeoAssembler.getLogger().error(e.getMessage());
                vr = VersionRange.createFromVersion(ad.getVersion());
            }
        }
        // look up project's attached artifacts
        Artifact artifact = resolveFromAttachedArtifacts(ad.getGroup(),
                ad.getName(), vr, ad.getType(), ad.getClassifier());
        if (artifact != null) {
            AbstractNuxeoAssembler.getLogger().debug(
                    "comparison: " + artifact.equals(factory.createDependencyArtifact(ad.getGroup(),
                            ad.getName(), vr, ad.getType(), ad.getClassifier(),
                            ad.getScope())));
            return artifact;
        }
        // use maven resolution
        artifact = factory.createDependencyArtifact(ad.getGroup(),
                ad.getName(), vr, ad.getType(), ad.getClassifier(),
                ad.getScope());
        try {
            resolver.resolve(artifact, remoteRepos, local);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact.", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact.", e);
        }
        return artifact;
    }

    @SuppressWarnings("unchecked")
    private Artifact resolveFromAttachedArtifacts(String group, String name,
            VersionRange vr, String type, String classifier) {
        Artifact artifactFound = null;
        List<AttachedArtifact> attachedArtifacts = project.getAttachedArtifacts();
        for (AttachedArtifact attachedArtifact : attachedArtifacts) {
            if (group != null && !group.equals(attachedArtifact.getGroupId())) {
                continue;
            }
            if (name != null && !name.equals(attachedArtifact.getArtifactId())) {
                continue;
            }
            if (vr != null
                    && !vr.containsVersion(new DefaultArtifactVersion(
                            attachedArtifact.getVersion()))) {
                continue;
            }
            if (type != null && !type.equals(attachedArtifact.getType())) {
                continue;
            }
            if (classifier != null
                    && !classifier.equals(attachedArtifact.getClassifier())) {
                continue;
            }
            artifactFound = attachedArtifact;
//            artifactFound.setResolved(true);
            break;
        }
        return artifactFound;
    }

    public void resolve(Artifact artifact) throws MojoExecutionException {
        try {
            resolver.resolve(artifact, remoteRepos, local);
        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact.", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact.", e);
        }
    }

}
