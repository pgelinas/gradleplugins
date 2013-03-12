package com.thalesgroup.gradle.pde.tasks

/**
 * @author Pascal Gélinas
 */
class EclipseUtils {
    public static String resolveEquinoxLauncherJarFile(String baseLocation) throws FileNotFoundException {
        File pluginsDir = new File(baseLocation, "plugins")
        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            throw new FileNotFoundException(pluginsDir.toString() + " does not exist.")
        }
        File launcherJar = pluginsDir.listFiles().find {
            it.name.equals("org.eclipse.equinox.launcher.jar") ||
                    it.name.startsWith("org.eclipse.equinox.launcher_") &&
                    it.name.endsWith(".jar")
        }
        if(launcherJar == null)
            throw new FileNotFoundException("Could not find a plugin matching org.eclipse.equinox.launcher")

        return launcherJar.absolutePath
    }
}
