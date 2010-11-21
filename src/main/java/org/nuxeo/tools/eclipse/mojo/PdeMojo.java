package org.nuxeo.tools.eclipse.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates project files for eclipse.
 * 
 * @goal initpde
 */
public class PdeMojo extends AbstractMojo {

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

        Configuration cfg = new Configuration();
        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("artifactId", mavenProject.getArtifactId());

            processTemplate(cfg, properties, "template.classpath",
                    resourceDirectory + File.separator + ".classpath");
            processTemplate(cfg, properties, "template.project",
                    resourceDirectory + File.separator + ".project");

            processTemplate(cfg, properties, "template.classpath",
                    testResourceDirectory + File.separator + ".classpath");
            processTemplate(cfg, properties, "test-template.project",
                    testResourceDirectory + File.separator + ".project");
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "An unexpected IO error occured while processing templates."
                            + e);
        } catch (TemplateException e) {
            throw new MojoExecutionException(
                    "An unexpected error occured  while processing templates."
                            + e);
        }

    }

    protected void processTemplate(Configuration cfg,
            Map<String, Object> properties, String templateLocation,
            String resourceDestimationLocation) throws IOException,
            TemplateException {
        try {
            cfg.setClassForTemplateLoading(getClass(), "");
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            Template tpl = cfg.getTemplate(templateLocation);
            Writer classpathWriter = new OutputStreamWriter(
                    new FileOutputStream(new File(resourceDestimationLocation)));
            tpl.process(properties, classpathWriter);
            getLog().info("Processed: " + resourceDestimationLocation);
        } catch (FileNotFoundException e) {
            getLog().error("Not processing:" + e.getMessage());
            getLog().debug("File not found", e);
        }
    }
}
