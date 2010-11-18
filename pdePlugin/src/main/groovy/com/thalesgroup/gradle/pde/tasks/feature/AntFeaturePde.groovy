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


package com.thalesgroup.gradle.pde.tasks.feature

import com.thalesgroup.gradle.pde.FeaturePdeConvention
import org.gradle.api.GradleException

class AntFeaturePde {
    
    void execute(FeaturePdeConvention featurePdeConvention,
    Map<String, Object> customValues,
    AntBuilder ant) {
        
        String eclipseLauncher = featurePdeConvention.getEclipseLauncher().replace('\\', '/')
        String equinoxLauncherPluginVersion = featurePdeConvention.getEquinoxLauncherPluginVersion();
        String buildDirectory = featurePdeConvention.getBuildDirectory().replace('\\', '/')
        String builderDir = featurePdeConvention.getBuilderDir().replace('\\', '/')
        String timestamp = featurePdeConvention.getTimestamp();
        String pdeBuildPluginVersion = featurePdeConvention.getPdeBuildPluginVersion();
        List extLocations = featurePdeConvention.getExtLocations();
        String jvmOptions = featurePdeConvention.getJvmOptions();
        String data = featurePdeConvention.getData();
        Boolean usePreviousLinks = featurePdeConvention.getUsePreviousLinks();
        
        
        List args = new ArrayList();
        args << "${jvmOptions}";
        if (equinoxLauncherPluginVersion) {
            args << "-jar \"${eclipseLauncher}/plugins/org.eclipse.equinox.launcher_${equinoxLauncherPluginVersion}.jar\"";
        } else {
            args << "-jar \"${eclipseLauncher}/plugins/org.eclipse.equinox.launcher.jar\"";
        }
        
        args << "-application org.eclipse.ant.core.antRunner"
        if (pdeBuildPluginVersion) {
            args << "-buildfile \"${eclipseLauncher}/plugins/org.eclipse.pde.build_${pdeBuildPluginVersion}/scripts/build.xml\""
        } else {
            args << "-buildfile \"${eclipseLauncher}/plugins/org.eclipse.pde.build/scripts/build.xml\""
        }
        args << "-Dtimestamp=${timestamp}"
        args << "-Dbuilder=\"${builderDir}\""
        
        //----------  Build the pluginPath
        if (extLocations && !usePreviousLinks) {
            String pluginPath = extLocations.join(File.pathSeparator)
            args << "-DpluginPath=${pluginPath}"
        }
        
        //Built from the given property file
        //The properties are added at the end of the command line
        //The command line properties override the default properties from the file
        if (!customValues.values().isEmpty()) {
            for (Map.Entry<String, String> entry: customValues.entrySet()){
                args << "-D" + entry.getKey() + "=" + entry.getValue()
            }
        }
        
        //-- Data directory
        args << "-data \"${data}\""
        
        
        String eclipseCommand = args.join(" ")
        
        println "[PDE Command line] java $eclipseCommand"
        println "Building in ${buildDirectory} ..."
        ant.exec(executable: "java", dir: buildDirectory, failonerror: true) { 
            arg(line: eclipseCommand) 
        }
    }
}