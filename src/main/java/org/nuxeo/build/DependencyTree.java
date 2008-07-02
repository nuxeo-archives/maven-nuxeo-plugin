package org.nuxeo.build;

import java.util.Map;


public interface DependencyTree {

    public abstract Node getRoot();

    public abstract Node getNode(String id);

    public abstract Map<String, Node> getNodes();

}