package org.nuxeo.build;

import java.util.Map;


public interface DependencyTree {

    Node getRoot();

    Node getNode(String id);

    Map<String, Node> getNodes();

}