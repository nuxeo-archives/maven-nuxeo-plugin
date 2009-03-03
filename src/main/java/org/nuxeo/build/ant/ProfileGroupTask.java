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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ProfileGroupTask extends Task implements TaskContainer {
    protected String defaultProfile;
    protected List<Task> tasks = new ArrayList<Task>();
    
    public void setDefault(String value) {
        this.defaultProfile = value;
    }

    public void addTask(Task task) { 
        // at this point the task implementation can be an UnknownElement so we cannot cast them to ProfileTask
        if (!"profile".equals(task.getTaskName())) {
            throw new BuildException("Trying to insert a task which is not a profile into a profile group: " +task.getTaskName());
        }
        tasks.add(task);    
    }
    
    public void execute() throws BuildException {
        Project project = getProject(); 
        for (Task task : tasks) {
            if (ProfileTask.executeProfileTask(project, task)) {
                return; 
            }
        }        
        if (defaultProfile != null) {
            for (Task task : tasks) {
                String name = (String)task.getRuntimeConfigurableWrapper().getAttributeMap().get("name");
                if (defaultProfile.equals(name)) {
                    ProfileTask.activateProfile(project, name);
                    task.perform();
                    return;
                }
            }
        }
    }
    

}
