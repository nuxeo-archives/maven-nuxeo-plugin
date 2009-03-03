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

import java.util.List;

import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Sequential;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ProfileTask extends Sequential {
    private String name;

    public void setName(String value) {
        this.name = value;
    }

    public void execute() throws BuildException {
        MavenEnvironment env = new MavenEnvironment();
        if (isProfileActivated(env.getMavenProject(), name)) {
            super.execute();
        }
    }
    
    public static boolean isProfileActivated(MavenProject project, String name) {
        List<Profile> profiles = project.getActiveProfiles();
        for (Profile p : profiles) {
            if (p.getId().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
