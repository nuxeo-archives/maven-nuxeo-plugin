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
 *     bstefanescu, jcarsique
 *
 * $Id$
 */

package org.nuxeo.build.assembler.xml;

import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.nuxeo.build.assembler.AbstractNuxeoAssembler;
import org.nuxeo.build.filters.OrArtifactFilter;
import org.nuxeo.build.filters.PatternFilterFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.common.xmap.annotation.XContext;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("artifact")
public class ArtifactDescriptor {

    private static final String CATEGORIES_SEPARATOR = ",";

    @XContext("mojo")
    protected AbstractNuxeoAssembler mojo;

    @XNode("@group")
    void setParametrizedGroup(String value) {
        group = StringUtils.expandVars(value, mojo.getProperties());
    }

    protected String group;

    @XNode("@name")
    void setParametrizedName(String value) {
        name = StringUtils.expandVars(value, mojo.getProperties());
    }

    protected String name;

    @XNode("@type")
    protected String type;

    @XNode("@version")
    void setParametrizedVersion(String value) {
        version = StringUtils.expandVars(value, mojo.getProperties());
    }

    protected String version;

    @XNode("@scope")
    protected String scope;

    @XNode("@classifier")
    protected String classifier;

    @XNode("@file")
    protected String file;

    @XNode("@trail")
    protected String trail;

    @XNode("@transitive")
    protected String transitive;

    /**
     * Pattern to match for dependencies with Bundle-Category in Manifest; may
     * contain multiple categories separated by {@value #CATEGORIES_SEPARATOR}.
     */
    @XNode("@category")
    protected String categories;

    /**
     * Whether to include dependencies depending on at least one dependency
     * satisfying category pattern
     */
    @XNode("@includeDependsOnCategory")
    protected boolean includeDependsOnCategory = true;

    @XNode("@profile")
    protected String profile;

    private ArtifactFilter filter;

    /**
     * Create an artifact descriptor from a string of the forms:
     * <ul>
     * <li> groupId:artfactId:version
     * <li>groupId:artfactId:version:type
     * </ul>
     *
     * @param versionId
     * @return
     */
    public static ArtifactDescriptor fromVersionId(String versionId) {
        String[] ar = StringUtils.split(versionId, ':', false);
        ArtifactDescriptor ad = new ArtifactDescriptor();
        ad.group = ar[0];
        if (ar.length > 1) {
            ad.name = ar[1];
        }
        if (ar.length > 2) {
            ad.version = ar[2];
        }
        if (ar.length > 3) {
            ad.type = ar[3];
        }
        if (ar.length > 4) {
            ad.classifier = ar[4];
        }
        return ad;
    }

    public ArtifactFilter getFilter() {
        if (filter == null) {
            AndArtifactFilter andFilter = new AndArtifactFilter();
            if (group != null) {
                andFilter.add(PatternFilterFactory.createGroupFilter(group));
            }
            if (name != null) {
                andFilter.add(PatternFilterFactory.createNameFilter(name));
            }
            if (type != null) {
                andFilter.add(PatternFilterFactory.createTypeFilter(type));
            }
            if (scope != null) {
                andFilter.add(PatternFilterFactory.createScopeFilter(scope));
            }
            if (version != null) {
                andFilter.add(PatternFilterFactory.createVersionFilter(version));
            }
            if (classifier != null) {
                andFilter.add(PatternFilterFactory.createClassifierFilter(classifier));
            }
            if (file != null) {
                andFilter.add(PatternFilterFactory.createFileFilter(file));
            }
            if (trail != null) {
                andFilter.add(PatternFilterFactory.createTrailFilter(trail));
            }
            if (getCategories() != null && getCategories().length > 0) {
                OrArtifactFilter orFilter = new OrArtifactFilter();
                for (String category : getCategories()) {
                    // andFilter.add(PatternFilterFactory.createCategoryFilter(category,mojo));
                    ArtifactFilter bundleCategoryFilter = PatternFilterFactory.createBundleCategoryFilter(
                            category, mojo);
                    if (includeDependsOnCategory) {
                        orFilter.add(PatternFilterFactory.createDependsOnCategoryFilter(
                                category, mojo));
                    }
                    orFilter.add(bundleCategoryFilter);
                }
                andFilter.add(orFilter);
            }
            filter = andFilter;
        }
        return filter;
    }

    public String[] getCategories() {
        return categories != null ? categories.split(CATEGORIES_SEPARATOR)
                : null;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

}
