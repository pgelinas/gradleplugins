 /*
 * Copyright (c) 2002-2013 Nu Echo Inc.  All rights reserved. 
 */

package com.thalesgroup.gradle.pde.tasks

import org.gradle.api.internal.*
import org.gradle.api.tasks.*;

import com.thalesgroup.gradle.pde.*;

/**
 * @author Nu Echo Inc.
 */
class AntPdeResources extends ConventionTask {
    @TaskAction
    void copyResources() throws IOException {
        PdeConvention conv = project.pdeBuild;
        String buildDirectory = conv.getBuildDirectory();

        File fBuilderDir = new File(conv.getBuilderDir());
        fBuilderDir.mkdirs();

        if (conv.getType() == BuildType.feature) {
            String[] features =  conv.getFeatures();
            new GenerateAllElementsAction().generate(conv.getBuilderDir(), features);
        }

        // build.properties
        InputStream buildPropertiesIs;
        if (conv.getBuildPropertiesFile() != null) {
            // use the user's file
            buildPropertiesIs = new FileInputStream(conv.getBuildPropertiesFile());
        } else {
            // use the default embedded file
            buildPropertiesIs = this.getClass().getResourceAsStream("/build.properties");
        }
        AntUtil.copyFile(buildPropertiesIs, fBuilderDir, "build.properties");
        buildPropertiesIs.close();

        // Init target platform
        if (conv.getUsePreviousLinks()) {
            try {
                new CleanTargetPlatformAction(ant, conv.getBaseLocation(), buildDirectory, conv
                        .getData()).clean();
            } catch (Exception e) {
                System.out.println("WARNING! Target Platform could not be initialized. "
                        + e.toString());
            }
        }

        // customTargets.xml
        InputStream customTargetsIs = this.getClass().getResourceAsStream(
                "/customTargets.xml");
        AntUtil.copyFile(customTargetsIs, fBuilderDir, "customTargets.xml");
        customTargetsIs.close();
    }
}
