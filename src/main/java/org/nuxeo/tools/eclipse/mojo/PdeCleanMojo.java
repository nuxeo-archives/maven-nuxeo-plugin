package org.nuxeo.tools.eclipse.mojo;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Clean files generated by pde goal
 * 
 * @goal cleanpde
 */
public class PdeCleanMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     */
    private org.apache.maven.project.MavenProject mavenProject;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException {
        getLog().info("Generating pde project configuration");

        List resources = mavenProject.getResources();
        String resourceDirectory = null;
        for (Object object : resources) {
            if (object instanceof Resource) {
                resourceDirectory = (((Resource) object).getDirectory());
            }
        }
        if (resourceDirectory == null) {
            throw new MojoExecutionException(
                    "An unexpected error occured: The resource directory is null");
        }

        List testResources = mavenProject.getTestResources();
        String testResourceDirectory = null;
        for (Object object : testResources) {
            if (object instanceof Resource) {
                testResourceDirectory = (((Resource) object).getDirectory());
            }
        }
        if (testResourceDirectory == null) {
            throw new MojoExecutionException(
                    "An unexpected error occured: The test resource directory is null");
        }

        FileUtils.deleteQuietly(new File(resourceDirectory + File.separator
                + ".project"));
        FileUtils.deleteQuietly(new File(resourceDirectory + File.separator
                + ".classpath"));
        FileUtils.deleteQuietly(new File(resourceDirectory + File.separator
                + ".settings"));
        FileUtils.deleteQuietly(new File(testResourceDirectory + File.separator
                + ".project"));
        FileUtils.deleteQuietly(new File(testResourceDirectory + File.separator
                + ".classpath"));
        FileUtils.deleteQuietly(new File(testResourceDirectory + File.separator
                + ".settings"));

    }
}
