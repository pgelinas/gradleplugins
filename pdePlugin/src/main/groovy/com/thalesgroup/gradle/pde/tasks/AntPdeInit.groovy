/** *****************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 ****************************************************************************** */

package com.thalesgroup.gradle.pde.tasks

import java.io.File

import org.gradle.api.internal.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*

import com.google.common.base.Strings;
import com.thalesgroup.gradle.pde.PdeConvention
import com.thalesgroup.gradle.pde.BuildType

class AntPdeInit extends ConventionTask {

    @TaskAction
    void init() {
        PdeConvention pdeBuild = project.pdeBuild
        printConfig(pdeBuild)
        pdeBuild.with {
            if (usePreviousLinks) {
                //Create the destination links directory
                def destLinkDir = baseLocation + "/links"
                project.delete(destLinkDir)
                project.mkdir(destLinkDir);

                if (linksSrcDirectory) {
                    println "Fetching link files from $linksSrcDirectory..."
                    // Copy the temp links
                    project.copy {
                        fileset(dir: pdeBuild.linksSrcDirectory) { include(name: '*.link') }
                    }
                } else if (extLocations) {
                    println "Generating link files..."
                    for (String extLoc : extLocations) {
                        def linkFileName = extLoc.replaceAll("[\\\\/:]", "_")
                        linkFileName = destLinkDir + "/$linkFileName.link"
                        new File(linkFileName).write("path=$extLoc")
                        println " -> generated " + linkFileName
                    }
                }
            }

            //Create the publish directory
            if (publishDirectory) {
                println "Creating the publication directory..."
                project.mkdir(publishDirectory)
            }
        }
    }

    private void printConfig(PdeConvention pdeBuild){
        pdeBuild.with{
            println "===================================================="
            println "*                PDE PARAMETERS                    *"
            println "===================================================="
            if(type == BuildType.feature){
                println "Features                : "
                for (String feat : features) {
                    println "  -> " + feat
                }
            } else{
                println "Product File            : " + getProductFile()
            }
            println "Build directory         : " + Strings.nullToEmpty(buildDirectory)
            println "Launcher Path           : " + Strings.nullToEmpty(eclipseLauncher)
            println "Launcher Plugin Version : " + Strings.nullToEmpty(equinoxLauncherPluginVersion)
            println "PDE Plugin Version      : " + Strings.nullToEmpty(pdeBuildPluginVersion)
            println "Eclipse workspace       : " + Strings.nullToEmpty(data)
            println "Target Platform         : " + Strings.nullToEmpty(base)

            if (linksSrcDirectory) {
                println "Link files directory    : " + linksSrcDirectory
            } else {
                if (targetFile) {
                    println "Target Platform File    : $targetFile"
                }
                if (extLocations) {
                    println "Extension Locations     : "
                    extLocations.each { println " -> " + it }
                }
            }

            println "JVM Options             : " + Strings.nullToEmpty(jvmOptions)
            println "Publish directory       : " + Strings.nullToEmpty(publishDirectory)

            // ExtraPropertiesExtension, these are added by gradle when the PdeConvention object is registered as an extension.
            if (!ext.properties.isEmpty()) {
                println "----- Additional parameters -----"
                for (Map.Entry<String, String> entry: ext.properties.entrySet()) {
                    println "  -> " + entry.getKey() + " = " + entry.getValue()
                }
            }
            println "===================================================="
        }
    }
    
}