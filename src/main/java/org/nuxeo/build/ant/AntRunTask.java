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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.ImportTask;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class AntRunTask extends ImportTask {

    private List<String> targets = new ArrayList<String>();
    private String file;
    private boolean optional;
    private static final FileUtils FILE_UTILS = FileUtils.newFileUtils();

    /**
     * sets the optional attribute
     *
     * @param optional if true ignore files that are not present,
     *                 default is false
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * the name of the file to import. How relative paths are resolved is still
     * in flux: use absolute paths for safety.
     * @param file the name of the file
     */
    public void setFile(String file) {
        // I don't think we can use File - different rules
        // for relative paths.
        this.file = file;
    }
    
    
    public void setTarget(String target) {
        if (target == null || target.length() == 0) {
            return;
        }
        for (String t : StringUtils.split(target, ",")) {
            this.targets.add(t);    
        }        
    }
    
    @Override
    public void execute() throws BuildException {
        File f = new File(getProject().replaceProperties(file));
        if (!f.exists()) {
            String message =
                "Cannot find " + file + " imported from antrun";
            if (optional) {
                getProject().log(message, Project.MSG_VERBOSE);
                return;
            } else {
                throw new BuildException(message);
            }
        }
        try {
            Project project = getProject();
            project.getTargets().remove(""); // remove implicit target to avoid errors when importing
            ProjectHelper.getProjectHelper().parse(project, f);
            
            Target t = (Target)project.getTargets().get("");
            if (t != null) {
                t.execute();
            }
            if (targets != null && !targets.isEmpty()) {
                for (String target : targets) {
                    t = (Target)project.getTargets().get(target);
                    if (t != null) {
                        t.execute();
                    } else {
                        project.log("Unknown target: "+target, Project.MSG_VERBOSE);    
                    }
                }
            } else if (project.getDefaultTarget() != null) {
                t = (Target)project.getTargets().get(project.getDefaultTarget());
                if (t != null) {
                    t.execute();
                }
            }
        } catch (BuildException ex) {
            throw ProjectHelper.addLocationToBuildException(
                    ex, getLocation());
        }
    }
    
}
