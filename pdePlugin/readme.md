This enables users to build Eclipse RCP applications; it is simply a thin wrapper around Eclipse's PDE build. If your project already works with PDE build then this plugin should work out of the box.

To use, build the project and upload it to a repository, then add it to your build script's classpath.

```groovy
buildscript { 
    repositories { 
        // Insert your repository settings here.
    } 
    dependencies {
        classpath ":pdePlugin:8.+" 
    }
}
apply plugin: "pdeBuild"
```

# Configuration
Configuration is done through the 'pdeBuild' extension and has a wide range of properties. Here is an overview of some of them:

```groovy
pdeBuild {
    // These are plugin specific properties
    // Drives which type of build is done; two possible value: feature of product
    type = "feature"

    // If the type is "feature" then this property defines which features to build
    // The feature directory name must match the feature ID in feature.xml
    features = ["FeatureSample"]
    // Otherwise, during a product build you need to provide a product file.
    productFile = "path/to/file.product"

    // The eclipse runtime that will execute the build.
    // This location must contain a plugins folder with the PDE plugin in it
    eclipseLauncher = "/usr/local/eclipse"

    // equinox eclipse launcher to use for launching the org.eclipse.ant.core.antRunner application
    equinoxLauncherPluginVersion = "1.0.201.R35x_v20090715"
    // here you can specify the PDE plugin version to use 
    pdeBuildPluginVersion = "3.5.1.R35x_20090820"
    // NOTE: if you don"t specify versions for equinox and PDE, gradle will use non versioned plugins. 
    // It is recommended to not specify plugin version when using a target platform definition file since this plugin can properly detect the version to use in this case.

    // The following are properties derived from PDE build; these drives the build environment. They are explained in-depth in the PDE build documentation.
    // The target platform to use to build the features (the $base folder MUST contain an "eclipse" folder if baseLocation isn't specified)
    // Can be the same eclipse runtime as eclipseLauncher or a different one, doesn't matter. 
    base = "/usr/local"
    baseLocation = "${base}/eclipse" // This is the default value.

    // The target platform definition file. This file is handled by PDE itself to generate the target platform 
    // at the baseLocation. This requires the eclipseLauncher to be at least 3.8; 4.2 should be fine too.
    targetFile = "${rootDir}/../target/test.target"

    // The working directory. All plugins and features source code will be copied here
    buildDirectory = "${buildDir}/work"

    // The directory that contains the various build files, such as build.properties
    builderDir = "${buildDirectory}/builder" // This is the default value.

    // ID of the build.  Used in naming the build output.
    buildId = "BUILD_ID" // This is the default

    // These properties are key-value pair that are passed to the runtime via the -D parameter.
    // If the key is a property that is in the build.properties file, the value of the file will be overwritten.
    // These can also configure some properties of the plugin (althought this is not recommended): baseLocation, builderDir and buildId.
    additionalProperties = ["arg1" : "value1", "arg2", "value2"]
}
```

# Changelog
* 8.0: Revamp of the project.
    * Migrated to gradle 1.3 minimum. Might work with earlier versions, not tested.
    * Removed duality product/feature, now only one plugin and switch between the two mode is done by configuration.
    * Eased configuration by following gradle's dsl features.
    * Reworked unversionned launcher and pde build: tries its best to detect proper jar and script dir. No extensive testing done, might not always work.
    * Target platform definition file now hanlded by PDE itself using the new pde.provisionTargetDefinition ant task.
    * Reworked how the sources are copied to the build dir: it's now a copy task available from the script to configure as you wish.
    * Lots of internal code cleanup.
* 7.0.2: ant BuildException catching for avoiding ugly stack traces when the PDE build fails
* 7.0.1: full refactoring, .link files generation, target platform cleaning, etc.
* 6.8: use extLocations
* 6.7.2: use CMSClassUnloadingEnabled in place of CMSPermGenSweepingEnabled
* 6.7.1: Fixed bug on dataDirectory
* 6.7: Added javacSource and javacTarget as parameters + eclipse directory
* 6.3->6.4: Fixed internal errors
* 6.2: Added jvmOptions to the ant.java options
* 6.1: Built with gradle-0.9-rc-1
* 6.0: Added a buildpath parameter
* 5.0: Initial changelog
