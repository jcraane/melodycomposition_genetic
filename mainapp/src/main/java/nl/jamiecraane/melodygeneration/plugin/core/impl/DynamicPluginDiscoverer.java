package nl.jamiecraane.melodygeneration.plugin.core.impl;

import nl.jamiecraane.melodygeneration.MelodyFitnessStrategy;
import nl.jamiecraane.melodygeneration.plugin.core.PluginDiscoverer;
import org.apache.log4j.Logger;
import org.jgap.util.JarClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Implementation of the PluginDiscoverer interface which loads all available plugins in the plugins
 * folder.
 */
public final class DynamicPluginDiscoverer implements PluginDiscoverer {
    private static final Logger LOG = Logger.getLogger(DynamicPluginDiscoverer.class);
    private static final String PLUGIN_SEARCH_DIR = "../plugins";
    private static final String JAR_EXTENSION = ".jar";
    private final List<String> pluginClassNames = new ArrayList<String>();
    private final List<Class> plugins = new ArrayList<Class>();
    private final Map<String, String> pluginsToJarMapping = new HashMap<String, String>();

    private DynamicPluginDiscoverer() {
    }

    /**
     * Attempts to create a DynamicPluginDiscoverer.
     * If for some reason an exception occurs during the creating of the DynamicPluginDiscoverer, a
     * StaticPluginDiscoverer is returned instead. The StaticPluginDiscoverer just returns the plugins
     * mentiond in the source code and does no dynamic discovery. An error message is written to the
     * logfile if this occurs.
     * @return an implementation of the PluginDiscoverer interface
     */
    private static PluginDiscoverer createPluginDiscoverer() {
        DynamicPluginDiscoverer pluginDiscoverer = new DynamicPluginDiscoverer();
        File pluginBaseDir = new File(PLUGIN_SEARCH_DIR);
        try {
            pluginDiscoverer.getJarFilesRecursive(pluginBaseDir);
        } catch (IOException e) {
            LOG.error("Unable to create the DynamicPluginDiscoverer see the exception for details. Falling back to the StaticPluginDiscoverer.", e);
            return new StaticPluginDiscoverer();
        }

        return pluginDiscoverer;
    }

    public List<MelodyFitnessStrategy> getAvailablePlugins() {
        List<MelodyFitnessStrategy> plugins = new ArrayList<MelodyFitnessStrategy>();

        Set classNames = this.pluginsToJarMapping.keySet();
        for (Object className : classNames) {
            String name = (String) className;
            JarClassLoader cl = new JarClassLoader(this.pluginsToJarMapping.get(name));
            MelodyFitnessStrategy strategy = null;
            try {
                strategy = (MelodyFitnessStrategy) cl.loadClass(name).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            plugins.add(strategy);
        }        

        return plugins;
    }

    private void getJarFilesRecursive(File basePath) throws IOException {
        File[] files = basePath.listFiles();
        for (File file : files) {
            System.out.println("file = " + file.getCanonicalPath());
            if (file.isDirectory()) {
                this.getJarFilesRecursive(file);
            } else {
                if (this.isJarFile(file)) {
                    this.getPluginsFromJar(file);
                }
            }
        }
    }

    private void getPluginsFromJar(File file) throws IOException {
          JarFile jar = new JarFile(file);
          Enumeration item = jar.entries();
          while (item.hasMoreElements()) {
            JarEntry entry = (JarEntry) item.nextElement();
            String name = entry.getName();
            if (name.toLowerCase().endsWith(".class")) {
              String classname = isPlugin(file.getCanonicalPath(), name);
              if (classname != null) {
                  this.pluginsToJarMapping.put(classname, file.getCanonicalPath());
              }
            }
          }
    }

    /**
   * Checks if a given class matches a given interface
   * @return corrected name of matched class, or null if not matching
   */
  private String isPlugin(String a_jarFilename, String testClass) {
    // remove trailing dots
    if (testClass.toLowerCase().endsWith(".class")) {
      testClass = testClass.substring(0, testClass.length() - 6);
    }
    // replace slashes with dots
    testClass = testClass.replace('\\', '.').replace('/', '.');
    // remove leading dots
    while (testClass.startsWith(".")) {
      testClass = testClass.substring(1);
    }
    if (testClass.indexOf('$') != -1) {
      // don't handle inner/internal classes
      return null;
    }
    try {
      ClassLoader cl;
      if (a_jarFilename == null) {
        cl = getClass().getClassLoader();
      }
      else {
        cl = new JarClassLoader(a_jarFilename);
      }
      Class testClassObj = Class.forName(testClass, false,
          cl);
      if (MelodyFitnessStrategy.class.isAssignableFrom(testClassObj)) {
        if (testClassObj.isInterface()) {
          // no interfaces wanted as result
          return null;
        }
        if ( (testClassObj.getModifiers() & java.lang.reflect.Modifier.ABSTRACT)
            > 0) {
          // no abstract classes wanted as result
          return null;
        }
        return testClass;
      }
    } catch (UnsatisfiedLinkError ignored) {
    } catch (IllegalAccessError ignored) {
    } catch (ClassNotFoundException ignored) {
    } catch (NoClassDefFoundError ignored) {
    }
    return null;
  }

    private boolean isJarFile(File file) {
        return file.getName().toLowerCase().endsWith(JAR_EXTENSION);
    }

    /**
     * For testing purposes.
     * @param args
     */
    public static void main(String[] args) {
        DynamicPluginDiscoverer pluginDiscoverer = (DynamicPluginDiscoverer) DynamicPluginDiscoverer.createPluginDiscoverer();
        for (String pluginClassName : pluginDiscoverer.pluginClassNames) {
            System.out.println("pluginClassName = " + pluginClassName);
        }
        List<MelodyFitnessStrategy> plugins = pluginDiscoverer.getAvailablePlugins();
        for (MelodyFitnessStrategy plugin : plugins) {
            System.out.println("plugin = " + plugin);
        }
    }
}
