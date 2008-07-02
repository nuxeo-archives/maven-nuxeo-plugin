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
 *     jcarsique
 *
 * $Id$
 */

package org.nuxeo.build.assembler;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.nuxeo.build.DependencyTree;
import org.nuxeo.build.DependencyTreeFull;
import org.nuxeo.build.Node;
import org.nuxeo.build.assembler.commands.Command;
import org.nuxeo.build.assembler.resource.ResourceSet;
import org.nuxeo.build.assembler.xml.AssemblyBuilder;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.common.xmap.Context;

/**
 * @author jcarsique
 *
 */
/**
 * @author jcarsique
 *
 */
/**
 * @author jcarsique
 *
 */
public abstract class AbstractNuxeoAssembler extends AbstractMojo implements
        NuxeoAssembler {

    /**
     * Location of the file.
     *
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    public abstract void execute() throws MojoExecutionException,
            MojoFailureException;

    /**
     * Location of the file.
     *
     * @parameter expression="${descriptor}" default-value="assembly.xml"
     * @required
     */
    protected String descriptor;

    /**
     * The output directory
     *
     * @parameter expression="${outputDirectory}" default-value="target"
     * @required
     */
    protected String outputDirectory;

    /**
     * Location of the file.
     *
     * @parameter expression="${targetFile}"
     * @required
     */
    private String targetFile;

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
     * Whether to zip the result or not
     *
     * @parameter expression="${format}"
     *
     */
    protected String format;

    /**
     * Whether or not to run the preprocessor after the assembly
     *
     * @parameter expression="${runPreprocessor}"
     *
     */
    protected boolean runPreprocessor;

    protected Map<String, ResourceSet> resourceSets = new HashMap<String, ResourceSet>();

    protected ArtifactResolver artifactResolver;

    protected DependencyTree dependencyTree;

    /**
     * The dependency tree builder to use.
     *
     * @component
     * @required
     * @readonly
     */
    private DependencyTreeBuilder dependencyTreeBuilder;

    /**
     * The artifact metadata source to use.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * The artifact collector to use.
     *
     * @component
     * @required
     * @readonly
     */
    private ArtifactCollector artifactCollector;

    /**
     * @parameter expression="${basedir}"
     */
    protected File basedir;

    private static int offset=0;

    public AbstractNuxeoAssembler() {
        super();
    }

    public Map<String, ResourceSet> getResourceSetMap() {
        return resourceSets;
    }

    /**
     * @return the artifactResolver.
     */
    public ArtifactResolver getArtifactResolver() {
        if (artifactResolver == null) {
            artifactResolver = new ArtifactResolver(local,
                    remoteArtifactRepositories, resolver, factory);
        }
        return artifactResolver;
    }

    public File getBasedir() {
        return basedir;
    }

    public String getFormat() {
        return format;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public MavenProject getProject() {
        return project;
    }

    public boolean isRunPreprocessor() {
        return runPreprocessor;
    }

    public String getTargetFileName() {
        return targetFile;
    }

    /**
     * @return the resolver.
     */
    public org.apache.maven.artifact.resolver.ArtifactResolver getResolver() {
        return resolver;
    }

    public DependencyTreeBuilder getDependencyTreeBuilder() {
        return dependencyTreeBuilder;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public ArtifactCollector getArtifactCollector() {
        return artifactCollector;
    }

    public DependencyTree getDependencyTree() {
        if (dependencyTree == null) {
            Context ctx = new Context();
            ctx.setProperty(Command.MOJO, this);
            ctx.setProperty(Command.PROJECT, getProject());
//            ctx.setProperty(Command.BUILDER, new DefaultDependencyTreeBuilder());
            ctx.setProperty(Command.BUILDER, getDependencyTreeBuilder());
            ctx.setProperty(Command.REPOSITORY, local);
            ctx.setProperty(Command.FACTORY, factory);
            ctx.setProperty(Command.METADATA_SOURCE, getArtifactMetadataSource());
            ctx.setProperty(Command.COLLECTOR, getArtifactCollector());
            artifactResolver = new ArtifactResolver(local,
                    remoteArtifactRepositories, resolver, factory);
            ctx.setProperty(Command.RESOLVER, artifactResolver);
            ctx.setProperty(Command.LOG, getLog());

            dependencyTree = new DependencyTreeFull(ctx);
//            dependencyTree = new DefaultDependencyTreeBuilder().buildDependencyTree(project,
//                    local, factory, null, null);
        }
        return dependencyTree;
    }

    public Set<Artifact> getArtifactDependencies(Artifact artifact) {
        HashSet<Artifact> result = null;
        DependencyTree tree = null;
        tree = getDependencyTree();
        getLog().debug("search dependencies for "+artifact.getId());
        Node artifactNode = tree.getNode(artifact.getId());
        if (artifactNode != null) {
            result = new HashSet<Artifact>();
            getLog().debug(artifactNode.getArtifact().toString());
            Set<String> nodesKnown=new HashSet<String>();
            collectChildrenArtifacts(artifactNode, result, nodesKnown);
        }
        return result;
    }

    private void collectChildrenArtifacts(Node root,
            Collection<Artifact> result, Set<String> nodesKnown) {
        String offsetString="";
        if (getLog().isDebugEnabled()) {
            offset++;
            for (int i=0;i<offset;i++) {
                offsetString+=" ";
            }
        }
        /*
         * Avoid infinite recursion in case of cyclic dependencies
         */
        nodesKnown.add(root.getArtifact().getId());
        for (Node node : root.getChildren()) {
            Artifact artifact = node.getArtifact();
            if (!nodesKnown.contains(artifact.getId())) {
                result.add(artifact);
                getLog().debug(offsetString+"=>"+artifact);
                collectChildrenArtifacts(node, result, nodesKnown);
            }
        }
        if (getLog().isDebugEnabled()) {
            offset--;
        }
    }

    public String expandVars(String value) {
        return StringUtils.expandVars(value, project.getProperties());
    }

    public void execute(AssemblyBuilder assemblyBuilder)
            throws MojoExecutionException {
        File assemblyFile = new File(descriptor);

        // direct deps
        resourceSets.put("*", new ProjectDependencySet(project, true));
        // entire deps tree
        resourceSets.put("**", new ProjectDependencySet(project, false));

        try {
            getLog().info("Loading assembly descriptor: " + descriptor);
            Assembly assembly = assemblyBuilder.parse(assemblyFile);
            assembly.setLog(getLog());
            HashMap<Object, Object> context = new HashMap<Object, Object>();
            context.putAll(project.getProperties());
            context.put(Command.ASSEMBLY_FILE, assemblyFile);
            context.put(Command.BUILDER, assemblyBuilder);
            context.put(Command.RESOURCE_SETS, resourceSets);
            assembly.run(context);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run assembly", e);
        }
    }

    /**
     * returns resolved artifact from project
     * @param dependency
     * @return
     */
    @SuppressWarnings("unchecked")
    public Artifact getArtifact(Artifact artifact) {
        Artifact artifactFound=getArtifact(artifact.getId());
        return artifactFound!=null?artifactFound:artifact;
    }

    @SuppressWarnings("unchecked")
    public Artifact getArtifact(String artifactId) {
        Artifact artifactFound=null;
        Collection<Artifact> artifacts = getProject().getArtifacts();
        Iterator<Artifact> it = artifacts.iterator();
        while (it.hasNext()) {
            Artifact artifactTemp=it.next();
            if (artifactId.equals(artifactTemp.getId())) {
                artifactFound=artifactTemp;
                break;
            }
        }
        return artifactFound;
    }


}