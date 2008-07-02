package org.nuxeo.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which preprocess a nuxeo EAR
 * 
 * @goal eclipse-version
 * 
 * @phase process-sources
 * 
 */
public class VersionMojo extends AbstractMojo {

    /**
     * Project instance to analyze.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private static final String snapTag = "-SNAPSHOT";

    /**
     * @throws MojoExecutionException, MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        String version = project.getVersion();
        if (version.length() > 1) {
            String eclipseVersion = executeMojo(version);
            project.getProperties().setProperty("eclipseVersion",
                    eclipseVersion);
            getLog().info("eclipseVersion:" + eclipseVersion);
        }
    }

    private String executeMojo(String ver) {
        String eclipseVersion;
        int snapIdx = ver.indexOf(snapTag);
        boolean isSnapshot = snapIdx > 0;
        if (isSnapshot) {
            eclipseVersion = ver.substring(0, snapIdx);
        } else {
            eclipseVersion = ver;
        }
        eclipseVersion = format(eclipseVersion, isSnapshot);
        if (isSnapshot) {
            eclipseVersion += snapTag;
        }
        return eclipseVersion;
    }

    private String format(String shortVersion, boolean isSnapshot) {
        // test if it's a "branch" version x.y.z or "trunk" version x.y
        if (shortVersion.matches("(\\d+\\.){2}\\d+")) {
            if (isSnapshot) {
                return shortVersion + ".";
            } else {
                return shortVersion + ".0";
            }
        }
        if (shortVersion.matches("(\\d+\\.)\\d+")) {
            if (isSnapshot) {
                return shortVersion + ".0.";
            } else {
                return shortVersion + ".0.0";
            }
        }
        return shortVersion;
    }

    public static void main(String[] args) {
        List<String> l = new ArrayList<String>(Arrays.asList(
                new VersionMojo().executeMojo("5.2-SNAPSHOT"),
                new VersionMojo().executeMojo("5.2"),
                new VersionMojo().executeMojo("5.1.3-SNAPSHOT"),
                new VersionMojo().executeMojo("5.1.3")));
        Collections.sort(l);
        System.out.println(l);
    }

}
