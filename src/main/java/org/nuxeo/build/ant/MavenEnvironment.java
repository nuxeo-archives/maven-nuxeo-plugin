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
package org.nuxeo.build.ant;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class MavenEnvironment {

    public final static String CLASS_NAME = "org.jvnet.maven.plugin.antrun.MavenComponentBag";
    
    protected static Method getMethod;
    
    
    protected Object mcb;
    private Field f_project;
    private Field f_factory;
    private Field f_artifactHandlerManager;
    private Field f_artifactMetadataSource;
    private Field f_remoteRepositories;
    private Field f_localRepository;
    private Field f_resolver;
    private Field f_projectHelper;
    private Field f_mavenProjectBuilder;    
    
    protected MavenProject project;
    protected ArtifactResolver resolver;
    
    
    public MavenEnvironment() {
        try {
            if (getMethod == null) {
                getMethod = Class.forName(CLASS_NAME).getMethod("get");
                getMethod.setAccessible(true);
            }
            mcb = getMethod.invoke(null);
            Field f = mcb.getClass().getField("project");
            f.setAccessible(true);
            project = (MavenProject)f.get(mcb);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to access "+CLASS_NAME);
        } 
    }


    public MavenProject getMavenProject() {
        try {
            if (f_project == null) {
                f_project = mcb.getClass().getField("project");
                f_project.setAccessible(true);

            } else if (project == null) {
                project = (MavenProject)f_project.get(mcb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to access org.nuxeo.build.ant.MavenComponentBag::project");  
        }
        return project;
    }
    
    public ArtifactResolver getResolver() {
        try {
            if (f_resolver == null) {
                f_resolver = mcb.getClass().getField("resolver");
                f_resolver.setAccessible(true);
            } else if (resolver == null) {
                resolver = (ArtifactResolver)f_resolver.get(mcb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to access org.nuxeo.build.ant.MavenComponentBag::resolver");  
        }
        return resolver;
    }
    
    
}
