/*******************************************************************************
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
*******************************************************************************/
package com.thalesgroup.gradle.pde.tasks

import groovy.util.AntBuilder;

public class CleanTargetPlatformAction {
    
    private String baseLocation;
    private AntBuilder ant;
    private String buildDirectory;
    private String data;
    
    public static final String RCP_CLEANER_APPLICATION_ID = "com.thalesgroup.rcpcleaner.application";
    
    public CleanTargetPlatformAction(AntBuilder ant, String baseLocation, String buildDirectory, String data) {
        this.ant = ant;
        this.baseLocation = baseLocation;
        this.buildDirectory = buildDirectory;
        this.data = data;
    }
    
    public boolean clean() throws FileNotFoundException {
        println "Cleaning target platform..."

        String equinoxLauncherJar = EclipseUtils.resolveEquinoxLauncherJarFile(baseLocation);
        
        List<String> args = new ArrayList<String>()
        args << "-classpath " + equinoxLauncherJar
        args << "org.eclipse.equinox.launcher.Main"
        args << "-application " + RCP_CLEANER_APPLICATION_ID
        args << "-clean"
        args << "-data " + data
        
        println "[ant:exec] java " + args.join(" ")
        
        ant.exec(executable: "java", dir: buildDirectory, failonerror: true) { 
            arg(line: args.join(" ")) 
        }
    }
}