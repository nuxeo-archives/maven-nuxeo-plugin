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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Profile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Sequential;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ProfileTask extends Sequential {

    public static final String MATCHED_PROFILES_REF = "nuxeo.matched.profiles.ref";
    public static final String ACTIVATED_PROFILES_REF = "nuxeo.activated.profiles.ref";
    
    protected String name;

    public void setName(String value) {
        this.name = value;
    }
    
    public String getProfileName() {
        return name;
    }
    
    
    public void execute() throws BuildException {        
        if (isProfileActivated(getProject(), name)) {
            getMatchedProfiles(getProject()).add(name);
            super.execute();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static boolean isProfileActivated(Project project, String name) {
        if (getActivatedProfiles(project).contains(name)) {
            return true;
        }
        MavenEnvironment env = new MavenEnvironment();
        List<Profile> profiles = env.getMavenProject().getActiveProfiles();
        for (Profile p : profiles) {
            if (p.getId().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static void activateProfile(Project project, String profile) {
        Set<String> ap = getActivatedProfiles(project);
        ap.add(profile);
     }

    public static boolean hasMatched(Project project, String profile) {
        return getMatchedProfiles(project).contains(profile);
    }
    
    @SuppressWarnings("unchecked")
    public static Set<String> getActivatedProfiles(Project project) {
        Set<String> mp = (Set<String>)project.getReference(ACTIVATED_PROFILES_REF);
        if (mp == null) {
            mp = new HashSet<String>();
            project.addReference(ACTIVATED_PROFILES_REF, mp);
        }
        return mp;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getMatchedProfiles(Project project) {
        Set<String> mp = (Set<String>)project.getReference(MATCHED_PROFILES_REF);
        if (mp == null) {
            mp = new HashSet<String>();
            project.addReference(MATCHED_PROFILES_REF, mp);
        }
        return mp;
    }

    public static boolean executeProfileTask(Project project, Task profileTask) {
        int last = getMatchedProfiles(project).size();
        profileTask.perform();
        return last != getMatchedProfiles(project).size();
    }
    
}
