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

import org.gradle.api.internal.*;
import org.gradle.api.tasks.*;

import com.thalesgroup.gradle.pde.PdeConvention;

class AntPdeInit extends ConventionTask {

    @TaskAction
    void init() {
        PdeConvention conv = project.pdeBuild;
        conv.print();
        if (conv.getUsePreviousLinks()) {
            //Create the destination links directory
            def destLinkDir = conv.getBaseLocation() + "/links"
            ant.delete(dir: destLinkDir, failonerror: false)
            ant.mkdir(dir: destLinkDir)

            if (conv.getLinksSrcDirectory()) {
                println "Fetching link files from ${conv.getLinksSrcDirectory()}..."
                // Copy the temp links
                ant.copy(todir: destLinkDir) {
                    fileset(dir: conv.getLinksSrcDirectory()) { include(name: '*.link') }
                }
            } else if (conv.getExtLocations()) {
                println "Generating link files..."
                for (String extLoc : conv.getExtLocations()) {
                    def linkFileName = extLoc.replaceAll("[\\\\/:]", "_")
                    linkFileName = destLinkDir + "/${linkFileName}.link"
                    ant.echo(message: "path=${extLoc}", file: linkFileName)
                    println " -> generated " + linkFileName
                }
            }
        }

        //Create the publish directory
        if (conv.getPublishDirectory()) {
            println "Creating the publication directory..."
            ant.mkdir(dir: conv.getPublishDirectory())
        }
    }



}