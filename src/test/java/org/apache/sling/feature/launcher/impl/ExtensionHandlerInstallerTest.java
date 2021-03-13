package org.apache.sling.feature.launcher.impl;

import java.net.URL;

public class ExtensionHandlerInstallerTest {

    //@Test
    public void testClassPath() throws Exception {

        // manual Test

        URL testFeatureFile = getClass().getResource("/feature-apiregion.json");
        Main.main(new String[] { "-f", testFeatureFile.getFile() });

    }

}
