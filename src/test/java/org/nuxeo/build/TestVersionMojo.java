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
 *     Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import junitx.framework.ArrayAssert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @author jcarsique
 *
 */
public class TestVersionMojo {

    private static final Log log = LogFactory.getLog(TestVersionMojo.class);

    private static final String[] testVersions = new String[] { "5.2-SNAPSHOT",
            "5.2", "5.2.m2", "5.1.3-SNAPSHOT", "5.1.3", "5.2-20080807",
            "5.2.rc1", "5.1.4-SNAPSHOT", "5.1.3-20080807", "5.1.3.rc1",
            "5.1.3.2", "5.1.4", "5.5.3", "5.5.3_02" };

    private static final int[] expectedOrder = new int[] { 9, 10, 11, 2, 3, 8,
            12, 6, 1, 5, 4, 7, 13,14 };

    private static final String eclipseFormat = "^(\\d+\\.){3}.*$";

    // private static final String[] expectedVersions = new String[] {
    // "5.2.0.-SNAPSHOT", "5.2.0.0", "5.2.0.m2", "5.1.3.-SNAPSHOT",
    // "5.1.3.0" };

    /**
     * Test method for {@link org.nuxeo.build.VersionMojo#execute()}.
     */
    @Test
    public void testExecute() {
        String[] resultArray = new String[testVersions.length];
        String[] resultExpectedOrdered = new String[testVersions.length];
        for (int i = 0; i < testVersions.length; i++) {
            resultArray[i] = new VersionMojo().executeMojo(testVersions[i]);
            log.info(testVersions[i] + " => " + resultArray[i]);
            Assert.assertTrue(resultArray[i] + " don't match " + eclipseFormat,
                    resultArray[i].matches(eclipseFormat));
            resultExpectedOrdered[expectedOrder[i] - 1] = resultArray[i];
        }

        // ArrayAssert.assertEquals(expectedVersions, resultArray);

        List<String> resultSorted = new ArrayList<String>(
                Arrays.asList(resultArray));
        Collections.sort(resultSorted);
        ArrayAssert.assertEquals("Bad sort result", resultExpectedOrdered,
                resultSorted.toArray());
    }
}
