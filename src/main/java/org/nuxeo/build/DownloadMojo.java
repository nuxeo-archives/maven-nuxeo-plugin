package org.nuxeo.build;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.nuxeo.common.utils.FileUtils;

/**
 * Download an artifact file. 
 * Does not require a pom.xml in current directory when running from command line.
 *
 * @goal download
 * @phase process-resources 
 * @requiresProject false
 *
 * @author bstefanescu
 */
public class DownloadMojo extends AbstractMojo {


    /**
     * @parameter expression="${artifact}"
     * @required
     * The artifact to download (groupId:artifactId:version[:type])
     */
    private String artifact;

    /**
     * @parameter expression="${file}"
     * @required
     * The file where to download the artifact file
     */
    private File out;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;


    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

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
     * @throws MojoExecutionException, MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        String[] ar = artifact.split(":");
        if (ar.length < 3) {
            throw new MojoExecutionException("Artifact key is invalid: "+artifact+". Please use the format: groupId:artifactId:version[:type][:classifier]");
        }
        String type = ar.length == 3 ? "jar" : ar[3];
        String classifier = ar.length == 5 ? ar[4] : null;
        Artifact a = null;
        if (classifier == null) {
            a = factory.createArtifact(ar[0], ar[1], ar[2], null, type);
        } else {
            a = factory.createArtifactWithClassifier(ar[0], ar[1], ar[2], type, ar[4]);
        }
        try {
            resolver.resolveAlways(a, remoteArtifactRepositories, local);
        } catch (Exception e) {
            throw new MojoExecutionException("Artifact not found: "+a, e);
        }
        File file = a.getFile();
        File dir = out.getParentFile();
        dir.mkdirs();
        try {
            FileUtils.copy(file, out);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy file: "+file+" to "+out);
        }
        System.out.println("> downloaded to " +out.getAbsolutePath());
    }

}
