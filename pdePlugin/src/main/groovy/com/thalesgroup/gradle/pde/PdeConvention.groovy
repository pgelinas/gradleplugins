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

package com.thalesgroup.gradle.pde;

import groovy.util.XmlSlurper;

import org.gradle.api.GradleException;
import org.gradle.api.Project

public class PdeConvention {
    
    // mandatory plugin fields
    String[] features;
    BuildType type;
    String eclipseLauncher;
    String publishDirectory;
    String productFile
    
    // optional plugin fields
    String pdeBuildPluginVersion;
    String equinoxLauncherPluginVersion;
    Boolean usePreviousLinks = false;
    List<String> extLocations;
    String linksSrcDirectory;
    String buildPropertiesFile;
    String rcpCleaner = rcpCleaner;
    String eclipseExtensionsRoot = eclipseExtensionsRoot;
    String data;
    String jvmOptions = "-Xms128m -Xmx512m -XX:MaxPermSize=256m";
    String targetFile;

    // mandatory PDE properties
    String base;
    String builderDir;
    String baseLocation;
    String buildDirectory;
    String buildId = "BUILD_ID";
    
    public void setType(String type){
        this.type = BuildType.valueOf(type)
    }
    
    public String getProductFile() {
        return normPathForAnt(this.productFile);
    }
    
    void setProductFile(String productFile){
        this.productFile = productFile;
    }
    
    public List<String> getExtLocations() {
        List<String> locations = new ArrayList<String>();
        if (this.extLocations != null) {
            for (String loc : this.extLocations) {
                loc = normPathForAnt(loc)
                
                
                if (!usePreviousLinks && !loc.endsWith("/eclipse")) {
                    loc += "/eclipse"
                }
                
                if (!(new File(loc).exists())) {
                    File locFile = new File(eclipseExtensionsRoot, loc)
                        if (!locFile.exists()) {
                            throw new GradleException("${loc} does not exist.")
                        }
                    loc = locFile.toString()
                }
                
                locations << normPathForAnt(loc)
            }
        }
        
        return locations
    }
    
    public String getRcpCleaner() {
        if (rcpCleaner == null) {
            rcpCleaner = "R:/extloc/platform-3.3/rcpcleaner";
        }
        return rcpCleaner;
    }
    
    public String getBase() {
        return normPathForAnt(base)
    }
    
    public String getBaseLocation() {
        if (baseLocation == null) {
            baseLocation = base + "/eclipse"
        }
        return normPathForAnt(baseLocation)
    }
    
    public String getBuilderDir() {
        if (builderDir == null) {
            builderDir = buildDirectory + "/builder"
        }
        return normPathForAnt(builderDir)
    }
    
    public String getBuildDirectory() {
        return normPathForAnt(buildDirectory)
    }
    
    public String getEclipseLauncher() {
        return normPathForAnt(eclipseLauncher)
    }
    
    public String getEclipseExtensionsRoot() {
        return normPathForAnt(eclipseExtensionsRoot)
    }
    
    public String getData() {
        if (data == null) {
            data = "eclipsews"
        }
        if (buildDirectory) {
            return normPathForAnt(buildDirectory + "/" + data)
        } else {
            return normPathForAnt(data)
        }
    }
    
    public String getLinksSrcDirectory() {
        return normPathForAnt(linksSrcDirectory)
    }
    
    public String getPublishDirectory() {
        return normPathForAnt(publishDirectory)
    }
    
    public static String normPathForAnt(String path) {
        if (path == null) {
            return null;
        } else {
            return path.replace('\\', '/');
        }
    }
    
}
