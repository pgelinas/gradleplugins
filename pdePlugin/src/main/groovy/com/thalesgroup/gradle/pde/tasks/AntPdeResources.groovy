 /*
 * Copyright (c) 2002-2013 Nu Echo Inc.  All rights reserved. 
 */

package com.thalesgroup.gradle.pde.tasks

import org.gradle.api.internal.*
import org.gradle.api.tasks.*

import com.google.common.io.*
import com.thalesgroup.gradle.pde.*

/**
 * @author Nu Echo Inc.
 */
class AntPdeResources extends ConventionTask {
    @TaskAction
    void copyResources() throws IOException {
        PdeConvention conv = project.pdeBuild

        File builderDir = new File(conv.builderDir)
        builderDir.mkdirs()

        if (conv.getType() == BuildType.feature) {
            new GenerateAllElementsAction().generate(conv.builderDir, conv.features)
        }

        copyBuildProperties(builderDir)
        copyCustomTargets(builderDir)

        // Init target platform
        if (conv.usePreviousLinks) {
            try {
                new CleanTargetPlatformAction(ant, conv.baseLocation, conv.buildDirectory, conv.data).clean()
            } catch (Exception e) {
                logger.warn "Target Platform could not be initialized. ", e
            }
        }

    }

    private copyCustomTargets(File fBuilderDir) {
        def customTargets = this.getClass().getResourceAsStream("/customTargets.xml")
        customTargets = new BufferedInputStream(customTargets)
        def out = new BufferedOutputStream(new FileOutputStream(new File(fBuilderDir, "customTargets.xml")))
        ByteStreams.copy(customTargets, out)
        customTargets.close()
        out.close()
    }

    private void copyBuildProperties(File fBuilderDir) {
        def buildProperties
        if (project.pdeBuild.buildPropertiesFile != null) {
            // use the user's file
            buildProperties =  new FileInputStream(project.pdeBuild.buildPropertiesFile)
        } else {
            // use the default embedded file
            buildProperties = this.getClass().getResourceAsStream("/build.properties")
        }
        buildProperties = new BufferedInputStream(buildProperties)

        def out = new BufferedOutputStream(new FileOutputStream(new File(fBuilderDir, "build.properties")))
        ByteStreams.copy(buildProperties, out)
        buildProperties.close()
        out.close()
    }
}
