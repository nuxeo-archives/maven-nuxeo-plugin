package org.nuxeo.build;

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

    /**
     * @throws MojoExecutionException, MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String version = project.getVersion();
        if (version.length() > 1) {
            final String eclipseVersion = executeMojo(version);
            project.getProperties().setProperty("eclipseVersion",
                    eclipseVersion);
            getLog().info("eclipseVersion:" + eclipseVersion);
        }
    }

    /**
     * Executes the version transformation on the given parameter
     *
     * @param version String to transform
     * @return
     */
    public String executeMojo(final String version) {
        String eclipseVersion;
        final String[] versionSplitted = version.split("^(\\d+\\.)+\\d*");
        String trailingTag = (versionSplitted.length>0)?versionSplitted[versionSplitted.length - 1]:null;

        if (trailingTag!=null) {
            final int tagIdx = version.indexOf(trailingTag);
            eclipseVersion = version.substring(0, tagIdx);
            trailingTag=trailingTag.replace(".", "_");
        } else {
            eclipseVersion = version;
        }
        eclipseVersion = format(eclipseVersion,trailingTag);
        if (trailingTag!=null) {
            if (!eclipseVersion.endsWith(".") && !trailingTag.startsWith(".")) {
                eclipseVersion += ".";
            }
            eclipseVersion += trailingTag;
        }
        return eclipseVersion;
    }

    private static String format(final String shortVersion, final String trailingTag) {
        // test if it's a "branch" version x.y.z or "trunk" version x.y
        if (shortVersion.matches("^(\\d+\\.){3}.*")) {
            return shortVersion;
        }
//        if (shortVersion.matches("^(\\d+\\.){2}\\d+")) {
//            return shortVersion +".0";
//        }
        if (shortVersion.matches("^(\\d+\\.){2}.*")) {
            if (shortVersion.endsWith(".")) {
                return shortVersion + "0.";
            }
            if (trailingTag==null) {
                return shortVersion + ".0";
            }
            return shortVersion;
        }
        if (shortVersion.matches("^\\d+\\..*")) {
            if (shortVersion.endsWith(".")) {
                return shortVersion + "0.0.";
            }
            if (trailingTag==null) {
                return shortVersion + ".0.0";
            }
            return shortVersion + ".0";
        }
        return shortVersion;
    }

}
