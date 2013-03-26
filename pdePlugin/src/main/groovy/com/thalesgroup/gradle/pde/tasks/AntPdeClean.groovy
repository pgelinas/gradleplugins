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

import java.io.File;
import java.io.FileNotFoundException;

import org.gradle.api.internal.*;
import org.gradle.api.tasks.*;

import com.thalesgroup.gradle.pde.PdeConvention;

class AntPdeClean extends ConventionTask {
    
    @TaskAction
    void clean() {
        PdeConvention conv = project.pdeBuild;
        println "Deleting the build directory..."
        //delete the working directory
        project.delete(conv.buildDirectory);
        
        println "Creating the build directory..."
        project.mkdir(conv.buildDirectory)
        
        println "Creating the pde eclipse workspace..."
        project.mkdir(conv.data)
        
        if (conv.usePreviousLinks) {
            def destLinkDir = new File(conv.baseLocation, "links")
            
            println "Deleting old link files..."
            project.delete(destLinkDir)
            project.mkdir(destLinkDir)
            
            def rcpcleaner = "R:/extloc/platform-3.3/rcpcleaner"
            new File("$destLinkDir/org.thalesgroup.rcpcleaner.link").write("path=$rcpcleaner")
            
            try {
                new CleanTargetPlatformAction(ant, conv.baseLocation, conv.buildDirectory, conv.data).clean()
            } catch (FileNotFoundException e) {
                logger.warn "Target Platform could not be cleaned. ", e
            }
        }
    }
}