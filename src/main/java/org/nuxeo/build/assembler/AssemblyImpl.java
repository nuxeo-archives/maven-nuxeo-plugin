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
 *     Bogdan Stefanescu, Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.build.assembler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.build.assembler.resource.ArtifactResourceSet;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.assembler.xml.ArtifactSet;
import org.nuxeo.build.assembler.xml.AssemblyBuilder;
import org.nuxeo.build.assembler.xml.AssemblyDescriptor;
import org.nuxeo.build.assembler.xml.FileSet;
import org.nuxeo.build.assembler.xml.Files;
import org.nuxeo.build.assembler.xml.ZipEntrySet;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.ZipUtils;
import org.nuxeo.runtime.jboss.deployment.preprocessor.DeploymentPreprocessor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class AssemblyImpl implements Assembly {

    private NuxeoAssembler mojo;

    private MavenProject project;

    private AssemblyDescriptor descriptor;

    private File targetDir;

    // groupId:artifactId
    private Map<String, Artifact> artifactMap;

    // groupId:artifactId:type:classifier:version
    private Map<String, Artifact> artifactIdMap;

    private Log log;

    private static String FORMAT_SEPARATOR = ",";

    public AssemblyImpl(NuxeoAssembler assembler, AssemblyDescriptor descriptor) {
        this.mojo = assembler;
        this.project = assembler.getProject();
        this.descriptor = descriptor;
    }

    /**
     * @param log the log to set.
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log.
     */
    public Log getLog() {
        return log;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Artifact> getArtifactMap() {
        if (artifactMap == null) {
            artifactMap = ArtifactUtils.artifactMapByVersionlessId(project.getArtifacts());
        }
        return artifactMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Artifact> getArtifactMapById() {
        if (artifactIdMap == null) {
            artifactIdMap = ArtifactUtils.artifactMapByArtifactId(project.getArtifacts());
        }
        return artifactIdMap;
    }

    /**
     * Get an artifact given its ID. Two ID formats are supported:
     * <ul>
     * <li>groupId:artifactId:type:classifier:version
     * <li>groupId:artifactId
     * </ul>
     *
     * @param key the artifact Id.
     *
     * @return the artifact if any or null otherwise
     */
    public Artifact getArtifact(String key) {
        Artifact artifact = getArtifactMapById().get(key);
        if (artifact == null) {
            artifact = getArtifactMap().get(key);
        }
        return artifact;
    }

    /**
     * @return the descriptor.
     */
    public AssemblyDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * @return the project.
     */
    public MavenProject getProject() {
        return project;
    }

    public void run(Map<Object, Object> context) throws Exception {

        Map<Object, Object> properties = descriptor.getProperties();
        if (properties != null) {
            mojo.getProject().getProperties().putAll(properties);
        }

        if (context == null) {
            context = new HashMap<Object, Object>();
        }
        targetDir = new File(mojo.getBasedir(), mojo.getOutputDirectory());
        // TODO JC use another targetFileName...
        targetDir = new File(targetDir, mojo.getTargetFileName() + ".tmp");
        // clean temp directory if already exists, otherwise create it
        if (targetDir.isDirectory()) {
            FileUtils.emptyDirectory(targetDir);
        } else {
            targetDir.mkdirs();
        }

        Map<String, ResourceSet> setMap = mojo.getResourceSetMap();
        context.put(Command.OUTPUT_DIRECTORY, targetDir);
        context.put(Command.RESOURCE_SETS, setMap);

        // Old style compatibility
        String[] includes = descriptor.getIncludes();
        if (includes != null && includes.length > 0) {
            AssemblyBuilder builder = (AssemblyBuilder) context.get("builder");
            // read includes TODO
            File asFile = (File) context.get("assemblyFile");
            // directory of the assembly file
            asFile = asFile.getParentFile();
            for (String inc : includes) {
                File incFile = new File(asFile, inc);
                AssemblyImpl ass = (AssemblyImpl) builder.parse(incFile);
                AssemblyDescriptor ad = ass.getDescriptor();
                properties = ad.getProperties();
                if (properties != null) {
                    mojo.getProject().getProperties().putAll(properties);
                }
                List<ArtifactSet> asets = ad.getArtifactSets();
                if (asets != null) {
                    descriptor.getArtifactSets().addAll(asets);
                }
                List<FileSet> fileSets = ad.getFileSets();
                if (fileSets != null) {
                    descriptor.getFileSets().addAll(fileSets);
                }
                List<Files> files = ad.getFiles();
                if (files != null) {
                    descriptor.getFiles().addAll(files);
                }
                List<ZipEntrySet> zipsets = ad.getZipEntrySets();
                if (files != null) {
                    descriptor.getZipEntrySets().addAll(zipsets);
                }
                List<Command> cmds = ad.getCommands();
                if (cmds != null) {
                    descriptor.getCommands().addAll(cmds);
                }
            }
        }

        List<Files> files = descriptor.getFiles();
        if (files != null) {
            log.info("Processing resource files");
            for (Files set : files) {
                String id = set.getId();
                log.info("Processing set: " + id);
                if (id != null) {
                    setMap.put(id, set);
                }
            }
        }
        List<FileSet> fileSets = descriptor.getFileSets();
        if (fileSets != null) {
            log.info("Processing resource file sets");
            for (FileSet set : fileSets) {
                String id = set.getId();
                log.info("Processing set: " + id);
                if (id != null) {
                    setMap.put(id, set);
                }
            }
        }
        List<ZipEntrySet> zipEntrySets = descriptor.getZipEntrySets();
        if (zipEntrySets != null) {
            log.info("Processing zip entry sets");
            for (ZipEntrySet set : zipEntrySets) {
                String id = set.getId();
                log.info("Processing set: " + id);
                if (id != null) {
                    setMap.put(id, set);
                }
            }
        }

        List<ArtifactSet> artifactSets = descriptor.getArtifactSets();
        if (artifactSets != null) {
            log.info("Processing artifact sets");
            for (ArtifactSet set : artifactSets) {
                String id = set.getId();
                log.info("Processing set: " + id);
                if (id != null) {
                    setMap.put(id, set);
                } else {
                    String extendsSetId = set.getExtendedSetId();
                    if (extendsSetId != null) {
                        ResourceSet superSet = setMap.get(extendsSetId);
                        if (superSet instanceof ArtifactResourceSet) {
                            set.setSuperSet((ArtifactResourceSet) superSet);
                        } else if (superSet != null) {
                            log.warn("Invalid super set in extends clause: "
                                    + extendsSetId
                                    + ". A super set must be an artifact set");
                        } else {
                            log.warn("Extended set not found: " + extendsSetId
                                    + ". Ignoring...");
                        }
                        set.setId(extendsSetId);
                        setMap.put(extendsSetId, set);
                    }
                }
                log.debug(id+": "+set.getArtifacts());
            }
        }

        log.info("Processing servers sets...");
//        for (ServerSet set : descriptor.getServersSet) {
//            log.info("Collecting " + set + " components...");
//            setMap.put(set.getId(), set);
//        }

        // run commands
        log.info("Running assemble commands");
        for (Command cmd : descriptor.getCommands()) {
            cmd.execute(project, context);
        }

        if (mojo.isRunPreprocessor()) {
            log.info("Running preprocessor on " + targetDir);
            DeploymentPreprocessor processor = new DeploymentPreprocessor(
                    targetDir);
            try {
                processor.init();
                processor.predeploy();
            } catch (Exception e) {
                throw new MojoExecutionException("Preprocessing Failed", e);
            }
            System.out.println("Preprocessing done.");
        }

        // package the directory that was build and create the target file
        File targetFile = new File(mojo.getBasedir(), mojo.getOutputDirectory());
        targetFile = new File(targetFile, mojo.getTargetFileName());
        log.info("Creating target file: " + targetFile);
        if (targetFile.isFile()) {
            targetFile.delete();
        } else if (targetFile.isDirectory()) {
            FileUtils.deleteTree(targetFile);
        }

        String formats = mojo.getFormat();
        for (String pack : formats.split(FORMAT_SEPARATOR)) {
            if ("zip".equals(pack)) { // only zip format is supported for now
                                        // (except directory)
                File targetFileZiped = new File(targetFile.getParentFile(),
                        targetFile.getName() + ".zip");
                ZipUtils.zip(targetDir.listFiles(), targetFileZiped);
                log.info("Zipped target to: " + targetFileZiped);
            } else { // should be "directory" but accept anything else than
                        // zip
                FileUtils.copyTree(targetDir, targetFile);
                log.info("Copied target to: " + targetFile);
            }
        }
        FileUtils.deleteTree(targetDir);
        log.info("Done.");
    }

    protected File getOutputDirectory(String outputDirectory) {
        File dir = targetDir;
        if (outputDirectory != null) {
            dir = new File(dir, outputDirectory);
            dir.mkdirs();
        }
        return dir;
    }
}
