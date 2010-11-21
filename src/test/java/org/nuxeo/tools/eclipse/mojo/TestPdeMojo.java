package org.nuxeo.tools.eclipse.mojo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import freemarker.template.Configuration;

/**
 * Testing pde goal.
 */
public class TestPdeMojo extends TestCase {

    /**
     * Testing process method
     * 
     * @throws Exception
     */
    public void testProcessTemplate() throws Exception {

        PdeMojo mojo = new PdeMojo();
        Configuration cfg = new Configuration();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("artifactId", "sample project name");

        File file = File.createTempFile("test", null);

        mojo.processTemplate(cfg, properties, "template.project",
                file.getAbsolutePath());

        String proccedFileContent = FileUtils.readFileToString(file);
        assertTrue(
                "The generated template should contain the maven project name",
                proccedFileContent.contains("sample project name"));

    }

}
