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

import groovy.util.AntBuilder

import java.util.Map

import org.gradle.api.internal.*;
import org.gradle.api.tasks.*;

import com.thalesgroup.gradle.pde.BuildType
import com.thalesgroup.gradle.pde.PdeConvention

class AntPdeBuild extends ConventionTask {
    @TaskAction
    void build() {
        PdeConvention conv = project.pdeBuild;
        conv.with {
            List args = new ArrayList()

            args << jvmOptions

            if (equinoxLauncherPluginVersion) {
                args << "-jar \"${eclipseLauncher}/plugins/org.eclipse.equinox.launcher_${equinoxLauncherPluginVersion}.jar\""
            } else {
                println "No launcher jar version supplied, automatically resolving which jar to use."
                String launcherJar = EclipseUtils.resolveEquinoxLauncherJarFile(eclipseLauncher)
                println "Found ${launcherJar}"
                args << "-jar \"${launcherJar}\""
            }

            args << "-application org.eclipse.ant.core.antRunner"

            def scriptsDir

            if (getPdeBuildPluginVersion()) {
                scriptsDir = "${eclipseLauncher}/plugins/org.eclipse.pde.build_${pdeBuildPluginVersion}/scripts"
            } else {
                println "No pde build plugin version supplied, automatically resolving the script dir."
                scriptsDir = resolveScriptDir(eclipseLauncher)
                println "Found ${scriptsDir}"
            }

            if (type == BuildType.product) {
                args << "-buildfile \"${scriptsDir}/productBuild/productBuild.xml\""
            } else {
                args << "-buildfile \"${scriptsDir}/build.xml\""
            }

            args << "-DbuildDirectory=\"${buildDirectory}\""
            args << "-Dbuilder=\"${builderDir}\""

            args << "-Dbase=\"${base}\""
            args << "-DbaseLocation=\"${baseLocation}\""

            args << "-DbuildId=\"${buildId}\""


            if (type == BuildType.product) {
                def productFile = productFile
                args << "-Dproduct=\"${productFile}\""
            }

            //----------  Build the pluginPath
            if (!usePreviousLinks && !extLocations.isEmpty()) {
                def pluginPath = extLocations.join(File.pathSeparator)
                args << "-DpluginPath=\"${pluginPath}\""
            }

            if(targetFile){
                args << "-DtargetFile=\"${targetFile}\""
            }
            
            //Built from the given property file
            //The properties are added at the end of the command line
            //The command line properties override the default properties from the file
            if (additionalProperties != null && !additionalProperties.isEmpty()) {
                additionalProperties.entrySet().each { args << "-D${it.key}=\"${it.value}\"" }
            }

            //-- Data directory
            args << "-data \"${data}\""

            String eclipseCommand = args.join(" ")

            println "[PDE Command line] java $eclipseCommand"
            println "Building in ${buildDirectory} ..."
            ant.exec(executable: "java", dir: buildDirectory, failonerror: true) {  arg(line: eclipseCommand)  }
        }
    }

    private static String resolveScriptDir(String launcherDir){
        File pluginsDir = new File(launcherDir, "plugins")
        File pdeBuildDir = pluginsDir.listFiles().find {
            it.directory && it.name.equals("org.eclipse.pde.build") || it.name.startsWith("org.eclipse.pde.build_")
        }
        return "${pdeBuildDir.absolutePath}/scripts"
    }
}
