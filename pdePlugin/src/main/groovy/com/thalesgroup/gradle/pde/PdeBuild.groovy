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

package com.thalesgroup.gradle.pde


import java.lang.reflect.Field
import java.util.jar.Manifest

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.*;
import org.gradle.api.tasks.Copy

import com.thalesgroup.gradle.pde.tasks.*
import com.thalesgroup.gradle.pde.tasks.feature.*

public class PdeBuild implements Plugin<Project> {
    public static final String CLEAN_TASK_NAME = "pdeClean"
    public static final String COPY_FEATURES_TASK_NAME = "pdeCopyFeatures"
    public static final String COPY_PLUGINS_TASK_NAME = "pdeCopyPlugins"
    public static final String INIT_TASK_NAME = "pdeInit"
    public static final String PROCESS_RESOURCES_TASK_NAME = "pdeProcessResources"
    public static final String PDE_BUILD_TASK_NAME = "pdeBuild"
    public static final String UPLOAD_TASK_NAME = "pdeUpload"


    public void apply(final Project project) {
        project.extensions.create("pdeBuild", PdeConvention)
        configureClean(project)
        configureInit(project)
        configureProcessResources(project)
        configurePdeBuild(project)
        configureDeploy(project)
    }

    private void configureClean(Project project) {
        project.task(type: AntPdeClean, description:"Deletes the build directory", CLEAN_TASK_NAME)
    }

    private void configureInit(Project project) {
        Map pathMappings = [:]
        Closure modifyCopyPath = {FileCopyDetails details, Closure rootPath ->
            def root = details.file
            def relativePath = details.relativePath
            for(def i = 1; i < relativePath.segments.length;i++){
                root = root.parentFile
            }
            RelativePath newRootPath = pathMappings[root]
            if(newRootPath == null){
                newRootPath = rootPath(root)
            }
            def newSegments = relativePath.segments[1..<relativePath.segments.length]
            def segments = newSegments.toArray(new String[newSegments.size()])
            details.relativePath = newRootPath.append(true, segments)
        }

        project.task(type: Copy, description: "Copy the features to the build directory.", COPY_FEATURES_TASK_NAME){
            PdeConvention conv = project.pdeBuild
            // Some magic here: the parameter to Copy#from is evaluated as per Project#file, which states that a closure
            // will be recursivly resolved. The resolving also happens during the action phase and not the configuration
            // phase, at which point the pdeBuild convention has all the user-defined values.
            from {conv.featuresSrcDirList}
            into {"${conv.buildDirectory}/features"}
            exclude "**/*.class"
            eachFile { FileCopyDetails details ->
                modifyCopyPath(details) { File root ->
                    def featureXml = new File(root, "feature.xml")
                    if(!featureXml.exists()) return
                    def xml = new XmlSlurper().parse(featureXml)
                    return new RelativePath(false, xml.@id.toString())
                }
            }
        }

        project.task(type: Copy, description: "Copy the plugins to the build directory.", COPY_PLUGINS_TASK_NAME){
            Map pluginMapping = [:]
            PdeConvention conv = project.pdeBuild
            from {conv.pluginsSrcDirList}
            into {"${conv.buildDirectory}/plugins"}
            exclude "**/*.class"
            eachFile { FileCopyDetails details ->
                modifyCopyPath(details) { File root ->
                    def manifestFile = new File(root, "META-INF/MANIFEST.MF")
                    if(!manifestFile.exists()) return
                        def fis = new FileInputStream(manifestFile)
                    def manifest = new Manifest(new BufferedInputStream(fis))
                    fis.close()
                    def pluginName = manifest.getMainAttributes().getValue("Bundle-SymbolicName")
                    int endName = pluginName.indexOf ';'
                    endName = endName != -1 ? endName : pluginName.size()
                    pluginName = pluginName[0..<endName]
                    return new RelativePath(false, pluginName)
                }
            }
        } << {
            // Clean up after ourselves when this is not needed anymore.
            pathMappings.clear()
        }
        
        project.task(type: AntPdeInit,
            description: "Initializes the build directory and the target platform",
            dependsOn: [
                COPY_FEATURES_TASK_NAME,
                COPY_PLUGINS_TASK_NAME
            ],
            INIT_TASK_NAME)
    }

    private void configureProcessResources(Project project) {
        project.task(type: AntPdeResources,
            dependsOn: INIT_TASK_NAME,
            description: "Processes PDE resources",
            PROCESS_RESOURCES_TASK_NAME)
    }

    private void configurePdeBuild(final Project project) {
        project.task(type: AntPdeBuild,
            dependsOn: PROCESS_RESOURCES_TASK_NAME,
            description:"Launches the PDE build process",
            PDE_BUILD_TASK_NAME)
    }

    private void configureDeploy(Project project) {
        project.task(type: AntPdeDeploy,
            dependsOn: PDE_BUILD_TASK_NAME,
            description: "Unzips artifacts produced by the PDE build into the publish directory",
            UPLOAD_TASK_NAME)
    }
}

